package org.vniizht.suburbtransform.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.vniizht.suburbtransform.util.Resources;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;

abstract class ConnectionPool {

    private static final String mainXmlConfigLocation = "/opt/read/datab/DEFAULTX.XML";
    private static final String spareXmlConfigLocation = "DEFAULTX.XML";
    private static final String primaryXmlDS = "NGDS";
    private static final String loggerXmlDS = "LogDS";

    private static DataSource dataSource;
    private static DataSource loggerDataSource;
    private static File xmlConfigFile;

    static {
        try {
            if (Files.exists(Paths.get(mainXmlConfigLocation))) {
                xmlConfigFile = new File(mainXmlConfigLocation);
            } else {
                System.out.println("Конфигурация не найдена: " + mainXmlConfigLocation);
            }
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

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = xmlConfigFile == null
                ? builder.parse(new InputSource(new StringReader(Resources.load(spareXmlConfigLocation))))
                : builder.parse(xmlConfigFile);
        doc.getDocumentElement().normalize();

        NodeList tasks = doc.getElementsByTagName("task");
        for (int i = 0; i < tasks.getLength(); i++) {
            Element task = (Element) tasks.item(i);
            if (dsName.equals(task.getAttribute("datasource"))) {
                return getHikariDataSource(task);
            }
        }
        throw new IllegalArgumentException("БД '" + dsName + "' не найдена в XML");
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