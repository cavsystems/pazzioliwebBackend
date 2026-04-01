package com.pazzioliweb.commonbacken.util;

import java.security.Key;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class Jwcommon {
	 private final String SECRET_KEY = "clave_secreta_clave_secreta_clave_secreta"; // 🔐 mínimo 32 bytes para HS256
	 private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    public Claims extraerClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
}
