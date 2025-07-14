package com.jandira.learningplatform.controller;

import com.jandira.learningplatform.model.Tutorial;
import com.jandira.learningplatform.repository.TutorialRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class TutorialController {

    @Autowired
    private TutorialRepository tutorialRepository;

    @GetMapping("/")
    public String redirectToLogin(HttpSession session) {
        if (session.getAttribute("username") == null) {
            return "redirect:/login";
        }
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(@RequestParam(name = "q", required = false) String query, Model model, HttpSession session) {
        if (session.getAttribute("username") == null) {
            return "redirect:/login";
        }

        model.addAttribute("role", session.getAttribute("role"));
        model.addAttribute("username", session.getAttribute("username"));

        if (query != null && !query.isBlank()) {
            model.addAttribute("tutorials", tutorialRepository.searchByTitle(query));
            model.addAttribute("query", query);
        } else {
            model.addAttribute("tutorials", List.of());
        }

        return "index";
    }

    // Formulário para novo tutorial (somente admin)
    @GetMapping("/tutorial/new")
    public String newTutorialForm(HttpSession session, Model model) {
        if (!"admin".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }
        model.addAttribute("tutorial", new Tutorial());
        return "tutorial_form";
    }

    // Salvar novo tutorial
    @PostMapping("/tutorial/save")
    public String saveTutorial(@ModelAttribute Tutorial tutorial, HttpSession session) {
        if (!"admin".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }
        tutorialRepository.save(tutorial);
        return "redirect:/";
    }

    // Formulário de edição
    @GetMapping("/tutorial/edit/{id}")
    public String editTutorial(@PathVariable Long id, HttpSession session, Model model) {
        if (!"admin".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }
        Optional<Tutorial> tutorial = tutorialRepository.findById(id);
        if (tutorial.isPresent()) {
            model.addAttribute("tutorial", tutorial.get());
            return "tutorial_edit";
        } else {
            return "redirect:/";
        }
    }

    @PostMapping("/tutorial/edit/{id}")
    public String updateTutorial(@PathVariable Long id, @ModelAttribute Tutorial updatedTutorial, HttpSession session) {
        if (!"admin".equals(session.getAttribute("role"))) {
            return "redirect:/login";
        }

        Optional<Tutorial> optional = tutorialRepository.findById(id);
        if (optional.isEmpty()) {
            return "redirect:/";
        }

        Tutorial tutorial = optional.get();
        tutorial.setTitle(updatedTutorial.getTitle());
        tutorial.setDescription(updatedTutorial.getDescription());
        tutorial.setVideoUrl(updatedTutorial.getVideoUrl());

        tutorialRepository.save(tutorial);

        return "redirect:/home";
    }

    @GetMapping("/tutorial/{id}")
    public String viewTutorial(@PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("username") == null) {
            return "redirect:/login";
        }

        Optional<Tutorial> optionalTutorial = tutorialRepository.findById(id);
        if (optionalTutorial.isEmpty()) {
            return "redirect:/home";
        }

        Tutorial tutorial = optionalTutorial.get();

        String[] words = tutorial.getTitle().split("\\s+");
        for (int i = words.length - 1; i >= 0; i--) {
            String word = words[i].replaceAll("\\W", "").toLowerCase();
            if (word.length() > 3) {
                session.setAttribute("lastKeyword", word);
                break;
            }
        }

        model.addAttribute("tutorial", optionalTutorial.get());
        return "tutorial_view";
    }
}
