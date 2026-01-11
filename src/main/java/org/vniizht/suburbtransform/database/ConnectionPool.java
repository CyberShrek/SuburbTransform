package org.vniizht.suburbtransform.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

abstract class ConnectionPool {

//    private static final String xmlConfigLocation = "/opt/read/datab/DEFAULTX.XML";
    private static final String xmlConfigLocation = "C:\\HelloWorld\\projects\\WAFW\\SuburbTransform\\DEFAULTX.XML";
    private static final String primaryXmlDS = "NGDS";
    private static final String loggerXmlDS = "LogDS";

    private static DataSource dataSource;
    private static DataSource loggerDataSource;

    static {
        try {
            dataSource       = getXmlDataSource(primaryXmlDS);
            loggerDataSource = getXmlDataSource(loggerXmlDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    public static Connection getLoggerConnection() throws SQLException {
        return loggerDataSource.getConnection();
    }

    private static DataSource getXmlDataSource(String dsName) throws Exception {

        File xmlFile = new File(xmlConfigLocation);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);
        doc.getDocumentElement().normalize();

        NodeList tasks = doc.getElementsByTagName("task");
        for (int i = 0; i < tasks.getLength(); i++) {
            Element task = (Element) tasks.item(i);
            if (dsName.equals(task.getAttribute("datasource"))) {
                return getHikariDataSource(task);
            }
        }
        throw new IllegalArgumentException("Database database '" + dsName + "' not found in XML");
    }

    private static DataSource getHikariDataSource(Element task) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(task.getAttribute("jdbcString"));
        config.setUsername(task.getAttribute("nameuser"));
        config.setPassword(task.getAttribute("pass"));

        // Минимальные настройки для долгих запросов
        config.setMaximumPoolSize(1);              // Одно соединение для исключения конфликтов
        config.setMinimumIdle(1);                  // Всегда одно соединение доступно
        config.setConnectionTimeout(60000);        // 60 секунд на получение соединения
        config.setMaxLifetime(28_800_000);         // 8 часов (больше чем время запроса)

        // Отключаем детекцию утечек для долгих запросов
        config.setLeakDetectionThreshold(0);

        // Отключаем JMX и логирование
        config.setRegisterMbeans(false);

        return new HikariDataSource(config);
    }
}