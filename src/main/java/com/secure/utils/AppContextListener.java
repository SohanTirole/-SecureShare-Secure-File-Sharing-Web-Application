package com.secure.utils;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {
	
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("SecureShare Application Started.");
        DBConnection.createDatabaseIfNotExists();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("SecureShare Application Stopping. Cleaning up resources...");

        // Shut down the MySQL AbandonedConnectionCleanupThread
        try {
            com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.checkedShutdown();
            //System.out.println("MySQL cleanup thread shut down successfully.");
        } catch (Exception e) {
        		e.printStackTrace();
            //System.err.println("Failed to shut down MySQL cleanup thread: " + e.getMessage());
        }

        // Deregister JDBC drivers
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                System.out.println("Deregistering JDBC driver: " + driver);
            } catch (SQLException e) {
                System.err.println("Error deregistering driver: " + e.getMessage());
            }
        }
    }
}