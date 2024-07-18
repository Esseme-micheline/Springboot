package net.javaguides.sms.controller;

import net.javaguides.sms.entity.User;
import net.javaguides.sms.entity.UserRole;
import net.javaguides.sms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginForm() {
        return "login"; // Return login.html view
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam String username, @RequestParam String password, @RequestParam String role, Model model) {
        // Debugging logs
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("Role: " + role);

        if (username == null || password == null || role == null) {
            model.addAttribute("error", "All fields are required");
            return "login";
        }

        User user = userRepository.findByUsername(username);
        if (user != null && passwordEncoder.matches(password, user.getPassword()) && user.getRole().name().equals(role)) {
            if (user.getRole() == UserRole.STUDENT) {
                return "redirect:/"; // Redirect to home page for students
            } else if (user.getRole() == UserRole.TEACHER) {
                return "redirect:/courses"; // Redirect to courses page for teachers
            } else {
                // Handle other roles if needed
            }
        }
        model.addAttribute("error", "Invalid username, password, or role");
        return "login"; // Return login.html view with error message
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // Return register.html view
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "register"; // Return register.html view with validation errors
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            bindingResult.rejectValue("password", "error.user", "Password cannot be null or empty");
            return "register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            return "register"; // Return register.html view with error message
        }

        return "redirect:/auth/login"; // Redirect to login page after registration
    }
    @GetMapping("/profile")
    public String profileForm(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(User user, RedirectAttributes redirectAttributes) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User existingUser = userRepository.findByUsername(userDetails.getUsername());
        existingUser.setUsername(user.getUsername());
        if (!user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(existingUser);
        redirectAttributes.addAttribute("successMessage", "Votre mise à jour a été effectuée avec succès");
        return "redirect:/auth/profile";
    }

    @PostMapping("/profile/delete")
    public String deleteProfile(RedirectAttributes redirectAttributes) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        userRepository.delete(user);
        SecurityContextHolder.clearContext();
        redirectAttributes.addAttribute("successMessage", "Votre suppression a été effectuée avec succès");
        return "redirect:/auth/login";
    }

}

