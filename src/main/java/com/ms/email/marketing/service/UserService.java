package com.ms.email.marketing.service;

import com.ms.email.marketing.model.RoleModel;
import com.ms.email.marketing.model.UserModel;
import com.ms.email.marketing.model.request.LoginRequest;
import com.ms.email.marketing.repository.RoleRepository;
import com.ms.email.marketing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveUser(UserModel userModel) {
        RoleModel role = roleRepository.findByName("ROLE_ADMIN");
        if (role == null) {
            role = checkRoleExist();
        }
        userModel.setPassword(passwordEncoder.encode(userModel.getPassword()));
        userModel.setRoles(Arrays.asList(role));
        userRepository.save(userModel);
    }

    public UserModel findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    private RoleModel checkRoleExist() {
        RoleModel role = new RoleModel();
        role.setName("ROLE_ADMIN");
        return roleRepository.save(role);
    }

}
