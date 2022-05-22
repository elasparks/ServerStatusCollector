package com.bloodxtears.serverstatuscollector.dao;

import com.bloodxtears.serverstatuscollector.models.Status;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class StatisticsDAO {
    private static Connection connection;

    public List<Status> getStatisticsRange(String from, String to) {
        List<Status> statistics = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + StatEntry.TABLE_NAME +
                    " WHERE " + StatEntry.COLUMN_NAME_DATE + " >= '" + from +
                    "' AND " + StatEntry.COLUMN_NAME_DATE + " <= '" + to + "';");
            while (resultSet.next()) {
                Status status = new Status();
                status.setDate(resultSet.getString(StatEntry.COLUMN_NAME_DATE));
                status.setTime(resultSet.getString(StatEntry.COLUMN_NAME_TIME));
                status.setCpuUsagePercent(resultSet.getDouble(StatEntry.COLUMN_NAME_CPU));
                status.setRamUsagePercent(resultSet.getDouble(StatEntry.COLUMN_NAME_RAM));
                status.setHardDriveFree(resultSet.getDouble(StatEntry.COLUMN_NAME_DISK));
                status.setDnsConnectionTime(resultSet.getDouble(StatEntry.COLUMN_NAME_NETWORK));
                statistics.add(status);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return statistics;
    }

    public static class StatEntry {
        public static final String TABLE_NAME = "statistics";
        public static final String _ID = "_id";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_CPU = "cpu";
        public static final String COLUMN_NAME_RAM = "ram";
        public static final String COLUMN_NAME_DISK = "disk";
        public static final String COLUMN_NAME_NETWORK = "network";
    }

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + StatEntry.TABLE_NAME + " (" +
                    StatEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    StatEntry.COLUMN_NAME_DATE + " TEXT NOT NULL," +
                    StatEntry.COLUMN_NAME_TIME + " TEXT NOT NULL," +
                    StatEntry.COLUMN_NAME_CPU + " REAL NOT NULL," +
                    StatEntry.COLUMN_NAME_RAM + " REAL NOT NULL," +
                    StatEntry.COLUMN_NAME_DISK + " REAL NOT NULL," +
                    StatEntry.COLUMN_NAME_NETWORK + " REAL NOT NULL)";

    static {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:statistics.db");
            connection.setAutoCommit(false);
            Statement statement = connection.createStatement();
            statement.executeUpdate(SQL_CREATE_ENTRIES);
            connection.commit();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
