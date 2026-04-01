package com.pazzioliweb.authbacken.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.pazzioliweb.commonbacken.conexiondb.TenantContext;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TenantFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String tenant = request.getHeader("X-TenantID");
        if (tenant != null) {
            TenantContext.setCurrentTenant(tenant);
        }
        filterChain.doFilter(request, response);
    }
}

