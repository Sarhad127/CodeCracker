package org.sarhad.codecracker.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(HashController.class)
public class HashControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void showHashForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hash"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("hash"));
    }
}
