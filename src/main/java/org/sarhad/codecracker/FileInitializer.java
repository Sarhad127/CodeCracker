package org.sarhad.codecracker;

import org.sarhad.codecracker.service.HashService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileInitializer {

    private final HashService  hashService;

    public FileInitializer(HashService hashService) {
        this.hashService = hashService;
    }

    //Skapar filerna och updaterar innehållet
    @Bean
    public CommandLineRunner init() {
        return args -> {
            Path passwordFile = Paths.get("src/main/resources/password.txt");
            Path hashesFile = Paths.get("src/main/resources/hashes.txt");
            if (Files.notExists(passwordFile)) {
                Files.createFile(passwordFile);
                System.out.println("Created password.txt");
            }
            if (Files.notExists(hashesFile)) {
                Files.createFile(hashesFile);
                System.out.println("Created hashes.txt");
            }
            hashService.updateHashesFile(passwordFile, hashesFile);
        };
    }
}
