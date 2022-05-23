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
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + StatisticsEntries.TABLE_NAME +
                    " WHERE " + StatisticsEntries.COLUMN_NAME_DATE + " >= '" + from +
                    "' AND " + StatisticsEntries.COLUMN_NAME_DATE + " <= '" + to + "';");
            while (resultSet.next()) {
                Status status = new Status();
                status.setDate(resultSet.getString(StatisticsEntries.COLUMN_NAME_DATE));
                status.setTime(resultSet.getString(StatisticsEntries.COLUMN_NAME_TIME));
                status.setCpuUsagePercent(resultSet.getDouble(StatisticsEntries.COLUMN_NAME_CPU));
                status.setRamUsagePercent(resultSet.getDouble(StatisticsEntries.COLUMN_NAME_RAM));
                status.setHardDriveFree(resultSet.getDouble(StatisticsEntries.COLUMN_NAME_DISK));
                status.setDnsConnectionTime(resultSet.getDouble(StatisticsEntries.COLUMN_NAME_NETWORK));
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

    public Status getLatestStatus() {
        Status status = null;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQL_SELECT_LAST);
            if (resultSet.next()) {
                status = new Status();
                status.setDate(resultSet.getString(StatisticsEntries.COLUMN_NAME_DATE));
                status.setTime(resultSet.getString(StatisticsEntries.COLUMN_NAME_TIME));
                status.setCpuUsagePercent(resultSet.getDouble(StatisticsEntries.COLUMN_NAME_CPU));
                status.setRamUsagePercent(resultSet.getDouble(StatisticsEntries.COLUMN_NAME_RAM));
                status.setHardDriveFree(resultSet.getDouble(StatisticsEntries.COLUMN_NAME_DISK));
                status.setDnsConnectionTime(resultSet.getDouble(StatisticsEntries.COLUMN_NAME_NETWORK));
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return status;
    }

    public void updateStatistics(Status status) {
        try {
            Status lastStat = getLatestStatus();
            Statement statement = connection.createStatement();
            if (lastStat != null && status.getDate().equalsIgnoreCase(lastStat.getDate())) {
                status.setCpuUsagePercent((status.getCpuUsagePercent() + lastStat.getCpuUsagePercent()) / 2);
                status.setRamUsagePercent((status.getRamUsagePercent() + lastStat.getRamUsagePercent()) / 2);
                status.setHardDriveFree((status.getHardDriveFree() + lastStat.getHardDriveFree()) / 2);
                if (status.getDnsConnectionTime() != -1)
                    status.setDnsConnectionTime((status.getDnsConnectionTime() + lastStat.getDnsConnectionTime()) / 2);
                else
                    status.setDnsConnectionTime(lastStat.getDnsConnectionTime());
                statement.executeUpdate(SQL_DELETE_LAST);
                connection.commit();
            }
            statement.executeUpdate(createInsertQuery(status));
            connection.commit();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static class StatisticsEntries {
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
            "CREATE TABLE IF NOT EXISTS " + StatisticsEntries.TABLE_NAME + " (" +
                    StatisticsEntries._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    StatisticsEntries.COLUMN_NAME_DATE + " TEXT NOT NULL," +
                    StatisticsEntries.COLUMN_NAME_TIME + " TEXT NOT NULL," +
                    StatisticsEntries.COLUMN_NAME_CPU + " REAL NOT NULL," +
                    StatisticsEntries.COLUMN_NAME_RAM + " REAL NOT NULL," +
                    StatisticsEntries.COLUMN_NAME_DISK + " REAL NOT NULL," +
                    StatisticsEntries.COLUMN_NAME_NETWORK + " REAL NOT NULL)";

    private static final String SQL_MAX_ID = "(SELECT MAX(" + StatisticsEntries._ID + ") FROM " + StatisticsEntries.TABLE_NAME + ");";
    private static final String SQL_DELETE_LAST = "DELETE FROM " + StatisticsEntries.TABLE_NAME + " WHERE " + StatisticsEntries._ID + " = " + SQL_MAX_ID;
    private static final String SQL_SELECT_LAST = "SELECT * FROM " + StatisticsEntries.TABLE_NAME + " WHERE " + StatisticsEntries._ID + " = " + SQL_MAX_ID;

    public static String createInsertQuery(Status status) {
        String query = "INSERT INTO " + StatisticsEntries.TABLE_NAME;
        String columns = " (" +
                StatisticsEntries.COLUMN_NAME_DATE + ", " +
                StatisticsEntries.COLUMN_NAME_TIME + ", " +
                StatisticsEntries.COLUMN_NAME_CPU + ", " +
                StatisticsEntries.COLUMN_NAME_RAM + ", " +
                StatisticsEntries.COLUMN_NAME_DISK + ", " +
                StatisticsEntries.COLUMN_NAME_NETWORK + ")";
        String values = " VALUES ('" +
                status.getDate() + "', '" +
                status.getTime() + "', " +
                status.getCpuUsagePercent() + ", " +
                status.getRamUsagePercent() + ", " +
                status.getHardDriveFree() + ", " +
                status.getDnsConnectionTime() + ");";
        return query + columns + values;
    }

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
