package com.smart.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.services.EmailService;

import jakarta.servlet.http.HttpSession;


@Controller
public class ForgotPasswordController {
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	BCryptPasswordEncoder bcryptPasswordEncoder;
	
	Random random = new Random(0000); // minium value(inclusive)
	
	@GetMapping("/forgot")
	public String openEmailForm()
	{
		return "forgot_email_form";
	}

	@PostMapping("send-otp")
	public String sendOTP(@RequestParam("email") String email, HttpSession session)
	{	
		
		System.out.println("EMAIL: "+email);
		
		// generating 4 digit otp
		
		int otp = random.nextInt(10000);  // maximum value(exclusive)
		
		System.out.println("OTP: "+otp);
		
		// specify the subject, message and sender
		String subject = "OTP from Smart Contact Manager";
		// sending message in form of html so better look
		String message = ""
				+ "<div style='border:1px solid #e2e2e2; padding:20px;'>" 
				+ "<h3>"
				+ "OTP is : "
				+ "<b>"+otp
				+"</b>"
				+"</h3>"
				+ "</div>"
				;
				
		String to = email;   // given email id pe bhejna h
		
		// send otp to given mail
		boolean flag = this.emailService.sendEmail(subject, message, to);
		
		if(flag) // if otp send successfully
		{	
			// store this otp in session or database to verify when user enter that otp.
			// here storing in session
			session.setAttribute("myotp", otp);
			// storing email also to check if user with given id exists or not in our database
			session.setAttribute("email", email);  
			
			return "verify_otp"; // then verify otp.
		}else
		{	// show the same with with error
			
			// send error message on forgot page where we are taking email id.
			session.setAttribute("message", "check your email id !!");
			return "forgot_email_form";
		}
		
		
	}
	
	// verify otp handler
	
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp, HttpSession session )
	{ 
		// user jo otp dalege wo hm store kar liye h 'otp' me and
		// jo otp gya h wo session me h and dono ko match karna h.
		
		// getting actual otp sent from session
		int myOtp = (Integer) session.getAttribute("myotp");
		// gettting the entered email from session
		String email= (String)session.getAttribute("email");
		
		if(myOtp == otp)
		{	
			// check if this is valid user i.e this user exist in our database
			// for this get the user by username, if not none means valid user else not.
			User user = this.userRepository.getUserByUserName(email);
			
			if(user == null)
			{
				// send error message
				session.setAttribute("message", "No user with this email!!");
				return "forgot_email_form";
			}
			else
			{
				// send new password form
				
			}
			return "password_change_form";
		}else
		{	
			// print error message
			session.setAttribute("message", "You have entered wrong otp");
			return "verify_otp";
		}
		
	}
	
	// change password handler
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newPassword") String newpassword, HttpSession session)
	{	
		// first get the user for which we have to change the password
		String email= (String)session.getAttribute("email");
		User user = this.userRepository.getUserByUserName(email);
		
		// set the password
		user.setPassword(this.bcryptPasswordEncoder.encode(newpassword));
		// now save the user in database
		this.userRepository.save(user);
		
		// redirecting to login page with message
		return "redirect:/signin?change=password changed succesfully...."; 
		// 'signin' wala url pe chla jayega ans hmara message 
		// i.e 'password changed succesfully' 'change' variable me rhega
		
		
		
	}
	
}
