package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.dao.UserRepository;
import com.smart.entities.User;

public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		// fetching user details from database

		User user = userRepository.getUserByUserName(username);
		
		if(user == null)
		{
			throw new UsernameNotFoundException("can not find user !!");
		}
		
		// ab is 'user' ko 'CustomerUserDetails' ka object bna ke return kar dena h.
		CustomerUserDetails customerUserDeatils = new CustomerUserDetails(user);

		return customerUserDeatils;
	}

}
