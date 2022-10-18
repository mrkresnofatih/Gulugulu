package com.mrkresnofatihdev.gulugulu.middlewares;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrkresnofatihdev.gulugulu.models.ResponseModel;
import com.mrkresnofatihdev.gulugulu.utilities.JwtHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Order(2)
public class RequireAuthenticationFilter implements Filter {
    @Autowired
    private JwtHelper jwtHelper;
    @Autowired
    private Tracer tracer;
    private final Logger logger = LoggerFactory.getLogger(RequireAuthenticationFilter.class);

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain filterChain) throws IOException, ServletException {
        logger.info("Starting Method: RequireAuthenticationFilter.doFilter!");
        var httpRequest = (HttpServletRequest) servletRequest;
        var uri = httpRequest.getRequestURI();
        if (!_MustGoThruAuthentication(uri)) {
            logger.info("No authentication needed, auth successful!");
            filterChain.doFilter(servletRequest, servletResponse);
            logger.info("Finishing Method: RequireAuthenticationFilter.doFilter!");
            return;
        }

        var authHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        var authHeaderIsNullOrEmptyOrShort = Objects.isNull(authHeader) || !authHeader.startsWith("Bearer ");
        if (!authHeaderIsNullOrEmptyOrShort) {
            var token = authHeader.substring(7);
            var tokenIsNullOrEmpty = token.trim().equals("");
            if (!tokenIsNullOrEmpty) {
                var tokenValidityResponse = jwtHelper.VerifyToken(token);
                if (Objects.isNull(tokenValidityResponse.getErrorMessage())) {
                    filterChain.doFilter(servletRequest, servletResponse);
                    logger.info("Finishing Method: RequireAuthenticationFilter.doFilter!");
                    return;
                }
                logger.warn("Token is expired, invalid, or unhandled exception in JWT verification!");
            }
        }

        logger.error("Authentication Failed!");
        var httpResponse = (HttpServletResponse) servletResponse;

        httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        var mapper = new ObjectMapper();
        var traceId = tracer.currentSpan().context().traceId();
        var returnErrorMessage = String.format("Authentication Error! | Corr: %s", traceId);
        var errorResponse = new ResponseModel<>(null, returnErrorMessage);
        httpResponse.getWriter().write(mapper.writeValueAsString(errorResponse));
    }

    private boolean _MustGoThruAuthentication(String uri) {
        var authProtectedURIPrefixes = _GetAuthProtectedURIPrefixes();
        for(var uriPrefix : authProtectedURIPrefixes) {
            if (uri.startsWith(uriPrefix)) {
                return true;
            }
        }
        return false;
    }

    private List<String> _GetAuthProtectedURIPrefixes() {
        var list = new ArrayList<String>();
        list.add("/api/v1/user-profile");
        list.add("/api/v1/user-credentials");
        list.add("/api/v1/friend");
        list.add("/api/v1/notification");
        return list;
    }
}
