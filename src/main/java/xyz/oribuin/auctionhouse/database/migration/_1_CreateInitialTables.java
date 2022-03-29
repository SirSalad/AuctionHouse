package xyz.oribuin.auctionhouse.database.migration;

import dev.rosewood.rosegarden.database.DataMigration;
import dev.rosewood.rosegarden.database.DatabaseConnector;
import dev.rosewood.rosegarden.database.MySQLConnector;

import java.sql.Connection;
import java.sql.SQLException;

public class _1_CreateInitialTables extends DataMigration {

    public _1_CreateInitialTables() {
        super(1);
    }

    @Override
    public void migrate(DatabaseConnector connector, Connection connection, String tablePrefix) throws SQLException {
        final String autoIncrement = connector instanceof MySQLConnector ? "AUTO_INCREMENT" : "";

        // Create the auction table
        String createAuctionTable = "CREATE TABLE " + tablePrefix + "auctions (" +
                " id INTEGER PRIMARY KEY " + autoIncrement + ", " +
                " seller VARCHAR(16) NOT NULL, " +
                " item VARBINARY(2456) NOT NULL, " +
                " price DOUBLE NOT NULL, " +
                " buyer VARCHAR(36), " +
                " createdTime LONG, " +
                " expiredTime LONG, " +
                " soldTime LONG, " +
                " expired BOOLEAN, " +
                "sold BOOLEAN)";

        try (var statement = connection.prepareStatement(createAuctionTable)) {
            statement.executeUpdate();
        }
    }
}
