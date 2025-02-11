package site.gutschi.humble.spring.integration.ui.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@SuppressWarnings("SameReturnValue")
public class UserController {

    @GetMapping("/users/create")
    public String createUserView(Model model) {
        //TODO: Create User
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        model.addAttribute("message", "Create user not yet implemented");
        return "error";
    }

    @PostMapping("/tasks/create")
    public String createTask(@RequestParam Map<String, String> body, Model model) {
        //TODO: Create User
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        model.addAttribute("message", "Create user not yet implemented");
        return "error";
    }
}