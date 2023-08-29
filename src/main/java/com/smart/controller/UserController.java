package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user") // is controller ke under 'urls' ko handle karne ke liye phle '/user' lagana
							// hoga. use 'Request' only not 'Get'.
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bcryptPasswordEncoder;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	// method for adding common data to response
	// to send the user data to each page starting with '/user' we made a common function to avoid writing code each time
	// This handler will automatically add user details
	
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {

		// us user se related info like user details, contacts show karne ke liye hmko
		// us user ka 'id' chahiye.
		String userName = principal.getName(); // will give the 'userName' specified by you at login time.
		System.out.println("USERNAME : " + userName); // will print 'email' in our case.

		// get the user using username(email) from database
		// we have to use function 'getUserByName' function of 'UserRepository'(dao)

		User user = userRepository.getUserByUserName(userName);

		System.out.println("User Details: " + user);

		// user data ko ' given url' pe bhejne ke liye
		model.addAttribute("user", user);

	}
	
	// Dashboard Home
	@RequestMapping("/index")
	public String dashboard(Model model) {

		/*
		// us user se related info like user details, contacts show karne ke liye hmko
		// us user ka 'id' chahiye.
		String userName = principal.getName(); // will give the 'userName' specified by you at login time.
		System.out.println("USERNAME : " + userName); // will print 'email' in our case.

		// get the user using username(email) from database
		// we have to use function 'getUserByName' function of 'UserRepository'(dao)

		User user = userRepository.getUserByUserName(userName);

		System.out.println("User Details: " + user);

		// user data ko ' given url' pe bhejne ke liye
		model.addAttribute("user", user); 
		*/
		
		model.addAttribute("title", "Add Contact");
		return "normal/user_dashboard"; // give the location after 'templates'.
	}

	// open add form handler

	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	
	// processing add contact form
	
	//The @RequestParam is used to extract query parameters while
	// @PathVariable is used to extract data right from the URI.
	
	// Here:
	// @ModelAttribute: for getting contact detail except image
	//(wo filed aayega isme jiska html field ka naam and contact entity atribute ka naam match karega).
	
	// @RequestParam: for separately handling any filed or filed doesn't match.
//	 Here for getting file details(image here) and will store in file say 'file'.
	// will acess the 'image'
	
	// Principal: for getting 'userName'.
	
	@PostMapping("/process-contact")
	public String processsContact(
			@ModelAttribute Contact contact, 
			@RequestParam("profileImage") MultipartFile file, 
			Principal principal, HttpSession session) {
		
		try { 
		// first get the user to which these contact will belong
		String userName = principal.getName(); 
		User user = userRepository.getUserByUserName(userName);
		
		// processing and uploading the file
		if(file.isEmpty())
		{	
			contact.setImage("contact.png"); // default img but not able to add
			System.out.println("File is empty!");
			// then print your message)
		}
		else {
			// add the file to specific folder and update the name of file to contact.
			
			// 1st get the file name and setImage. image that contact me chla jayega
			contact.setImage(file.getOriginalFilename());
			
			// ab kahan us image ko select karna h.
			// iske liye 1st create a 'new ClassPathResource for ClassLoader usage' to get a set of overloaded 'copy()' methods.
			File saveFile = new ClassPathResource("static/img").getFile();
			
			// find the path separately for adding int 'Files.copy()' as target
			// yahan 'file.getOriginalFilename()' use kiye h 
			// agar file name sbka unique chahiye to 'fileName' ke saath 'id' ya koi cheez add kar do jisse unique ban jaye.
			Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			
			//Now copy that file: Files.copy(source, target, optional)
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			
			System.out.println("Image uploaded successfully!");
		}
		
		// first set the user to whom this contact belong
		contact.setUser(user);
		
		// now add cur contact details to 'contacts' filed of user.
		user.getContacts().add(contact); // will contain all details of contact except 'image'.
		
		// save this user(not contact) to database
		// contact will automatically get added to user contacts and 'contact' entity also
		userRepository.save(user);
		
		System.out.println("Contact Details: "+contact);
		
		System.out.println("Contact added to data base");
		
		// success message
		session.setAttribute("message", new Message("Successfully Added, Add more !!", "alert-success"));
		
		}catch(Exception e) {
			System.err.println("Error: "+e.getMessage());
			e.printStackTrace();
			
			// error message
			session.setAttribute("message", new Message("Something went wrong!!", "alert-danger"));
		}
		return "normal/add_contact_form";
	}
	
	// show contacts handler
	
	// per page = 5([n])
	// current page = 0 ([page])
	
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page,  Model model, Principal principal)
	{
		
		model.addAttribute("title", "Add Contact");
		
		// getting user id
		// principal will give the userName and from username we can get the id
		String userName = principal.getName();
		
		User user = userRepository.getUserByUserName(userName);
		
		// pagination
		// per page = 5([n])
		// current page = 0 ([page])
		Pageable pageable = PageRequest.of(page, 5);
		
		// now we can get the contact using pagination
		Page<Contact> contacts = contactRepository.findContactsByUser(user.getId(), pageable);
		
		model.addAttribute("contacts", contacts); // sending the contact
		model.addAttribute("currentPage", page);  // 'page':cur page no
		model.addAttribute("totalPages", contacts.getTotalPages());
		
		
		return "normal/show_contacts";
	}
	
	// showing particular contact detail
	
	@GetMapping("/contact/{cId}")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal)
	{	
		// dynamic 'cId' ko fetch karne ke liye 'pathVariable'
		// 'cId' ke through contact detail ko fetch kar lenge and isko show kar denge
		
		Optional<Contact> contactOptional = contactRepository.findById(cId);
		Contact contact = contactOptional.get(); // us contact ka sara detail isme aa jayega
		
		// koi user dusre user ka contact nhi dekh paye url me 'id' change karke iske liye 'model' se data ek check ke through bhejenge
		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);
		
		// agar user ka id and contact ka user id match kiya then hi hm data ko send karenge.
		// iska matlab usere apna contact ko hoi dekhna cha rha nhi to koi error message show kar denge
		
		// Aother way of solving this.
		
		if(user.getId()== contact.getUser().getId())
		{
			// then user apna hi contact dekhna cha rha
			model.addAttribute("contact", contact);
		}
		
		return "normal/contact_detail";
	}
	
	// delete contact handler

	@RequestMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cId, HttpSession session, Principal principal)
	{
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		
		// to delete the image , you have to do it manually.
		// do it later
		
		// deleting contact 
		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);
		
		user.getContacts().remove(contact); // ye equals to function of 'contact' call karega
		// and agar is contact ki id kisi bhi contact ki id se match karega tb user ka conatct me se isko remove kar dega
		// ab update user contact i.e user ko database me save kar dena h.
		userRepository.save(user);
		
		// print message through session
		session.setAttribute("message", new Message("Contact deleted successfully...", "alert-success"));
		
		return "redirect:/user/show-contacts/0";
	}
	
	
	// open update form handler
	
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cId,  Model model)
	{	
		model.addAttribute("title", "Update Contact");
		
		Optional<Contact> contactOptional = contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		
		model.addAttribute("contact", contact);
		return "normal/update_form";
	}
	
	// update contact handler
	
	@PostMapping("/process-update")
	public String updateHandler(
			@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file, 
			Principal principal, HttpSession session
			)
	{	
		
		try {
			// old contact detail that is saved in database
			Contact oldContactDetail = contactRepository.findById(contact.getcId()).get();
			
			if(!file.isEmpty())
			{ 
				// means agar koi nya photo user chose kar rha tb ye steps karna h
				
				
				//1)  first delete the current photo (not working)
				// first you need the path
				
//				File deleteFile = new ClassPathResource("static/img").getFile();
//				File file1 = File(deleteFile, oldContactDetail.getImage());
//				file1.delete();
				
				// 2) Then update with new photo. just same we did while adding contact.
				
				File saveFile = new ClassPathResource("static/img").getFile();
				
				// find the path separately for adding int 'Files.copy()' as target
				// yahan 'file.getOriginalFilename()' use kiye h 
				// agar file name sbka unique chahiye to 'fileName' ke saath 'id' ya koi cheez add kar do jisse unique ban jaye.
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				//Now copy that file: Files.copy(source, target, optional)
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				contact.setImage(file.getOriginalFilename());
				
				
			}
			else 
			{
				// purana wala ko hi set kar dena h
				contact.setImage(oldContactDetail.getImage());
			}
			// first get the user to which these contact will belong from database
			String userName = principal.getName(); 
			User user = userRepository.getUserByUserName(userName);
			contact.setUser(user);
			contactRepository.save(contact);
			
			session.setAttribute("message", new Message("Contact updated successfully...", "alert-success"));
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("CONTACT NAME: "+contact.getName());
		System.out.println("CONTACT ID: "+contact.getcId());
		return "redirect:/user/contact/"+contact.getcId();
	}
	
	@GetMapping("/profile")
	public String yourProfile(Model model)
	{	
		model.addAttribute("title", "Profile Page");
		return "normal/profile";
	}
	
	
	// open settings handler
	
	@GetMapping("/settings")
	public String openSettings()
	{
		return "normal/settings";
	}
	
	
	// change password handler
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword, 
			@RequestParam("newPassword") String newPassword,
			Principal principal,
			HttpSession session
			)
	{
		
		System.out.println("OLD PASSWORD: "+oldPassword);
		System.out.println("NEW PASSWORD: "+newPassword);
		
		
		// first getting the user
		String userName = principal.getName(); 
		User user = userRepository.getUserByUserName(userName);
		
		if(bcryptPasswordEncoder.matches(oldPassword, user.getPassword()))
		{
			// change password
			user.setPassword(bcryptPasswordEncoder.encode(newPassword));
			userRepository.save(user);
			
			session.setAttribute("message", new Message("Password changed successfully...", "alert-success"));
		}
		else 
		{
			//error ...
			session.setAttribute("message", new Message("Please enter correct old Password...", "alert-danger"));
			return "redirect:/user/settings";
		}
			
		return "redirect:/user/index";
		
	}
	
	
	
}
