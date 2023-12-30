package com.testing.keycloak.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.testing.keycloak.entities.UserDetailsEntity;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetailsEntity, Long> {

	@Query(value = "select * from user_details where user_personal_no = :userPersonalNo and active_flag = true",nativeQuery = true)
	public UserDetailsEntity findActiveUsersByUserPersonalNo(String userPersonalNo);
	
	@Query(value = "select * from user_details where user_personal_no = :userPersonalNo",nativeQuery = true)
	public UserDetailsEntity findByUserPersonalNo(String userPersonalNo);
}