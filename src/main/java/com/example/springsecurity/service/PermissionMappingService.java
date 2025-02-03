package com.example.springsecurity.service;

import com.example.springsecurity.util.RequiresPermission;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Map;

@Component
public class PermissionMappingService {
    private final ApplicationContext applicationContext;

    public PermissionMappingService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public String[] getRequiredPermission(String method, String path) {
        Map<String, Object> controllers = applicationContext.getBeansWithAnnotation(RestController.class);

        for (Object controller : controllers.values()) {
            Class<?> clazz = controller.getClass();

            for (Method m : clazz.getDeclaredMethods()) {
                if (matchesRequest(m, method, path)) {
                    RequiresPermission annotation = m.getAnnotation(RequiresPermission.class);
                    return annotation != null ? annotation.value() : null;
                }
            }
        }
        return null; // No permission required
    }

    private boolean matchesRequest(Method method, String httpMethod, String requestPath) {
        String basePath = "";

        RequestMapping classMapping = method.getDeclaringClass().getAnnotation(RequestMapping.class);
        if (classMapping != null && classMapping.value().length > 0) {
            basePath = classMapping.value()[0]; // Controller-level base path
        }

        String fullPath = basePath;
        if (method.isAnnotationPresent(GetMapping.class) && httpMethod.equals("GET")) {
            fullPath += (method.getAnnotation(GetMapping.class).value()).length > 0 ? method.getAnnotation(GetMapping.class).value()[0] : "";
        } else if (method.isAnnotationPresent(PostMapping.class) && httpMethod.equals("POST")) {
            fullPath += method.getAnnotation(PostMapping.class).value()[0];
        } else if (method.isAnnotationPresent(PutMapping.class) && httpMethod.equals("PUT")) {
            fullPath += method.getAnnotation(PutMapping.class).value()[0];
        } else if (method.isAnnotationPresent(DeleteMapping.class) && httpMethod.equals("DELETE")) {
            fullPath += method.getAnnotation(DeleteMapping.class).value()[0];
        } else {
            return false;
        }

        return pathMatches(fullPath, requestPath);
    }

    private boolean pathMatches(String registeredPath, String requestPath) {
        // Replace numbers and UUIDs in registered paths with {id}
        String normalizedRegisteredPath = registeredPath.replaceAll("/\\d+", "/{id}").replaceAll("/[a-f0-9\\-]{36}", "/{id}");
        String normalizedRequestPath = requestPath.replaceAll("/\\d+", "/{id}").replaceAll("/[a-f0-9\\-]{36}", "/{id}");

        return normalizedRegisteredPath.equals(normalizedRequestPath);
    }
}

