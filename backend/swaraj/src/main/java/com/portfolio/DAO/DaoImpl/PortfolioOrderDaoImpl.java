package com.portfolio.DAO.DaoImpl;

//AI GENERATED CODE

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.ToIntFunction;

import com.portfolio.config.DatabaseConnection;

/** Persists the owner-defined display order for portfolio collection records. */
public class PortfolioOrderDaoImpl {
    private static final Set<String> SECTIONS = Set.of("skills", "projects", "education");

    public <T> List<T> applyOrder(String section, List<T> values, ToIntFunction<T> idOf) {
        if (values == null || values.size() < 2 || !SECTIONS.contains(section)) return values;

        try (Connection connection = DatabaseConnection.getConnection()) {
            List<Integer> orderedIds = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT item_id FROM portfolio_item_order WHERE section_key = ? ORDER BY sort_order ASC")) {
                statement.setString(1, section);
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) orderedIds.add(result.getInt("item_id"));
                }
            }

            if (orderedIds.isEmpty()) return values;
            Map<Integer, T> byId = new HashMap<>();
            for (T value : values) byId.put(idOf.applyAsInt(value), value);
            List<T> ordered = new ArrayList<>(values.size());
            Set<Integer> included = new HashSet<>();
            for (Integer id : orderedIds) {
                T value = byId.get(id);
                if (value != null && included.add(id)) ordered.add(value);
            }
            for (T value : values) {
                int id = idOf.applyAsInt(value);
                if (included.add(id)) ordered.add(value);
            }
            return ordered;
        } catch (SQLException e) {
            // Before the owner first saves a custom order, the settings table may not exist yet.
            if (e.getErrorCode() == 1146 || "42S02".equals(e.getSQLState())) return values;
            e.printStackTrace();
            return values;
        } catch (Exception e) {
            e.printStackTrace();
            return values;
        }
    }

    public boolean saveOrder(String section, List<Integer> ids) {
        if (!SECTIONS.contains(section) || ids == null) return false;
        try (Connection connection = DatabaseConnection.getConnection()) {
            ensureTable(connection);
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement delete = connection.prepareStatement(
                        "DELETE FROM portfolio_item_order WHERE section_key = ?")) {
                    delete.setString(1, section);
                    delete.executeUpdate();
                }
                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO portfolio_item_order (section_key, item_id, sort_order) VALUES (?, ?, ?)")) {
                    for (int position = 0; position < ids.size(); position++) {
                        insert.setString(1, section);
                        insert.setInt(2, ids.get(position));
                        insert.setInt(3, position);
                        insert.addBatch();
                    }
                    insert.executeBatch();
                }
                connection.commit();
                return true;
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void ensureTable(Connection connection) throws Exception {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS portfolio_item_order (
                    section_key VARCHAR(24) NOT NULL,
                    item_id INT NOT NULL,
                    sort_order INT NOT NULL,
                    PRIMARY KEY (section_key, item_id),
                    KEY idx_portfolio_item_order (section_key, sort_order)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
                """);
        }
    }
}
