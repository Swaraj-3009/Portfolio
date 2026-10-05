package com.portfolio.Servlets.AdminServlet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class AddMyProjectServletTest {

    @Test
    void shouldAllowProjectCreationWithoutId() {
        assertDoesNotThrow(() -> AddMyProjectServlet.parseOptionalId(""));
        assertNull(AddMyProjectServlet.parseOptionalId(""));
        assertNull(AddMyProjectServlet.parseOptionalId(null));
    }
}
