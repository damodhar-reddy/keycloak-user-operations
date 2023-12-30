package com.testing.keycloak.entities;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "USER_DETAILS")


public class UserDetailsEntity { 

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "USER_ID")
	private Long userId;

	@Column(name = "USER_PERSONAL_NO")
	private String userPersonalNo;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "FIRST_NAME")
	private String firstName;

	@Column(name = "LAST_NAME")
	private String lastName;

	@Column(name = "MIDDLE_NAME")
	private String middleName;

	@Column(name = "GENDER")
	private String gender;

	@Column(name = "ACTIVE_FLAG")
	private Boolean activeFlag;

	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "UPDATED_DATE")
	private Date updatedDate;

	@Column(name = "UPDATED_BY")
	private String updatedBy;

	}