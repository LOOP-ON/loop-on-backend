package com.loopon.auth.infrastructure.apple;

import com.loopon.global.domain.ErrorCode;
import com.loopon.global.exception.BusinessException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.PrivateKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppleClientSecretGenerator {
    @Value("${apple.team-id}")
    private String teamId;

    @Value("${apple.key-id}")
    private String keyId;

    @Value("${apple.client-id}")
    private String clientId;

    @Value("${apple.key-path}")
    private Resource privateKeyResource;

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
        try (InputStreamReader reader = new InputStreamReader(privateKeyResource.getInputStream());
             PEMParser pemParser = new PEMParser(reader)) {

            PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            return converter.getPrivateKey(privateKeyInfo);

        } catch (IOException e) {
            log.error("Apple Private Key 로드 실패. 경로: {}", privateKeyResource, e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
