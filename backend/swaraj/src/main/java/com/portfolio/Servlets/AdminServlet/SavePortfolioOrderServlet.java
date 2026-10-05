package com.portfolio.Servlets.AdminServlet;

//AI GENERATED CODE

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.portfolio.DAO.DaoImpl.PortfolioOrderDaoImpl;
import com.portfolio.Service.BothService;
import com.portfolio.model.Admin;
import com.portfolio.model.MyEducation;
import com.portfolio.model.MyProject;
import com.portfolio.model.MySkills;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/portfolioOrder")
public class SavePortfolioOrderServlet extends HttpServlet {
    private final BothService bothService = new BothService();
    private final PortfolioOrderDaoImpl orderDao = new PortfolioOrderDaoImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        Admin admin = session == null ? null : (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Admin session expired. Sign in again.");
            return;
        }

        String section = request.getParameter("section");
        List<Integer> requestedIds;
        List<Integer> currentIds;
        try {
            requestedIds = parseIds(request.getParameter("ids"));
            currentIds = currentIdsFor(section);
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(e.getMessage());
            return;
        }

        if (currentIds == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Could not verify current portfolio items.");
            return;
        }
        if (requestedIds.size() != currentIds.size()
                || !new HashSet<>(requestedIds).equals(new HashSet<>(currentIds))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Order must contain each current item exactly once.");
            return;
        }

        if (orderDao.saveOrder(section, requestedIds)) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Portfolio order saved.");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Could not save portfolio order.");
        }
    }

    private List<Integer> parseIds(String value) {
        if (value == null || value.isBlank()) return List.of();
        List<Integer> ids = new ArrayList<>();
        Set<Integer> unique = new HashSet<>();
        for (String part : value.split(",")) {
            try {
                int id = Integer.parseInt(part.trim());
                if (id <= 0 || !unique.add(id)) throw new IllegalArgumentException("Order contains an invalid or repeated item.");
                ids.add(id);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Order contains an invalid item id.");
            }
        }
        return ids;
    }

    private List<Integer> currentIdsFor(String section) {
        if ("skills".equals(section)) {
            List<MySkills> items = bothService.showMySkill();
            return items == null ? null : items.stream().map(MySkills::getId).collect(Collectors.toList());
        }
        if ("projects".equals(section)) {
            List<MyProject> items = bothService.showMyProject();
            return items == null ? null : items.stream().map(MyProject::getId).collect(Collectors.toList());
        }
        if ("education".equals(section)) {
            List<MyEducation> items = bothService.showMyEducation();
            return items == null ? null : items.stream().map(MyEducation::getId).collect(Collectors.toList());
        }
        throw new IllegalArgumentException("Choose skills, projects, or education.");
    }
}
