package com.example.customerservice.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.Permission;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "role_master",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_role_master_role_name",
                        columnNames = "role_name"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "role_name", nullable = false, length = 100)
    private String roleName;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "Active";

//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "role_permission",
//            joinColumns = @JoinColumn(name = "role_id"),
//            inverseJoinColumns = @JoinColumn(name = "permission_id")
//    )
//    private Set<Permission> permissions = new HashSet<>();
}
