package com.ms.email.marketing.repository;

import com.ms.email.marketing.model.RoleModel;
import com.ms.email.marketing.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<RoleModel, Long> {
    RoleModel findByName(String name);
}
