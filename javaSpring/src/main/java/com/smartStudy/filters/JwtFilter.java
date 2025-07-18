/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartStudy.filters;

import com.smartStudy.untils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 *
 * @author AN515-57
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        System.out.println("====== JwtFilter: Processing URL: " + uri);

        String header = request.getHeader("Authorization");
        if (header == null) {
            header = request.getHeader("authorization"); // fallback (trường hợp proxy biến đổi)
        }

        System.out.println("====== JwtFilter - Authorization Header: " + header);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                String username = JwtUtils.validateTokenAndGetUsername(token);
                if (username != null) {
                    // Bạn có thể load roles từ DB, tạm hardcode ROLE_ADMIN để đảm bảo hoạt động
                    List<SimpleGrantedAuthority> authorities = List.of(
                            new SimpleGrantedAuthority("ROLE_ADMIN")
                    );

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    filterChain.doFilter(request, response);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace(); // log lỗi token nếu cần
            }

            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token không hợp lệ hoặc đã hết hạn.");
            return;
        }

        // Nếu không có token và đang truy cập /api/secure → trả lỗi
        if (uri.startsWith(request.getContextPath() + "/api/secure")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Thiếu header Authorization.");
            return;
        }

        // Cho phép các URL khác tiếp tục
        filterChain.doFilter(request, response);
    }
}

