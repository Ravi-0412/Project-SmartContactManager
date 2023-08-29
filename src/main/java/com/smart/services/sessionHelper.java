package com.smart.services;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Component
public class sessionHelper {
	public void removeMessageFromSession() {
		try {
			
			// remove karne ke liye session ka object chahiye and then typecast karna hoga 'ServletRequestAttributes' me
			// uske liye itna karna hoga
			HttpSession session=((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getSession();
			session.removeAttribute("message");
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
