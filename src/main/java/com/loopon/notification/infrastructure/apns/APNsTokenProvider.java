package com.loopon.notification.infrastructure.apns;

import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.PrivateKey;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class APNsTokenProvider {

    @Value("${apple.team-id}")
    private String teamId;

    @Value("${apple.key-id}")
    private String keyId;

    @Value("${apple.key-path}")
    private Resource p8Resource;

    private PrivateKey privateKey;

    private volatile String cachedToken;
    private volatile Instant cachedAt;

    //55분 기준으로 갱신
    private static final long REFRESH_AFTER_SECONDS = 55L * 60L;

    private final ReentrantLock refreshLock = new ReentrantLock();

    @PostConstruct
    public void init() throws IOException {
        try (PEMParser pemParser =
                     new PEMParser(new InputStreamReader(p8Resource.getInputStream()))) {

            Object obj = pemParser.readObject();

            PrivateKeyInfo privateKeyInfo;
            if (obj instanceof PrivateKeyInfo pki) {
                privateKeyInfo = pki;
            } else if (obj instanceof org.bouncycastle.openssl.PEMKeyPair kp) {
                privateKeyInfo = kp.getPrivateKeyInfo();
            } else {
                throw new IllegalStateException("Unsupported PEM object: " + (obj == null ? "null" : obj.getClass()));
            }

            this.privateKey = new JcaPEMKeyConverter().getPrivateKey(privateKeyInfo);
        }
    }

    public String getToken() {
        //캐시가 있고 아직 갱신 필요 없으면 그대로 반환
        Instant now = Instant.now();
        if (isCacheValid(now)) {
            return cachedToken;
        }

        //갱신 필요 → 동시성 제어
        refreshLock.lock();
        try {
            // lock 잡고 다시 확인 (double-check)
            now = Instant.now();
            if (isCacheValid(now)) {
                return cachedToken;
            }

            String newToken = generateToken(now);
            cachedToken = newToken;
            cachedAt = now;
            return newToken;

        } finally {
            refreshLock.unlock();
        }
    }

    private boolean isCacheValid(Instant now) {
        if (cachedToken == null || cachedAt == null) return false;
        long ageSeconds = now.getEpochSecond() - cachedAt.getEpochSecond();
        return ageSeconds >= 0 && ageSeconds < REFRESH_AFTER_SECONDS;
    }

    //실제 토큰 생성
    private String generateToken(Instant now) {
        if (privateKey == null) {
            throw new IllegalStateException("APNs privateKey not initialized. Check p8 path and init()");
        }

        return Jwts.builder()
                .header()
                .add("alg", "ES256")
                .add("kid", keyId)
                .and()
                .issuer(teamId)
                .issuedAt(Date.from(now))
                .signWith(privateKey, Jwts.SIG.ES256)
                .compact();
    }
}
