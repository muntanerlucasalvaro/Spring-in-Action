package com.alvaro.demo.spring_cap1;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

// @WebMvcTest: levanta solo la parte de Spring MVC necesaria
// para testear HomeController, sin arrancar toda la app entera.
@WebMvcTest(HomeController.class)
public class HomeControllerTest {

    // MockMvc simula peticiones HTTP sin necesitar un navegador ni un servidor real.
    // Spring lo inyecta automáticamente gracias a @WebMvcTest.
    @Autowired
    private MockMvc mockMvc;

    // @Test marca este método como un test que JUnit debe ejecutar.
    @Test
    public void testHomePage() throws Exception {
        mockMvc.perform(get("/"))              // Simula un GET a "/"
                .andExpect(status().isOk())               // Espera código HTTP 200
                .andExpect(view().name("home"))           // Espera que la vista sea "home"
                .andExpect(content().string(
                        containsString("Welcome to my DEMO")));         // Espera que el HTML contenga "HOME"
    }
}