package org.sarhad.codecracker.controller;

import org.sarhad.codecracker.service.HashService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.*;
import java.security.NoSuchAlgorithmException;

@Controller
public class HashController {

    private final HashService hashService;

    public HashController(HashService hashService) {
        this.hashService = hashService;
    }

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
        String md5Hash = hashService.generateMD5(input);
        String sha256Hash = hashService.generateSHA256(input);
        model.addAttribute("md5Hash", md5Hash);
        model.addAttribute("sha256Hash", sha256Hash);
        return "hash";
    }

    @PostMapping("/search")
    public String searchHash(@RequestParam("hashValue") String hashValue, Model model) {
        try {
            hashService.loadPasswordHashMap();
            String password = hashService.findPasswordByHash(hashValue);
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

}