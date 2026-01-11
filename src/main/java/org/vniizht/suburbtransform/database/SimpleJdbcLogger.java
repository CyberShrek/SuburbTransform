package org.vniizht.suburbtransform.database;

import org.vniizht.suburbtransform.util.Resources;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SimpleJdbcLogger {

    private static final Connection connection;

    static {
        try {
            connection = ConnectionPool.getLoggerConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Map<String, Object>> queryForMatrix(String queryId, Map<String, Object> params) throws Exception {
        return SimpleJdbc.queryForMatrix(queryId, params, connection);
    }
    public static ResultSet query(String queryId, Map<String, Object> params) throws Exception {
        return SimpleJdbc.query(queryId, params, connection);
    }
    public static void update(String queryId, Map<String, Object> params) throws Exception {
        SimpleJdbc.update(queryId, params, connection);
    }
}
