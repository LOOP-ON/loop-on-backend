package com.loopon.auth.infrastructure.apple;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.PrivateKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class AppleClientSecretGenerator {
    @Value("${apple.team-id}")
    private String teamId;

    @Value("${apple.key-id}")
    private String keyId;

    @Value("${apple.client-id}")
    private String clientId;

    @Value("${apple.key-path}")
    private String keyPath;

    @Value("${apple.url}")
    private String url;

    private static final long EXPIRATION_MILLIS = 1000L * 60L * 5L;

    public String createClientSecret() {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_MILLIS);

        return Jwts.builder()
                .header().keyId(keyId).and()
                .issuer(teamId)
                .audience().add(url).and()
                .subject(clientId)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getPrivateKey(), Jwts.SIG.ES256)
                .compact();
    }

    private PrivateKey getPrivateKey() {
        try {
            ClassPathResource resource = new ClassPathResource(keyPath);
            InputStreamReader reader = new InputStreamReader(resource.getInputStream());

            try (PEMParser pemParser = new PEMParser(reader)) {
                PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();
                JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
                return converter.getPrivateKey(privateKeyInfo);
            }
        } catch (IOException e) {
            throw new RuntimeException("Apple Private Key 로드 실패", e);
        }
    }
}
