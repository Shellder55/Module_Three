package crud.dao;

import crud.config.HibernateConfig;
import crud.model.User;
import org.hibernate.Session;

public class UserDaoImpl implements UserDao {
    private final String nameTable = HibernateConfig.loadProperties().getProperty("hibernate.name_table");

    @Override
    public void createUsersTable(Session session) {
        String sql =
                "CREATE TABLE IF NOT EXISTS " + nameTable +
                        "(id BIGSERIAL PRIMARY KEY, " +
                        "name VARCHAR(50) NOT NULL, " +
                        "email VARCHAR(255) UNIQUE NOT NULL, " +
                        "age INTEGER NOT NULL, " +
                        "created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP)";

        session.createNativeQuery(sql).executeUpdate();
    }

    @Override
    public void dropUsersTable(Session session) {
        String sql =
                "DROP TABLE IF EXISTS " + nameTable;
        session.createNativeQuery(sql).executeUpdate();
    }

    @Override
    public void createUser(User user, Session session) {
        session.persist(user);
    }

    @Override
    public void updateUser(Long id, Session session, String name, String email, Integer age) {
        User user = getUserById(id, session);
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
    }

    @Override
    public User getUserById(Long id, Session session) {
        return session.get(User.class, id);
    }

    @Override
    public void deleteUser(Long id, Session session) {
        User user = session.get(User.class, id);
        if (user != null) {
            session.remove(user);
        }
    }
}
