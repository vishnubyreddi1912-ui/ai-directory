package com.aihub.directory;

import com.aihub.directory.repositories.AiToolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
public class DirectoryApplication   implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AiToolRepository websiteRepository;

    public static void main(String[] args) {
        SpringApplication.run(DirectoryApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            System.out.println("connected");
        } catch (Exception e) {
            System.err.println("❌ Failed to connect to database");
            e.printStackTrace();
        }
    }
}