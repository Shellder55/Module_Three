package crud.config;

import crud.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HibernateConfig {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();
                configuration.addProperties(HibernateConfig.loadProperties());
                configuration.addAnnotatedClass(User.class);

                StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties())
                        .build();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to create hibernate SessionFactory", e);
            }
        }
        return sessionFactory;
    }

    public static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream inputStream = HibernateConfig.class.getClassLoader().getResourceAsStream("hibernate.properties");
             InputStream inputStream1 = HibernateConfig.class.getClassLoader().getResourceAsStream("hibernate.properties")) {

            if (inputStream == null) {
                throw new RuntimeException("File hibernate.properties not found");
            }

            properties.load(inputStream);
            properties.load(inputStream1);

        } catch (IOException e) {
            throw new RuntimeException("Error loading configuration " + e);
        }
        return properties;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}