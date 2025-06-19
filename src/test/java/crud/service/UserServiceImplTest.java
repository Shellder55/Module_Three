package crud.service;

import crud.config.DaoException;
import crud.config.EntityNotFoundException;
import crud.dao.UserDaoImpl;
import crud.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    private Transaction transaction;
    @Mock
    private Logger logger;
    @Mock
    private UserDaoImpl userDao;
    private User testUser;
    private Long invalidId;
    private Long nonExistingUserId;
    private RuntimeException runtimeException;
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        testUser = new User(1L, "test", "test@test.com", 10, LocalDateTime.now());
        invalidId = -1L;
        nonExistingUserId = 999999L;
        runtimeException = new RuntimeException();
    }

    @Test
    public void createUsersTableSuccess() {
        doNothing().when(userDao).createUsersTable(session);

        userService.createUsersTable();

        verify(userDao).createUsersTable(session);
        verify(transaction).commit();
        verify(session).close();
    }

    @Test
    public void createUsersTableThrowError() {
        doThrow(runtimeException).when(userDao).createUsersTable(session);

        userService.createUsersTable();

        verify(userDao).createUsersTable(session);
        verify(transaction).rollback();
        verify(session).close();
        verify(logger).error("Error creating table: " + runtimeException.getMessage());
    }

    @Test
    public void dropUsersTableSuccess() {
        doNothing().when(userDao).dropUsersTable(session);

        userService.dropUsersTable();

        verify(userDao).dropUsersTable(session);
        verify(transaction).commit();
        verify(session).close();
    }

    @Test
    public void dropUsersTableThrowError() {
        doThrow(runtimeException).when(userDao).dropUsersTable(session);

        userService.dropUsersTable();

        verify(userDao).dropUsersTable(session);
        verify(transaction).rollback();
        verify(session).close();
        verify(logger).error("Error removing table: " + runtimeException.getMessage());
    }

    @Test
    public void createUserSuccess() {
        doNothing().when(userDao).createUser(testUser, session);

        assertDoesNotThrow(() -> userService.createUser(testUser));

        verify(userDao).createUser(testUser, session);
        verify(transaction).commit();
        verify(session).close();
    }

    @Test
    public void createUserThrowDaoException() {
        doThrow(runtimeException).when(userDao).createUser(testUser, session);

        Exception exception = assertThrows(RuntimeException.class, () -> userService.createUser(testUser));
        assertEquals("Error saving user: ", exception.getMessage());

        verify(userDao).createUser(testUser, session);
        verify(transaction).rollback();
        verify(session).close();
    }

    @Test
    public void updateUserSuccess() {
        doNothing().when(userDao).updateUser(testUser.getId(), session, testUser.getName(), testUser.getEmail(), testUser.getAge());

        assertDoesNotThrow(() -> userService.updateUser(testUser.getId(), testUser.getName(), testUser.getEmail(), testUser.getAge()));

        verify(userDao).updateUser(testUser.getId(), session, testUser.getName(), testUser.getEmail(), testUser.getAge());
        verify(transaction).commit();
        verify(session).close();
    }

    @Test
    void updateUserInvalidIdThrowsDaoException() {
        doThrow(runtimeException).when(userDao).updateUser(invalidId, session, testUser.getName(), testUser.getEmail(), testUser.getAge());

        DaoException exception = assertThrows(DaoException.class, () ->
                userService.updateUser(invalidId, testUser.getName(), testUser.getEmail(), testUser.getAge()));

        assertEquals("Failed changing user data: " + invalidId, exception.getMessage());
        assertSame(runtimeException, exception.getCause());
        verify(transaction).rollback();
        verify(session).close();
    }

    @Test
    void getUserByIdSuccess(){
        when(userDao.getUserById(testUser.getId(), session)).thenReturn(testUser);

        assertDoesNotThrow(() -> userService.getUserById(testUser.getId()));

        verify(userDao).getUserById(testUser.getId(), session);
        verify(transaction).commit();
        verify(session).close();
    }

    @Test
    void getUserByIdThrowEntityNotFoundException(){
        when(userDao.getUserById(nonExistingUserId, session)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.getUserById(nonExistingUserId));

        assertEquals("User with ID: " + nonExistingUserId + " not found", exception.getMessage());
        verify(userDao).getUserById(nonExistingUserId, session);
        verify(transaction).commit();
        verify(session).close();
    }

    @Test
    void getUserByIdThrowDaoException(){
        when(userDao.getUserById(testUser.getId(), session)).thenThrow(runtimeException);

        DaoException exception = assertThrows(DaoException.class, () -> userService.getUserById(testUser.getId()));

        assertEquals("Failed to get user by ID: " + testUser.getId(), exception.getMessage());
        assertSame(runtimeException, exception.getCause());
        verify(userDao).getUserById(testUser.getId(), session);
        verify(session).close();
    }

    @Test
    void deleteUserSuccess(){
        when(userDao.getUserById(testUser.getId(), session)).thenReturn(testUser);
        doNothing().when(userDao).deleteUser(testUser.getId(), session);

        assertDoesNotThrow(() -> userService.deleteUser(testUser.getId()));

        verify(userDao).getUserById(testUser.getId(), session);
        verify(userDao).deleteUser(testUser.getId(), session);
        verify(transaction).commit();
        verify(session).close();
    }

    @Test
    void deleteUserThrowEntityNotFoundException(){
        when(userDao.getUserById(nonExistingUserId, session)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(nonExistingUserId));

        assertEquals("User with ID: " + nonExistingUserId + " not found", exception.getMessage());
        verify(userDao).getUserById(nonExistingUserId, session);
        verify(logger).warn("Attempt to delete non-existent user with ID: {}", nonExistingUserId);
        verify(session).close();
        verify(transaction).rollback();
    }

    @Test
    void deleteUserThrowDaoException(){
        when(userDao.getUserById(testUser.getId(), session)).thenThrow(runtimeException);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.deleteUser(testUser.getId()));

        assertSame(runtimeException, exception);
        verify(userDao).getUserById(testUser.getId(), session);
        verify(session).close();
        verify(logger).error("Error delete user: " + exception.getMessage());
    }
}