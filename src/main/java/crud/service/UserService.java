package crud.service;

import crud.model.User;

public interface UserService {
    void createUsersTable();

    void dropUsersTable();

    void createUser(User user);

    void updateUser(Long id, String name, String email, Integer age);

    void getUserById(Long id);

    void deleteUser(Long id);

}
