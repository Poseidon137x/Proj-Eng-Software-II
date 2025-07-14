package com.jandira.learningplatform.controller;

import com.jandira.learningplatform.model.User;
import com.jandira.learningplatform.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // Página de login
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // Processar login
    @PostMapping("/login")
    public String doLogin(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        User user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            model.addAttribute("error", "Usuário ou senha inválidos");
            return "login";
        }

        // Armazenar dados na sessão
        session.setAttribute("userId", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("role", user.getRole());

        return "redirect:/home";
    }

    // Página de cadastro
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // Processar cadastro
    @PostMapping("/register")
    public String doRegister(@RequestParam String username, @RequestParam String password, @RequestParam(defaultValue = "USER") String role, Model model) {
        if (userRepository.findByUsername(username) != null) {
            model.addAttribute("error", "Usuário já existe");
            return "register";
        }

        User newUser = new User(username, password, role.toLowerCase());
        userRepository.save(newUser);
        return "redirect:/login";
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot_password";
    }

    @PostMapping("/forgot-password")
    public String findUserForReset(@RequestParam String username, Model model) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            model.addAttribute("error", "Usuário não encontrado");
            return "forgot_password";
        }
        model.addAttribute("username", username);
        return "reset_password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String username, @RequestParam String newPassword, Model model) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            model.addAttribute("error", "Usuário não encontrado");
            return "forgot_password";
        }
        user.setPassword(newPassword);
        userRepository.save(user);
        model.addAttribute("message", "Senha redefinida com sucesso");
        return "login";
    }
}
