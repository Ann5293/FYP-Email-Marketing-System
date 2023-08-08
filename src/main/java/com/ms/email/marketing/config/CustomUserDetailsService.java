package com.ms.email.marketing.config;

import com.ms.email.marketing.model.RoleModel;
import com.ms.email.marketing.model.UserModel;
import com.ms.email.marketing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel userModel = userRepository.findByUsername(username);
        if (userModel != null) {
            return new org.springframework.security.core.userdetails.User(
                    userModel.getUsername(),
                    userModel.getPassword(),
                    mapRolesToAuthorities(userModel.getRoles()));
        } else {
            throw new UsernameNotFoundException("Invalid username or password");
        }
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<RoleModel> roles) {
        Collection<? extends GrantedAuthority> mapRoles = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        return mapRoles;
    }
}
