package org.vniizht.suburbtransform.database;

import lombok.SneakyThrows;
import org.vniizht.suburbtransform.util.Log;
import org.vniizht.suburbtransform.util.Resources;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

public class SimpleJdbc {

    private static final Connection connection;

    static {
        try {
            connection = ConnectionPool.getConnection();
            connection.setAutoCommit(false);
            connection.commit();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Map<String, Object>> queryForMatrix(String queryId) {
        return queryForMatrix(queryId, null);
    }
    public static List<Map<String, Object>> queryForMatrix(String queryId, Map<String, Object> params) {
        return queryForMatrix(queryId, params, connection);
    }
    static List<Map<String, Object>> queryForMatrix(String queryId, Map<String, Object> params, Connection connection) {
        return resultSetToMatrix(query(queryId, params, connection));
    }

    public static <T> List<T> queryForObjects(String queryId, Class<T> clasS) {
        return queryForObjects(queryId, null, clasS);
    }
    public static <T> List<T> queryForObjects(String queryId, Map<String, Object> params, Class<T> clasS) {
        List<Map<String, Object>> rows = queryForMatrix(queryId, params);
        List<T> objects = new ArrayList<>();
        try {
            for (Map<String, Object> row : rows) {
                T obj = clasS.newInstance();
                for (Field field : clasS.getFields()) {
                    field.set(obj, row.get(field.getName()));
                }
                objects.add(obj);
            }
        }
        catch (Exception e) {
            throw new JdbcException(e);
        }
        return objects;
    }

    public static ResultSet query(String queryId) {
        return query(queryId, null);
    }
    public static ResultSet query(String queryId, Map<String, Object> params) {
        return query(queryId, params, connection);
    }
    static ResultSet query(String queryId, Map<String, Object> params, Connection connection) {
        try {
            return prepareStatement(queryId, params, connection)
                    .executeQuery();
        }
        catch (SQLException e) {
            throw new JdbcException(e);
        }
    }

    public static void update(String queryId, Map<String, Object> params) {
        update(queryId, params, connection);
    }
    static void update(String queryId, Map<String, Object> params, Connection connection) {
        try {
            prepareStatement(queryId, params, connection)
                    .executeUpdate();
        } catch (SQLException e) {
            throw new JdbcException(e);
        }
    }

    public static <T> void batchUpdate(String queryId, List<T> paramsList, Function<T, Map<String, Object>> paramsMapper, int batchSize, Log log) {
        try {
            List<String> paramNames = new ArrayList<>();
            PreparedStatement statement = connection.prepareStatement(
                    replaceParams(Resources.load(getQueryPath(queryId)), paramNames)
            );
            for (int i = 0; i < paramsList.size(); i++) {
                setParams(statement, paramNames, paramsMapper.apply(paramsList.get(i)));
                statement.addBatch();
                if (i % batchSize == 0 || i == paramsList.size() - 1) {
                    statement.executeBatch();
                    log.reline(i + " из " + paramsList.size());
                }
            }
            log.nextLine();
        }
        catch (Exception e) {
            throw new JdbcException(e);
        }
    }

    private static PreparedStatement prepareStatement(String queryId, Map<String, Object> params, Connection connection) {
        try {
            List<String> paramNames = new ArrayList<>();
            PreparedStatement statement = connection.prepareStatement(
                    replaceParams(Resources.load(getQueryPath(queryId)), paramNames),
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            );
            setParams(statement, paramNames, params);

            return statement;
        }
        catch (Exception e) {
            throw new JdbcException(e);
        }
    }

    public static <T> T rsToObject(ResultSet rs, Class<T> clasS) {
        try {
            T obj = clasS.newInstance();
            for (Field field : clasS.getFields()) {
                field.set(obj, rs.getObject(field.getName()));
            }
            return obj;
        }
        catch (Exception e) {
            throw new JdbcException(e);
        }
    }

    private static String getQueryPath(String queryId) {
        return "sql/" + queryId + ".sql";
    }

    private static String replaceParams(String sql, List<String> paramNames) {
        sql = sql.replaceAll("\\?", "�");
        Pattern pattern = Pattern.compile("\\$\\{(\\w+)}");
        java.util.regex.Matcher matcher = pattern.matcher(sql);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            paramNames.add(matcher.group(1));
            matcher.appendReplacement(result, "?");
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private static void setParams(PreparedStatement statement, List<String> paramNames, Map<String, Object> params)
            throws SQLException {
        for (int i = 0; i < paramNames.size(); i++) {
            String paramName = paramNames.get(i);
            Object value = params.get(paramName);

            if (value instanceof List) {
                // Преобразуем List в массив
                List<?> list = (List<?>) value;
                Object[] array = list.toArray();

                // Создаем SQL массив с базовым типом VARCHAR
                Array sqlArray = statement.getConnection().createArrayOf("VARCHAR", array);
                statement.setArray(i + 1, sqlArray);
            } else {
                statement.setObject(i + 1, value);
            }
        }
    }

    private static List<Map<String, Object>> resultSetToMatrix(ResultSet rs) {

        try {
            List<Map<String, Object>> result = new ArrayList<>();
            ResultSetMetaData metaData = rs.getMetaData();

            // Заголовки столбцов
            List<String> columnNames = new ArrayList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columnNames.add(metaData.getColumnName(i));
            }

            // Строки
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 0; i < metaData.getColumnCount(); i++) {
                    row.put(columnNames.get(i), rs.getObject(i + 1));
                }
                result.add(row);
            }
            return result;
        }
        catch (Exception e) {
            throw new JdbcException(e);
        }
    }

    static class JdbcException extends RuntimeException {
        @SneakyThrows
        JdbcException(Exception e) {
            super(e);
            connection.rollback();
        }
    }
}
