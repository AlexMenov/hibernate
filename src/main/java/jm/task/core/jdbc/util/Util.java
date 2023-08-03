package jm.task.core.jdbc.util;

import jm.task.core.jdbc.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Util {
    private static final SessionFactory configuration;
    private static final String URL = "db.url";
    private static final String USER = "db.user";
    private static final String PASS = "db.pass";
    private static final Properties PROPERTIES = new Properties();

    static {
        try {
            loadProperties();
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .applySettings(PROPERTIES)
                    .build();

            configuration = new MetadataSources(registry)
                    .addAnnotatedClass(User.class)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//            configuration = new Configuration().addAnnotatedClass(User.class).buildSessionFactory();
//        driverLoader();
    }

    private Util() {
    }

    public static SessionFactory getConfig() {
        if (configuration == null) {
            throw new RuntimeException("SessionFactory not initialized!");
        }
        return configuration;
    }

    public static Connection getConnection() {
        loadProperties();
        try {
            return DriverManager.getConnection(
                    PROPERTIES.getProperty(URL),
                    PROPERTIES.getProperty(USER),
                    PROPERTIES.getProperty(PASS));
        } catch (SQLException e) {
            throw new RuntimeException("Connection error");
        }
    }

    private static void loadProperties() {
        try (InputStream inputStream = Util.class.getClassLoader().getResourceAsStream("hibernate.properties")) {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Properties read error");
        }
    }

    private static void driverLoader() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver load error");
        }
    }

    public enum Action {
        NULLPARAMS(null), SAVEUSER(null), REMOVEUSERBYID(null),
        GETALLUSERS("from User"),
        CLEANUSERSTABLE("DELETE from User"),
        DROPUSERSTABLE("DROP TABLE IF EXISTS users.users"),
        CREATEUSERSTABLE("""
                CREATE TABLE IF NOT EXISTS users.users (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(50) NOT NULL, 
                last_name VARCHAR(50) NOT NULL,
                age INT NOT NULL                     
                );
                """);

        private final String query;

        Action(String query) {
            this.query = query;
        }

        public String getQuery() {
            return query;
        }
    }
}
