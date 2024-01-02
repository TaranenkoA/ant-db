package ru.taranenkoant.ant.db.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@code @author:} TaranenkoAnt
 * {@code @createDate:} 02.01.2024
 */
public class FunctionPostgresQueryImpl implements Query {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final List<Parameter> parameters = new ArrayList<>();
    private PreparedStatement preparedStatement;
    private int cursorIndex;
    public ResultSet execute(Connection connection, String query, Map<String, Object> parameterValues) throws SQLException {
        boolean autoCommit = connection.getAutoCommit();
        logger.info("auto-commit is enabled :{}", autoCommit);
        if (autoCommit) {
            connection.setAutoCommit(false);
        }

        fillParameters(query, parameterValues);
        query = prepareQuery(query);

        try {
            CallableStatement callableStatement = connection.prepareCall(query);
            this.preparedStatement = callableStatement;
            bindParameters();
            if (cursorIndex > 0) {
                callableStatement.registerOutParameter(cursorIndex, Types.OTHER);
            }
            callableStatement.execute();
            ResultSet rs = (ResultSet) callableStatement.getObject(cursorIndex);
            if (autoCommit) {
                connection.commit();
            }
            return rs;
        } finally {
            if (autoCommit) {
                connection.setAutoCommit(true);
            }
        }
    }

    private void fillParameters(String queryStr, Map<String, Object> parameterValues) {
        parameters.clear();
        int cursorIndex = -1;
        Matcher matcher = Pattern.compile(":(?<PARAM>\\w+)|'[DdMmYyHh0-9IiSs_\\W]+'").matcher(queryStr);
        int index = 0;
        int cursorPosition = queryStr.indexOf('?');
        while (matcher.find()) {
            if (cursorIndex == -1 && cursorPosition >= 0 && matcher.start() > cursorPosition) {
                cursorIndex = ++index;
            }
            String key = matcher.group(1);
            if (key == null) {
                continue;
            }
            Object value = parameterValues.get(key);
            parameters.add(new Parameter(++index, key, value));
        }

        if (cursorIndex == -1 && cursorPosition >= 0) {
            cursorIndex = parameters.size() + 1;
        }
        this.cursorIndex = cursorIndex;
    }

    private String prepareQuery(String queryStr) {
        Matcher matcher = Pattern.compile(":(?<PARAM>\\w+)|'[DdMmYyHh0-9IiSs_\\W]+'").matcher(queryStr);
        while (matcher.find()) {
            String group = matcher.group(1);
            if (group == null) {
                continue;
            }
            queryStr = queryStr.replaceAll(":" + group, "?");
        }
        return queryStr;
    }

    private void bindParameters() throws SQLException {
        for (Parameter parameter : parameters) {
            Object value = parameter.getValue();
            int index = parameter.getIndex();
            if (value == null || "".equals(value))
                preparedStatement.setObject(index, null);
            else if (value instanceof String)
                preparedStatement.setString(index, (String) value);
            else if (value instanceof BigDecimal)
                preparedStatement.setBigDecimal(index, (BigDecimal) value);
            else if (value instanceof Double || value instanceof Float)
                preparedStatement.setBigDecimal(index, BigDecimal.valueOf(((Number) value).doubleValue()));
            else if (value instanceof Number)
                preparedStatement.setLong(index, ((Number) value).longValue());
            else if (value instanceof Date)
                preparedStatement.setTimestamp(index, Timestamp.from(((Date) value).toInstant()));
            else if (value instanceof byte[])
                preparedStatement.setBinaryStream(index, new ByteArrayInputStream((byte[]) value));
            else if (value instanceof ByteArrayInputStream)
                preparedStatement.setBinaryStream(index, (InputStream) value);
            else
                throw new IllegalArgumentException("Unused type: " + value.getClass());
        }
    }

    @Override
    public void close() {
        try {
            preparedStatement.close();
        } catch (SQLException e) {
            logger.error("", e);
            throw new RuntimeException(e);
        }
    }
}
