package com.ms.email.marketing.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="ems_role")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String name;

    @ManyToMany(mappedBy="roles")
    private List<UserModel> users;

    @Override
    public String toString() {
        return "RoleModel[id=" + id + ", name=" + name + "]";
    }
}
