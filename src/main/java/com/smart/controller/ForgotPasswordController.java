package com.smart.controller;

import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ForgotPasswordController {
	
	Random random = new Random(0000); // minium value(inclusive)
	
	@GetMapping("/forgot")
	public String openEmailForm()
	{
		return "forgot_email_form";
	}

	@PostMapping("send-otp")
	public String sendOTP(@RequestParam("email") String email)
	{	
		
		System.out.println("EMAIL: "+email);
		
		// generating 4 digit otp
		
		int otp = random.nextInt(10000);  // maximum value(exclusive)
		
		System.out.println("OTP: "+otp);
		
		// write code for sending otp to email
		
		return "verify_otp";
	}
	
}
