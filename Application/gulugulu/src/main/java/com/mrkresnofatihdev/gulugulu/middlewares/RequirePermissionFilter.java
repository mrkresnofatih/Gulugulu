package com.mrkresnofatihdev.gulugulu.middlewares;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mrkresnofatihdev.gulugulu.models.IdentityPermissionGetRequestModel;
import com.mrkresnofatihdev.gulugulu.models.ResponseModel;
import com.mrkresnofatihdev.gulugulu.services.IIdentityPermissionService;
import com.mrkresnofatihdev.gulugulu.utilities.Constants;
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
import java.util.Objects;

@Component
@Order(3)
public class RequirePermissionFilter implements Filter {
    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private Tracer tracer;

    @Autowired
    private IIdentityPermissionService identityPermissionService;

    private final Logger logger = LoggerFactory.getLogger(RequirePermissionFilter.class);

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain filterChain) throws IOException, ServletException {
        logger.info("Starting method: RequirePermissionFilter.doFilter!");
        var httpRequest = (HttpServletRequest) servletRequest;
        var uri = httpRequest.getRequestURI();
        var requiredPermission = _GetRequiredPermission(uri);
        if (Objects.isNull(requiredPermission)) {
            logger.info("No authorization needed, authorization successful!");
            filterChain.doFilter(servletRequest, servletResponse);
            logger.info("Finishing method: RequirePermissionFilter.doFilter");
            return;
        }

        var authHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        var resourceName = httpRequest.getHeader(Constants.Http.Headers.ResourceName);
        var authHeaderIsNullOrEmptyOrShort = Objects.isNull(authHeader) || !authHeader.startsWith("Bearer ");
        if (Objects.isNull(resourceName)) logger.error("ResourceName is null!");
        if (!authHeaderIsNullOrEmptyOrShort && !Objects.isNull(resourceName)) {
            var token = authHeader.substring(7);
            var tokenIsNullOrEmpty = token.trim().equals("");
            if (!tokenIsNullOrEmpty) {
                var tokenClaimsResponse = jwtHelper.GetClaims(token);
                if (Objects.isNull(tokenClaimsResponse.getErrorMessage())) {
                    var identityName = tokenClaimsResponse.getData().get(Constants.Jwt.Claim.IdentityName).asString();
                    var hasPermission = _HasRequiredPermission(
                            requiredPermission,
                            identityName,
                            resourceName,
                            uri
                    );
                    if (hasPermission) {
                        filterChain.doFilter(servletRequest, servletResponse);
                        logger.info("Finishing Method: RequirePermissionFilter.doFilter!");
                        return;
                    }
                    logger.info("doesn't have required permission");
                } else {
                    logger.warn("get claims of Token is invalid: possible unhandled exception in JWT verification!");
                }
            }
        }

        logger.error("Authorization Failed!");
        var httpResponse = (HttpServletResponse) servletResponse;

        httpResponse.setStatus(HttpStatus.FORBIDDEN.value());
        httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        var mapper = new ObjectMapper();
        var traceId = tracer.currentSpan().context().traceId();
        var returnErrorMessage = String.format("Authorization Error! | Corr: %s", traceId);
        var errorResponse = new ResponseModel<>(null, returnErrorMessage);
        httpResponse.getWriter().write(mapper.writeValueAsString(errorResponse));
    }

    private String _GetRequiredPermission(String uri) {
        for (var uriPrefix : Constants.PermissionProtectedURIPrefixes.keySet()) {
            if (uri.startsWith(uriPrefix)) {
                return Constants.PermissionProtectedURIPrefixes.get(uriPrefix);
            }
        }
        return null;
    }

    private boolean _HasRequiredPermission(
            String requiredPermission,
            String identityName,
            String resourceName,
            String httpUri) {
        try {
            var resourceNameFormatBasedOnHttpUri = Constants
                    .URIPrefixResourceNamePrefixMap
                    .get(Constants
                            .URIPrefixResourceNamePrefixMap
                            .keySet()
                            .stream()
                            .map(s -> s.replace("%s", ""))
                            .filter(httpUri::startsWith)
                            .findFirst()
                            .orElseThrow());
            var isResourceNameFormatCorrect = resourceName
                    .startsWith(resourceNameFormatBasedOnHttpUri.replace("%s", ""));
            if (!isResourceNameFormatCorrect) {
                logger.error(String.format("resourceName format is incorrect => required: %s, provided: %s", resourceNameFormatBasedOnHttpUri, resourceName));
                return false;
            }
            var identityPermissionResponse = identityPermissionService
                    .GetPermission(new IdentityPermissionGetRequestModel(requiredPermission, identityName));
            var permittedResourceNames = identityPermissionResponse.getResourceNames();
            if (permittedResourceNames
                    .stream()
                    .filter(s -> s.contains("*"))
                    .map(s -> s.split("\\*")[0])
                    .anyMatch(s -> String.format("!%s", resourceName).startsWith(s))) {
                return false;
            }
            if (permittedResourceNames.contains(String.format("!%s", resourceName))) {
                return false;
            }
            if (permittedResourceNames.contains(resourceName)) {
                return true;
            }
            return permittedResourceNames
                    .stream()
                    .filter(s -> s.contains("*"))
                    .map(s -> s.split("\\*")[0])
                    .anyMatch(resourceName::startsWith);
        }
        catch (Exception e) {
            logger.error(e.toString());
            logger.error("_HasRequiredPermission thrown an error, possibly doesn't have the required permission");
            return false;
        }
    }
}
