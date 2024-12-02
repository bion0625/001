package com.uj.stxtory.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "tb_upbit_key")
public class TbUPbitKey {
	@Id
    @GeneratedValue
	private Long id;
	
	@Column(name = "user_login_id", nullable = false)
	private String userLoginId;
	
	@Column(name = "access_key", nullable = false)
	private String accessKey;
	
	@Column(name = "secret_key", nullable = false)
	private String secretKey;
	
	@Column(name = "auto_on", nullable = false)
	private Boolean autoOn = true;
}
