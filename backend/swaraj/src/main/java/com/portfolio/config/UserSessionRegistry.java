package com.portfolio.config;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.portfolio.model.Users;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/** Tracks active sessions so an administrator can revoke access after account removal. */
@WebListener
public class UserSessionRegistry implements HttpSessionListener {
    private static final Set<HttpSession> ACTIVE_SESSIONS = ConcurrentHashMap.newKeySet();

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        ACTIVE_SESSIONS.add(event.getSession());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        ACTIVE_SESSIONS.remove(event.getSession());
    }

    public static void invalidateUserSessions(String username) {
        if (username == null || username.isBlank()) return;

        for (HttpSession session : ACTIVE_SESSIONS) {
            try {
                Object value = session.getAttribute("loggedInUser");
                if (value instanceof Users user && username.equals(user.getUsername())) {
                    session.invalidate();
                }
            } catch (IllegalStateException ignored) {
                // Another request or the container already invalidated this session.
            }
        }
    }
}
