package com.example.demo.controllers;

import com.example.demo.errorresponses.UserErrorResponse;
import com.example.demo.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {
	private String regexPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private CartRepository cartRepository;


	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		if(userRepository.findById(id).isPresent()){
			return ResponseEntity.of(userRepository.findById(id));
		}else{
			throw new UserException("USER WITH " + id  +" REQUESTED NOT FOUND");
		}
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		System.out.println(user);

		if(user== null){
			throw new UserException("USER REQUESTED NOT FOUND: " + username);
		}
		return  ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
//		System.out.println(createUserRequest.getPassword());
//		System.out.println(createUserRequest.getConfirmedPassword());
//		User user = new User();
//		User tempUser = userRepository.findByUsername(createUserRequest.getUsername());
//		if(tempUser != null){
//			throw new UserException("User already in the database");
//		}
//		if(!createUserRequest.getPassword().equals(createUserRequest.getConfirmedPassword())) {
//			throw new UserException("The passwords you entered don't match");
//		}
//		if(!createUserRequest.getPassword().matches(regexPassword)){
//			throw new UserException("The password you entered is week");
//		}
//		String encodedPassword = bCryptPasswordEncoder.encode(createUserRequest.getPassword());
//		String salt = encodedPassword.substring(7, encodedPassword.length() - 1).substring(0, 22);
//		Cart cart = new Cart();
//		user.setUsername(createUserRequest.getUsername());
//		user.setPassword(encodedPassword);
//		user.setSalt(salt);
//        user.setCart(cart);
//		cartRepository.save(cart);
//		userRepository.save(user);
//
//		return ResponseEntity.ok(user);
		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
		if(createUserRequest.getPassword().length()<7 ||
				!createUserRequest.getPassword().equals(createUserRequest.getConfirmedPassword())){
			//System.out.println("Error - Either length is less than 7 or pass and conf pass do not match. Unable to create ",
			//		createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();
		}
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		userRepository.save(user);
		return ResponseEntity.ok(user);
	}

	@ExceptionHandler
	public ResponseEntity<UserErrorResponse> handleException(UserException exc){
			UserErrorResponse error = new UserErrorResponse(HttpStatus.NOT_FOUND.value(), exc.getMessage(), System.currentTimeMillis());
			return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}
	
}
