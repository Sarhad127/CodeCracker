package org.sarhad.codecracker.controller;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class HashController {

    private final Map<String, String> passwordHashMap = new HashMap<>();

    @GetMapping("/")
    public String redirectToHash() {
        return "redirect:/hash";
    }

    @GetMapping("/hash")
    public String showHashForm() {
        return "hash";
    }

    @GetMapping("/search")
    public String showSearchForm() {
        return "search";
    }

    @PostMapping("/hash")
    public String generateHashes(@RequestParam("input") String input, Model model) throws NoSuchAlgorithmException {
        String md5Hash = generateMD5(input);
        String sha256Hash = generateSHA256(input);
        model.addAttribute("md5Hash", md5Hash);
        model.addAttribute("sha256Hash", sha256Hash);
        return "hash";
    }

    @PostMapping("/search")
    public String searchHash(@RequestParam("hashValue") String hashValue, Model model) {
        try {
            loadPasswordHashMap();
            String password = findPasswordByHash(hashValue);
            if (password != null) {
                model.addAttribute("message", "Password is: " + password);
            } else {
                model.addAttribute("message", "Password not found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("message", "error!");
        }
        return "search";
    }

    // Kontrollerar om hashvärdet finns i min fil och returnerar lösenordet om hashet finns
    private String findPasswordByHash(String hash) {
        for (Map.Entry<String, String> entry : passwordHashMap.entrySet()) {
            String[] hashes = entry.getValue().split(":");
            if (hashes.length == 3) {
                String md5Hash = hashes[1].trim();
                String sha256Hash = hashes[2].trim();
                if (md5Hash.equals(hash) || sha256Hash.equals(hash)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    private String generateMD5(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        BigInteger no = new BigInteger(1, messageDigest);
        String hashText = no.toString(16);
        while (hashText.length() < 32) {
            hashText = "0" + hashText;
        }
        return hashText;
    }

    private String generateSHA256(String input) throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] digest = sha.digest(input.getBytes(StandardCharsets.UTF_8));
        BigInteger bigNumber = new BigInteger(1, digest);
        String hashText = bigNumber.toString(16);
        while (hashText.length() < 64) {
            hashText = "0" + hashText;
        }
        return hashText;
    }

    //Läser in datat från hashes.txt filen
    private void loadPasswordHashMap() throws IOException {
        Path path = Paths.get("src/main/resources/hashes.txt");

        if (Files.exists(path)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(":")) {
                        String[] parts = line.split(":");
                        if (parts.length == 3) {
                            passwordHashMap.put(parts[0].trim(), line);
                        }
                    }
                }
            }
        }
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
            updateHashesFile(passwordFile, hashesFile);
        };
    }
    private void updateHashesFile(Path passwordFile, Path hashesFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(passwordFile.toFile()));
             BufferedWriter writer = new BufferedWriter(new FileWriter(hashesFile.toFile()))) {

            String password;
            while ((password = reader.readLine()) != null && !password.trim().isEmpty()) {
                String md5Hash = generateMD5(password);
                String sha256Hash = generateSHA256(password);
                writer.write(password + ":" + md5Hash + ":" + sha256Hash);
                writer.newLine();
            }
            System.out.println("Updated hashes.txt.");
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println("error!");
        }
    }
}