package ru.taranenkoant.ant.db.query;

import java.io.Closeable;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * {@code @author:} TaranenkoAnt
 * {@code @createDate:} 30.12.2023
 */
public interface Query extends Closeable {

    ResultSet execute() throws SQLException;
}
