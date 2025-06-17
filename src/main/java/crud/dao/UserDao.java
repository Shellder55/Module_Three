package crud.dao;

import crud.model.User;
import org.hibernate.Session;

public interface UserDao {
    void createUsersTable(Session session);

    void dropUsersTable(Session session);

    void createUser(User user, Session session);

    void updateUser(Long id, Session session, String name, String email, Integer age);

    User getUserById(Long id, Session session);

    void deleteUser(Long id, Session session);
}
