package com.alvaro.demo.spring_cap1;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// @Controller: le dice a Spring que esta clase es un controlador.
// Spring la detecta automáticamente al arrancar (component scanning)
// y crea un objeto de esta clase por ti (bean) sin que tengas que hacer new HomeController().
@Controller
public class HomeController {

    // @GetMapping("/"): indica que este método se ejecuta cuando
    // llega una petición HTTP tipo GET a la ruta raíz "/"
    // (por ejemplo, cuando alguien visita http://localhost:8080/)
    @GetMapping("/")
    public String home() {
        // El String que devuelve NO es el HTML en sí,
        // es el "nombre lógico" de una vista.
        // Spring, al ver que hay Thymeleaf en el proyecto, busca
        // automáticamente el archivo: src/main/resources/templates/home.html
        return "home";
    }
}