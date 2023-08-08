package com.ms.email.marketing.controller;

import com.ms.email.marketing.constant.AppConstant;
import com.ms.email.marketing.model.CustomerGroupModel;
import com.ms.email.marketing.model.CustomerListingModel;
import com.ms.email.marketing.model.EmailTemplateModel;
import com.ms.email.marketing.model.UserModel;
import com.ms.email.marketing.repository.*;
import com.ms.email.marketing.service.EmailService;
import com.ms.email.marketing.service.TemplateService;
import com.ms.email.marketing.service.UserService;
import com.ms.email.marketing.utils.CommonUtil;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

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
    @Autowired
    private CustomerGroupRepository customerGroupRepo;
    @Autowired
    private EmailTemplateRepository emailTemplateRepo;
    @Autowired
    private CustomerListingRepository customerListingRepo;
    @Autowired
    private CampaignRepository campaignRepo;
    @Autowired private UserRepository userRepo;

    @GetMapping("/demo")
    public String demo(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            // Assuming the principal is your user object
            // Customize this based on your UserDetailsService implementation
            log.info("principal: {}", principal);
            if (principal instanceof UserModel) {
                log.info("IS usermodel object");
                UserModel user = (UserModel) principal;
                model.addAttribute("test", user);
            }
        }
        return "demo";
    }

    @GetMapping("/dashboard")
    public String home(Model model) {
        Long getCountByTemplate = emailTemplateRepo.count();
        model.addAttribute("templateCount", getCountByTemplate);
        return "dashboard";
    }

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

    @GetMapping("/contact")
    public String showContactPage(Model model) {
        List<CustomerGroupModel> customerGroupList = customerGroupRepo.findAll();
        model.addAttribute("customerGroupList", customerGroupList);
        return "contact";
    }

    @GetMapping("/template")
    public String showEmailTemplate(Model model) {
        return "emailTemplateList";
    }

    @GetMapping("/template/create")
    public String showEmailTemplateCreate(Model model) {
        return "emailTemplateCreate";
    }

    @GetMapping("/template/{id}")
    public String editEmailTemplatePage(
            Model model
            , @PathVariable("id") String id
            , RedirectAttributes redirectAttributes
    ) {
        Optional<EmailTemplateModel> templateModel = emailTemplateRepo.findById(Long.valueOf(id));
        if (templateModel.isPresent()) {
            log.info("check template id: " + id);
            model.addAttribute("templateObj", templateModel.get());
            return "emailTemplateEdit";
        }
        redirectAttributes.addFlashAttribute(FLASH_ATTR_ERRORMSG, String.format("This email template is not available or not found, Template ID: %s", id));
        return "redirect:/template";
    }

    @PostMapping("/template/{id}")
    public String updateEmailTemplate(Model model, @PathVariable("id") String id) {
        Optional<EmailTemplateModel> templateModel = emailTemplateRepo.findById(Long.valueOf(id));
        if (templateModel.isPresent()) {
            log.info("check template id: " + id);
            model.addAttribute("templateObj", templateModel.get());
            return "emailTemplateEdit";
        }
        model.addAttribute("errorMsg", "Template not found or invalid");
        return "emailTemplateList";
    }

    @GetMapping("/contact/{id}")
    public String addCustomerIntoGroup(
            Model model
            , @PathVariable("id") String customerGroupId
    ) {
        Optional<CustomerGroupModel> customerGroupModel = customerGroupRepo.findById(Long.valueOf(customerGroupId));
        if(customerGroupModel.isPresent()){
            model.addAttribute("customerGroupObj", customerGroupModel.get());
            List<CustomerListingModel> customerListingModelList = customerListingRepo.findAllByCustomerGroupIdAndStatusNot(customerGroupModel.get().getId(), AppConstant.STATUS_DELETED);
            model.addAttribute("customerList", customerListingModelList);
            return "customer";
        }
        return "redirect:/contact";
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
