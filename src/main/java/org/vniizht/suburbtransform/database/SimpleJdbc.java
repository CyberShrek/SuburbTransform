package org.vniizht.suburbtransform.database;

import org.vniizht.suburbtransform.util.Resources;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SimpleJdbc {

    public static boolean isQueryExists(String queryId) {
        return Resources.exists(getQueryPath(queryId));
    }

    public static void query(String queryId, Map<String, Object> params) throws SQLException, IOException {
        queryForList(queryId, params);
    }

    public static List<List<Object>> queryForList(String queryId, Map<String, Object> params) throws SQLException, IOException {

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
    public static List<List<Object>> queryForList(String queryId) throws SQLException, IOException {
        return queryForList(queryId, null);
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

    private static List<List<Object>> resultSetToList(ResultSet rs) throws SQLException {
        List<List<Object>> result = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();

        // Заголовки столбцов
        result.add(new ArrayList<>());
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            result.get(0).add(metaData.getColumnName(i));
        }

        // Строки
        while (rs.next()) {
            List<Object> row = new ArrayList<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                row.add(rs.getObject(i));
            }
            result.add(row);
        }

        return result;
    }
}
