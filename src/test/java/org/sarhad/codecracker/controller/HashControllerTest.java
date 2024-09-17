package org.sarhad.codecracker.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sarhad.codecracker.service.HashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HashController.class)
class HashControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private HashService hashService;

    @InjectMocks
    private HashController hashController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void redirectToHash() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hash"));
    }

    @Test
    void showHashForm() throws Exception {
        mockMvc.perform(get("/hash"))
                .andExpect(status().isOk())
                .andExpect(view().name("hash"));
    }

    @Test
    void showSearchForm() throws Exception {
        mockMvc.perform(get("/search"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"));
    }

    @Test
    void generateHashes() throws Exception {
        String input = "testInput";
        String md5Hash = "md5Hash";
        String sha256Hash = "sha256Hash";

        when(hashService.generateMD5(input)).thenReturn(md5Hash);
        when(hashService.generateSHA256(input)).thenReturn(sha256Hash);

        mockMvc.perform(post("/hash")
                        .param("input", input)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("hash"))
                .andExpect(model().attribute("md5Hash", md5Hash))
                .andExpect(model().attribute("sha256Hash", sha256Hash));

        verify(hashService).generateMD5(input);
        verify(hashService).generateSHA256(input);
    }

    @Test
    void searchHash() throws Exception {
        String hashValue = "someHashValue";
        String password = "password123";

        when(hashService.findPasswordByHash(hashValue)).thenReturn(password);
        doNothing().when(hashService).loadPasswordHashMap();

        mockMvc.perform(post("/search")
                        .param("hashValue", hashValue)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attribute("message", "Password is: " + password));

        verify(hashService).findPasswordByHash(hashValue);
        verify(hashService).loadPasswordHashMap();
    }

    @Test
    void searchHashWhenNotFound() throws Exception {
        String hashValue = "someHashValue";

        when(hashService.findPasswordByHash(hashValue)).thenReturn(null);
        doNothing().when(hashService).loadPasswordHashMap();

        mockMvc.perform(post("/search")
                        .param("hashValue", hashValue)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attribute("message", "Password not found."));

        verify(hashService).findPasswordByHash(hashValue);
        verify(hashService).loadPasswordHashMap();
    }

    @Test
    void searchHashWhenIOException() throws Exception {
        String hashValue = "someHashValue";

        doThrow(new IOException()).when(hashService).loadPasswordHashMap();

        mockMvc.perform(post("/search")
                        .param("hashValue", hashValue)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attribute("message", "error!"));

        verify(hashService).loadPasswordHashMap();
        verify(hashService, never()).findPasswordByHash(hashValue);
    }
}
