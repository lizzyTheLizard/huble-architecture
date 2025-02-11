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

    @GetMapping("/users/current")
    public String viewUser(Model model) {
        //TODO: View User
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        model.addAttribute("message", "View user not yet implemented");
        return "error";
    }

    @GetMapping("/users/current/edit")
    public String createUserView(Model model) {
        //TODO: Edit User
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        model.addAttribute("message", "Edit user not yet implemented");
        return "error";
    }

    @PostMapping("/users/current/edit")
    public String createTask(@RequestParam Map<String, String> body, Model model) {
        //TODO: Edit User
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        model.addAttribute("message", "Edit user not yet implemented");
        return "error";
    }
}