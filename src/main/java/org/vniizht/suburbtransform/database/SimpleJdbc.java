package org.vniizht.suburbtransform.database;

import org.vniizht.suburbtransform.util.Resources;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SimpleJdbc {

    public static boolean isQueryExists(String queryId) {
        return Resources.exists(getQueryPath(queryId));
    }

    public static void query(String queryId, Map<String, Object> params) throws SQLException, IOException {
        queryForMatrix(queryId, params);
    }

    public static List<Map<String, Object>> queryForMatrix(String queryId, Map<String, Object> params) throws SQLException, IOException {

        String sql = Resources.load(getQueryPath(queryId));
        List<String> paramNames = new ArrayList<>();
        String preparedSql = replaceParams(sql, paramNames);

        try (Connection connection = ConnectionPool.getConnection();
             PreparedStatement stmt = connection.prepareStatement(preparedSql)) {
            if (params != null)
                setParams(stmt, paramNames, params);

            try (ResultSet rs = stmt.executeQuery()) {
                return resultSetToList(rs);
            }
        }
    }
    public static List<Map<String, Object>> queryForMatrix(String queryId) throws SQLException, IOException {
        return queryForMatrix(queryId, null);
    }

    public static <T> List<T> queryForObjects(String queryId, Map<String, Object> params, Class<T> clazz) throws Exception {
        List<Map<String, Object>> rows = queryForMatrix(queryId, params);
        List<T> objects = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            T obj = clazz.newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                field.set(obj, row.get(field.getName()));
            }
            objects.add(obj);
        }
        return objects;
    }
    public static <T> List<T> queryForObjects(String queryId, Class<T> clasS) throws Exception {
        return queryForObjects(queryId, null, clasS);
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

    private static void setParams(PreparedStatement stmt, List<String> paramNames, Map<String, Object> params)
            throws SQLException {
        for (int i = 0; i < paramNames.size(); i++) {
            String paramName = paramNames.get(i);
            Object value = params.get(paramName);

            if (value instanceof List) {
                // Преобразуем List в массив
                List<?> list = (List<?>) value;
                Object[] array = list.toArray();

                // Создаем SQL массив с базовым типом VARCHAR
                Array sqlArray = stmt.getConnection().createArrayOf("VARCHAR", array);
                stmt.setArray(i + 1, sqlArray);
            } else {
                stmt.setObject(i + 1, value);
            }
        }
    }

    private static List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        ResultSetMetaData metaData       = rs.getMetaData();

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
}
