package com.loopon.notification.infrastructure.apns;

import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.io.InputStreamReader;
import java.security.PrivateKey;
import java.time.Instant;
import java.util.Date;

@Component
public class APNsTokenProvider {

    @Value("${apns.team-id}")
    private String teamId;

    @Value("${apns.key-id}")
    private String keyId;

    @Value("${apns.p8-path}")
    private String p8Path;

    private PrivateKey privateKey;

    @PostConstruct
    public void init() throws IOException {
        ClassPathResource resource = new ClassPathResource(p8Path);

        try (PEMParser pemParser = new PEMParser(new InputStreamReader(resource.getInputStream()))) {
            PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();
            this.privateKey = new JcaPEMKeyConverter().getPrivateKey(privateKeyInfo);
        }
    }

    public String generateToken() {
        Instant now = Instant.now();

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