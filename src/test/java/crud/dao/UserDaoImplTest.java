package crud.dao;

import crud.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class UserDaoImplTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    private static SessionFactory sessionFactory;
    private Session session;
    private UserDaoImpl userDao;
    private User user;


    @BeforeAll
    static void beforeAll() {
        Properties properties = new Properties();
        properties.put("hibernate.connection.url", postgres.getJdbcUrl());
        properties.put("hibernate.connection.username", postgres.getUsername());
        properties.put("hibernate.connection.password", postgres.getPassword());
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.name_table", "users");

        Configuration configuration = new Configuration();
        configuration.addProperties(properties);
        configuration.addAnnotatedClass(User.class);

        sessionFactory = configuration.buildSessionFactory();
    }

    @AfterAll
    static void afterAll() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void setUp() {
        session = sessionFactory.openSession();
        userDao = new UserDaoImpl();

        session.beginTransaction();
        userDao.createUsersTable(session);
        session.getTransaction().commit();

        user = new User();
        user.setName("user");
        user.setEmail("user@user.com");
        user.setAge(20);
        userDao.createUser(user,session);
    }

    @AfterEach()
    void tearDown() {
        session.beginTransaction();
        userDao.dropUsersTable(session);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    void testCreateAndGetUser() {
        session.beginTransaction();
        userDao.createUser(user, session);
        session.getTransaction().commit();

        assertNotNull(user.getId());

        User userDB = userDao.getUserById(user.getId(), session);

        assertNotNull(userDB);
        assertEquals("user", userDB.getName());
        assertEquals("user@user.com", userDB.getEmail());
        assertEquals(20, userDB.getAge());
        assertNotNull(userDB.getCreated_at());
    }

    @Test
    void testUpdateUser() {
        session.beginTransaction();
        userDao.createUser(user, session);
        session.getTransaction().commit();

        Long id = user.getId();
        Instant instant = user.getCreated_at();

        session.beginTransaction();
        userDao.updateUser(id, session, "userUpdate", "userUpdate@user.com", 21);
        session.getTransaction().commit();

        User userUpdate = userDao.getUserById(id,session);
        assertEquals("userUpdate", userUpdate.getName());
        assertEquals("userUpdate@user.com", userUpdate.getEmail());
        assertEquals(21, userUpdate.getAge());
        assertEquals(instant, userUpdate.getCreated_at());
    }

    @Test
    void testDeleteUser(){
        session.beginTransaction();
        userDao.createUser(user, session);
        session.getTransaction().commit();

        Long id = user.getId();

        session.beginTransaction();
        userDao.deleteUser(id,session);
        session.getTransaction().commit();

        User userDeleted = userDao.getUserById(id,session);
        assertNull(userDeleted);
    }
}
