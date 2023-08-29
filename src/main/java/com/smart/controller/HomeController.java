package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	// home  page handler
	
	@GetMapping("/")
	public String home(Model model)
	{	
		model.addAttribute("title", "Home- Smart Contact Manager");
		return "home";
	}
	
	// about  page handler
	
	@GetMapping("/about")
	public String about(Model model)
	{	
		model.addAttribute("title", "About- Smart Contact Manager");
		return "about";
	}
	
	// signup  page handler
	
	@GetMapping("/signup")
	public String signup(Model model)
	{	
		model.addAttribute("title", "Register- Smart Contact Manager");
		model.addAttribute("user", new User());   // data ko store karne ke liye signup page se
		return "signup";
	}
	
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, @RequestParam(value= "agreement", defaultValue ="false") boolean agreement, Model model, HttpSession session)
	{
		// 'agreement wala field 'user' me nhi h isliye usko alag se handle karenge(@RequestParam). jo entity k apart nhi h wo sbko aise hi acess karte h.
		//, and Model ka object liye h data ko bhejne ke liye.
		// 'message' bhejne ke liye 'session' ka object chahiye
		
		try {
			if(!agreement) {
				System.out.println("you have not agreed terms and conditions");
				throw new Exception("you have not agreed terms and conditions");
			}
			
			if(result.hasErrors())
			{	
				System.out.println("eroor : "+result.toString());
				model.addAttribute("user", user);
				return "signup";
			}
			
			// if everything goes correct then only all these lines will execute otherwise 'catch' block will execute.
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			
			// adding the password
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			
			System.out.println("Agreement"+agreement);
			System.out.println("User"+user);
			
			// saving into database
			System.out.println("saving into databse");
			User user1= this.userRepository.save(user);
			
			model.addAttribute("user", new User());  // will send blank object , just like the blank page we see after registration
			
			// give message to user
			session.setAttribute("message", new Message("Successfully Registered !!", "alert-success"));
			System.out.println("saved one"+user1.toString());
			return "signup"; // blank sign page
			
		} catch(Exception e)
		{
			e.printStackTrace();
			// to show message to user on page
			model.addAttribute("user", user); // show the details of user only like what they have entered
			// give message to user
			session.setAttribute("message", new Message("something went wrong !!"+e.getMessage(), "alert-danger"));
			// again show the 'signup' page with all the details he has entered
			return "signup";
		}
		
		
	}
	
	// handler for custom login
	@GetMapping("/signin")
	public String customLogin(Model model)
	{	
		model.addAttribute("title", "Login Page");
		return "login";
	}
	  
	
}
