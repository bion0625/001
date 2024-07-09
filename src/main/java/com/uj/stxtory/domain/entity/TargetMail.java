package com.uj.stxtory.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
public class TargetMail extends Base {

    @Id
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    public TargetMail(String email) {
        this.email = email;
    }
}
