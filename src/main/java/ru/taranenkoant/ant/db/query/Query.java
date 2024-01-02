package ru.taranenkoant.ant.db.query;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * {@code @author:} TaranenkoAnt
 * {@code @createDate:} 30.12.2023
 */
public interface Query extends Closeable {

    ResultSet execute(Connection connection, String queryStr, Map<String, Object> parameterValues) throws SQLException;
}
