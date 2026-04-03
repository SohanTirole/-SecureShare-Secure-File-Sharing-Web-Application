package com.secure.utils;

import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.annotation.WebListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.sql.*;

@WebListener
public class CleanupListener implements ServletContextListener {
    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        String uploadPath = sce.getServletContext().getInitParameter("file-upload-path");

        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("Running File Cleanup Task...");
            try (Connection conn = DBConnection.getConnection()) {
                // Find files that are expired but still on disk
                String sql = "SELECT stored_name FROM files WHERE (expiry_time < NOW() OR status = 'expired')";
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                
                while (rs.next()) {
                    File file = new File(uploadPath + File.separator + rs.getString("stored_name"));
                    if (file.exists()) {
                        file.delete();
                        System.out.println("Deleted expired file: " + file.getName());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.HOURS); // Runs every hour
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        scheduler.shutdownNow();
    }
}