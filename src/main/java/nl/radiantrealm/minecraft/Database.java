package nl.radiantrealm.minecraft;

import nl.radiantrealm.library.controller.DatabaseController;

public class Database extends DatabaseController {

    @Override
    protected String databaseURL() {
        return "localhost";
    }

    @Override
    protected String databaseUsername() {
        return System.getenv("DB_USERNAME");
    }

    @Override
    protected String databasePassword() {
        return System.getenv("DB_PASSWORD");
    }
}
