package com.ms.email.marketing.controller;

import com.ms.email.marketing.constant.AppConstant;
import com.ms.email.marketing.model.CustomerGroupModel;
import com.ms.email.marketing.model.CustomerModel;
import com.ms.email.marketing.repository.CustomerGroupRepository;
import com.ms.email.marketing.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class CustomerController {
    private final Logger log = LoggerFactory.getLogger(CustomerController.class);
    @Autowired
    private CustomerGroupRepository customerGroupRepo;
    @Autowired
    private CustomerRepository customerRepo;

    @GetMapping("/contact")
    public String showContactPage(Model model) {
        List<CustomerGroupModel> customerGroupList = customerGroupRepo.findAll();
        model.addAttribute("customerGroupList", customerGroupList);
        return "customerGroupIndex";
    }

    @GetMapping("/contact/{id}")
    public String addCustomerIntoGroup(
            Model model
            , @PathVariable("id") String customerGroupId
            , RedirectAttributes redirectAttributes
    ) {
        Optional<CustomerGroupModel> customerGroupModel = customerGroupRepo.findById(Long.valueOf(customerGroupId));
        if (customerGroupModel.isPresent()) {
            model.addAttribute("customerGroupObj", customerGroupModel.get());
            List<CustomerModel> customerModelList = customerRepo.findAllByCustomerGroupIdAndStatusNot(customerGroupModel.get().getId(), AppConstant.STATUS_DELETED);
            model.addAttribute("customerList", customerModelList);
            return "customer";
        }
        redirectAttributes.addFlashAttribute(AppConstant.FLASH_ATTR_ERRORMSG, "The customer group ID not found: "+ customerGroupId);
        return "redirect:/contact";
    }
}
