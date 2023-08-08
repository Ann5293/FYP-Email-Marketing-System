package com.ms.email.marketing.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Collection;
import java.util.UUID;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name="ems_user")
public class UserModel {

    @Id
//    @GeneratedValue(generator = "uuid2")
//    @GenericGenerator(name = "uuid2", strategy = "uuid2")
//    @Column(name = "uuid", updatable = false, nullable = false, columnDefinition = "VARCHAR(36)")
//    @Type(type = "uuid-char")
//    private UUID id;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String username;
    private String password;
    private String company;
    private String job;
    private String country;
    private String address;
    private String phone;
    private String facebookLink;
    private String instagramLink;
    private String linkedinLink;

    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL
    )
    @JoinTable(
            name = "ems_user_role",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id")
    )
    private Collection<RoleModel> roles;

}
