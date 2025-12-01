package com.example.kromannreumert.security.JwtUtil;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
public class JwtGenerator {

    private final RSAPrivateKey privateKey;

    public JwtGenerator(@Value("${jwt.private-key}") RSAPrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public String issueToken(String username, List<String> roles) throws Exception {
        Instant now = Instant.now();

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(username)
                .claim("roles", roles)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(now.plusSeconds(3600)))
                .build();

        JWSHeader header = new JWSHeader(JWSAlgorithm.RS256);
        SignedJWT jwt = new SignedJWT(header, claims);

        jwt.sign(new RSASSASigner(privateKey));

        return jwt.serialize();
    }
}
