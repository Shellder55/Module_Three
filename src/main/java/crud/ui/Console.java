package crud.ui;

import crud.model.User;
import crud.service.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class Console {

    private final UserServiceImpl userService;
    private final Scanner scanner;
    private static final Logger logger = LoggerFactory.getLogger(Console.class);

    public Console(UserServiceImpl userService) {
        this.userService = userService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            menu();
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> createUsersTable();
                case 2 -> dropUsersTable();
                case 3 -> createUser();
                case 4 -> getUserById();
                case 5 -> updateUser();
                case 6 -> deleteUser();
                case 0 -> {
                    System.out.println("exit...");
                    return;
                }
                default -> System.out.println("Wrong choice");
            }

        }
    }

    private void menu() {
        System.out.println("-----MENU----- \n");
        System.out.println("1. Create table");
        System.out.println("2. Delete table");
        System.out.println("3. Create user");
        System.out.println("4. Get user");
        System.out.println("5. Update user");
        System.out.println("6. Delete user");
        System.out.println("0. Exit \n");
        System.out.print("Select action: ");
    }

    private void createUsersTable() {
        logger.info("Creating table...");
        userService.createUsersTable();
        logger.info("Table successfully created \n");
    }

    private void dropUsersTable() {
        logger.info("Removing table...");
        userService.dropUsersTable();
        logger.info("Table successfully deleted \n");
    }

    private void createUser() {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.print("Enter age: ");
        Integer age = scanner.nextInt();

        User user = new User(name, email, age);

        logger.info("Saving the user...");
        userService.createUser(user);
        logger.info("User added successfully. ID: '{}'\n", user.getId());
    }

    private void getUserById() {
        System.out.print("Enter user ID to search: ");
        Long id = scanner.nextLong();

        logger.info("Getting user by ID: '{}'...", id);
        userService.getUserById(id);
        logger.info("User by ID: '{}' successfully retrieved \n", id);
    }

    private void updateUser() {
        System.out.print("Enter user ID to change: ");
        Long id = scanner.nextLong();
        scanner.nextLine();

        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        System.out.print("Enter age: ");
        Integer age = scanner.nextInt();


        logger.info("Changing user data by ID: '{}'...", id);
        userService.updateUser(id, name, email, age);
        logger.info("User data by ID: '{}' successfully changed \n", id);
    }

    private void deleteUser() {
        System.out.print("Enter user id to delete: ");
        Long id = scanner.nextLong();

        logger.info("Deleting a user by ID: '{}'...", id);
        userService.deleteUser(id);
        logger.info("User by ID: '{}' delete successfully \n", id);
    }

}
