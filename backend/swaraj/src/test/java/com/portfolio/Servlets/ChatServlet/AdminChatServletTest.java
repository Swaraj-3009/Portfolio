package com.portfolio.Servlets.ChatServlet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

class AdminChatServletTest {
    @Test
    void shouldRequireAuthentication() throws Exception {
        AtomicInteger status = new AtomicInteger();
        new AdminChatServlet().doGet(request(null), response(status));
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, status.get());
    }

    @Test
    void shouldRejectRegularUsers() throws Exception {
        HttpSession session = proxy(HttpSession.class, (method, arguments) -> {
            if("getAttribute".equals(method.getName()) && "loggedInUser".equals(arguments[0])){
                return new Object();
            }
            return null;
        });
        AtomicInteger status = new AtomicInteger();
        new AdminChatServlet().doGet(request(session), response(status));
        assertEquals(HttpServletResponse.SC_FORBIDDEN, status.get());
    }

    private HttpServletRequest request(HttpSession session) {
        return proxy(HttpServletRequest.class, (method, arguments) -> {
            if("getSession".equals(method.getName())){
                return session;
            }
            return defaultValue(method.getReturnType());
        });
    }

    private HttpServletResponse response(AtomicInteger status) {
        return proxy(HttpServletResponse.class, (method, arguments) -> {
            if("sendError".equals(method.getName())){
                status.set((Integer) arguments[0]);
            }
            return defaultValue(method.getReturnType());
        });
    }

    @SuppressWarnings("unchecked")
    private <T> T proxy(Class<T> type, ProxyCall call) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (instance, method, arguments) -> call.invoke(method, arguments));
    }

    private Object defaultValue(Class<?> type) {
        if(!type.isPrimitive()) return null;
        if(type == boolean.class) return false;
        if(type == char.class) return '\0';
        if(type == byte.class) return (byte) 0;
        if(type == short.class) return (short) 0;
        if(type == int.class) return 0;
        if(type == long.class) return 0L;
        if(type == float.class) return 0f;
        return 0d;
    }

    @FunctionalInterface
    private interface ProxyCall {
        Object invoke(Method method, Object[] arguments) throws Throwable;
    }
}
