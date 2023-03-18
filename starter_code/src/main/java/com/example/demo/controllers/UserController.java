package com.example.demo.controllers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	final Logger log = LoggerFactory.getLogger(UserController.class);



	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		if(userRepository.findById(id).isPresent()){
			log.error("Cannot find user with id: {}", id);
			return ResponseEntity.of(userRepository.findById(id));
		}else{
			log.debug("UserController.findById called with id {}", id);
			return  ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);

		if(user== null){
			log.error("Cannot find user with username: {}", username);
			return ResponseEntity.notFound().build();
		}
		log.debug("UserController.findByUserName called with username {}", username);
		return  ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		try{
			String username = createUserRequest.getUsername();
			String password = createUserRequest.getPassword();
		}catch (NullPointerException e){
			log.error("User creation failed:" , e);
		}

		User user = new User(createUserRequest.getUsername());
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);

		if(!createUserRequest.getPassword().matches(regexPassword) ||
				!createUserRequest.getPassword().equals(createUserRequest.getConfirmedPassword())){
			log.error("User creation failed: user can't be created because password you enetered is not valid {}", createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();
		}
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		userRepository.save(user);


		log.info("User creation success: The user {} has been created", createUserRequest.getUsername());
		return ResponseEntity.ok(user);
	}
}
