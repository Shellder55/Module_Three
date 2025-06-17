package crud;

import crud.config.HibernateConfig;
import crud.service.UserServiceImpl;
import crud.ui.Console;

public class Main {
    public static void main(String[] args) {
        try {
            UserServiceImpl userService = new UserServiceImpl();
            Console console = new Console(userService);
            console.start();
        } finally {
            HibernateConfig.shutdown();
        }
    }
}

