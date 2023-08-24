package com.ms.email.marketing.controller;

import com.ms.email.marketing.model.*;
import com.ms.email.marketing.repository.*;
import com.ms.email.marketing.service.EmailService;
import com.ms.email.marketing.service.TemplateService;
import com.ms.email.marketing.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.ms.email.marketing.constant.AppConstant.FLASH_ATTR_ERRORMSG;

@Controller
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private TemplateService templateService;
    @Autowired private UserRepository userRepo;



    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        UserModel user = new UserModel();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register")
    public String processRegister(
            Model model
            //, BindingResult result
            , RedirectAttributes redirectAttributes
            , @ModelAttribute UserModel registerUser
    ) {
        //UserModel user = new UserModel();
        log.info("registerUser: " + registerUser);
        UserModel existingUser = userService.findUserByUsername(registerUser.getUsername());

        String errorMsg = "";
        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            errorMsg = "There is already an account registered with the same email";
            //result.rejectValue("email", null, "There is already an account registered with the same email");
        }
        if (!errorMsg.isEmpty()) {//if(result.hasErrors()){
            model.addAttribute("user", registerUser);
            redirectAttributes.addFlashAttribute(FLASH_ATTR_ERRORMSG, errorMsg);
            return "redirect:/register";
        }

        userService.saveUser(registerUser);
        return "redirect:/register?success";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        UserModel user = new UserModel();
        log.info("GET Login");
        return "login";
    }

    @PostMapping("/login")
    public String loginAndReturnToLoginPage(Model model) {
        log.info("POST Login");
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String getProfilePage(
            Model model
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof org.springframework.security.core.userdetails.User) {
                org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) principal;
                String username = user.getUsername();
                UserModel userModel = userService.findUserByUsername(username);
                if (userModel != null) {
                    log.info("UserModel: " + userModel);
                    model.addAttribute("userObj", userModel);
                    if(model.getAttribute("profileObj") == null){
                        model.addAttribute("profileObj", userModel);
                    }
                }
            }
        }
        return "userprofile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            Model model
            ,@ModelAttribute UserModel userProfile
    ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof org.springframework.security.core.userdetails.User) {
                org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) principal;
                String username = user.getUsername();
                UserModel userModel = userService.findUserByUsername(username);
                if (userModel != null) {
                    log.info("UserModel: " + userModel);
                    userModel.setName(userProfile.getName());
                    userModel.setCompany(userProfile.getCompany());
                    userModel.setJob(userProfile.getJob());
                    userModel.setCountry(userProfile.getCountry());
                    userModel.setAddress(userProfile.getAddress());
                    userModel.setPhone(userProfile.getPhone());
                    userModel.setEmail(userProfile.getEmail());
                    userModel.setFacebookLink(userProfile.getFacebookLink());
                    userModel.setInstagramLink(userProfile.getInstagramLink());
                    userModel.setLinkedinLink(userProfile.getLinkedinLink());
                    userModel.setEmail(userProfile.getEmail());
                    userRepo.saveAndFlush(userModel);
                }
            }
        }
        return "redirect:/profile";
    }


}
