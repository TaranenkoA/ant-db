package ru.taranenkoant.ant.db.query;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * {@code @author:} TaranenkoAnt
 * {@code @createDate:} 30.12.2023
 */
public interface QueryFactory {

    Query createQuery(Connection conn, String sql, HashMap<String, Object> param) throws SQLException;
}
