package com.pazzioliweb.commonbacken.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


import com.pazzioliweb.commonbacken.conexiondb.TenantContext;
import com.pazzioliweb.commonbacken.dtos.DatosSesiones;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TenantInterceptor implements HandlerInterceptor {
	@Autowired
	  private RedisTemplate<String, DatosSesiones> redisTemplate;
	    @Autowired
	    private  Jwcommon  jwtUtil;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String tenant = request.getHeader("X-TenantID"); // o desde request.getParameter("db")
        System.out.println("teant actual "+request.getHeader("X-TenantID"));
        if (tenant != null && !tenant.isEmpty()) {
            TenantContext.setCurrentTenant(tenant);
        } else {
        	
        	  String token = null;
                 System.out.println("cookie"+request.getCookies());
              // ✅ Buscar cookie con nombre "token"
              if (request.getCookies() != null) {
                  for (Cookie cookie : request.getCookies()) {
                      if ("token".equals(cookie.getName())) {
                          token = cookie.getValue();
                         
                          break;
                      }
                  }
                  if(token==null) {
                	  TenantContext.clear(); 
                	  System.out.println("cookie"+token);
                  }else {
                	  System.out.println("cookiee"+token);
                	  Claims claims = jwtUtil.extraerClaims(token);
                	   String db = claims.get("idsecion", String.class);
                	   
                	   
                	   DatosSesiones datos = redisTemplate.opsForValue().get( db);
                	
                	   TenantContext.setCurrentTenant(datos.getDbName());
                  }
              }else {
            	    TenantContext.clear(); 
              }
        // Limpia si no viene nada
        }
        return true;
    }
}
