package net.javaguides.sms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/user/profile")
    public String userProfile() {
        return "profile"; // Exemple de gestion du profil utilisateur
    }

    // Autres méthodes pour gérer les fonctionnalités des utilisateurs
}
