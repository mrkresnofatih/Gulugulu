package com.mrkresnofatihdev.gulugulu.middlewares;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrkresnofatihdev.gulugulu.models.ResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(1)
public class ApplicationExceptionFilter implements Filter {
    @Autowired
    private Tracer tracer;

    private final Logger logger = LoggerFactory.getLogger(ApplicationExceptionFilter.class);

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain filterChain) throws IOException, ServletException {
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            logger.error(e.toString());
            var httpResponse = (HttpServletResponse) servletResponse;

            httpResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            var mapper = new ObjectMapper();
            var traceId = tracer.currentSpan().context().traceId();
            var returnErrorMessage = String.format("Bad Request! | Corr: %s", traceId);
            var errorResponse = new ResponseModel<>(null, returnErrorMessage);
            httpResponse.getWriter().write(mapper.writeValueAsString(errorResponse));
        }
    }
}
