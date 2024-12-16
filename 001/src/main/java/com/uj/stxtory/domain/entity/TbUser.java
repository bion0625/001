package com.uj.stxtory.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class TbUser extends Base {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "user_login_id", nullable = false)
    private String userLoginId;

    @Column(name = "user_password", nullable = false)
    private String userPassword;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "user_role", nullable = false)
    private String userRole = "USER";

    @Column(name = "user_email", nullable = true)
    private String userEmail;

    @Column(name = "user_phone", nullable = true)
    private String userPhone;
}
