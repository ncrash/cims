package kr.co.kcs.cims.controller.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@RestController
public class HelloController {
    @GetMapping("/")
    public ModelAndView root() {
        return new ModelAndView("redirect:/kcs/swagger.html");
    }

    @GetMapping("/hello")
    public String index() {
        return "hello";
    }
}
