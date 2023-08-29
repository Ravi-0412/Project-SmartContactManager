package com.smart.dao;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;


public interface ContactRepository extends JpaRepository<Contact, Integer>{
	
	// will show contact through pagination
	
	// writing a custom function to get all contacts by specific user
	
	// 1st go the user field and then go to id ti get the contacts of that user
	@Query("from Contact as c where c.user.id =:userId")
	public Page<Contact> findContactsByUser(@Param("userId")int userId, Pageable pageable);
	
	// search functionality
	public List<Contact> findByNameContainingAndUser(String name, User user);
	
}
