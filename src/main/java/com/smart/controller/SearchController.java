package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

@RestController
public class SearchController {
	
	@Autowired
	private UserRepository userRepository;  //to get current user
	
	@Autowired
	private ContactRepository contactRepository;

	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal)
	{
		System.out.println("query: "+query);
		
		String userName = principal.getName(); 
		User user = userRepository.getUserByUserName(userName);
		
		List<Contact> contacts = contactRepository.findByNameContainingAndUser(query, user);
		
		// now return the contact
		return ResponseEntity.ok(contacts);
	}
	
}
