package site.gutschi.humble.spring.main.security;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@SuppressWarnings("SameReturnValue")
public class LoginController {
    @GetMapping("/login.html")
    public String showLogin(@RequestParam(value = "error", required = false, defaultValue = "false") boolean error, Model model) {
        model.addAttribute("error", error);
        return "login";
    }
}