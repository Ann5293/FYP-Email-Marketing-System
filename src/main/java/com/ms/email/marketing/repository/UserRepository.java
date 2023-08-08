package com.ms.email.marketing.repository;

import com.ms.email.marketing.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserModel, UUID> {

    List<UserModel> findByUsernameAndPassword(String username, String password);

    UserModel findByUsername(String username);
}
