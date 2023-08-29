package com.smart.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.User;

public interface UserRepository extends JpaRepository<User, Integer>{
	
	@Query("select u from User u where u.email = :email") 
	public User getUserByUserName(@Param("email") String email);
	
	// in 'Query' one 1st email is class variable and 2nd one is dynamic one.
	// both should match.
	// And for getting dynamic content(from web that user enter) we use '@param'.
	
	// jb hm 'getUserByUserName()' call kareneg koi email dal ke then wo passed email
	// 2nd email ke jagah pe aa jayega and then us user ka sara data apne pass aa jayega.
	
	
}
