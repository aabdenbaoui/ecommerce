package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;
    private final UserRepository userRepoMock = mock(UserRepository.class);
    private final CartRepository cartRepoMock = mock(CartRepository.class);
    private final BCryptPasswordEncoder encoderMock = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepoMock);
        TestUtils.injectObjects(userController, "cartRepository", cartRepoMock);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoderMock);
        Cart cart = new Cart();
        User user = new User(0, "testUser", "Geeks@portal20",  cart);
        when(userRepoMock.findByUsername("someone")).thenReturn(null);
        when(userRepoMock.findByUsername("testUser")).thenReturn(user);
        when(userRepoMock.findById(0L)).thenReturn(java.util.Optional.of(user));
        when(userRepoMock.findById(2L)).thenReturn(null);
    }
    @Test
    public void test_encoding_method(){
        when(encoderMock.encode("Geeks@portal20")).thenReturn("Geeks@portal20Hashed");
        CreateUserRequest createUserRequest = new CreateUserRequest("testUser", "Geeks@portal20", "Geeks@portal20" );
        ResponseEntity<User> response = userController.createUser(createUserRequest);
        User u = response.getBody();
        assertEquals("Geeks@portal20Hashed", u.getPassword());


    }
    @Test
    public void create_User_Succeed() {
        CreateUserRequest createUserRequest = new CreateUserRequest("testUser", "Geeks@portal20", "Geeks@portal20" );
        ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertNotNull(response);
        assertNotEquals("Bad Request", response.getStatusCode().getReasonPhrase());
        assertEquals("OK", response.getStatusCode().getReasonPhrase());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("testUser", u.getUsername());
    }

    @Test
    public void create_User_With_Mismatch_Password() {
        CreateUserRequest createUserRequest = new CreateUserRequest("testUser", "Geek@sportal20", "Geeks@portal20" );
         ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertNotNull(response);
        assertNotEquals("OK", response.getStatusCode().getReasonPhrase());
        assertEquals("Bad Request", response.getStatusCode().getReasonPhrase());
    }
    @Test
    public void create_User_With_No_Special_Character_No_Numbers() {
        CreateUserRequest createUserRequest = new CreateUserRequest("testUser", "Geekslportal20", "Geekslportal20" );
        final ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertNotNull(response);
        assertNotEquals("OK", response.getStatusCode().getReasonPhrase());
        assertEquals("Bad Request", response.getStatusCode().getReasonPhrase());
    }
    @Test
    public void create_User_With_Password_Meets_Requirement() {
        CreateUserRequest createUserRequest = new CreateUserRequest("testUser", "Geeks@portal20", "Geeks@portal20" );
        final ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertNotNull(response);
        assertNotEquals("Bad Request", response.getStatusCode().getReasonPhrase());
        assertEquals("OK",(response.getStatusCode().getReasonPhrase()));
    }

    @Test
    public void createUserWithShortPassword() {
        CreateUserRequest createUserRequest = new CreateUserRequest("testUser", "Gfg@20", "Gfg@20" );
        final ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertNotNull(response);
        assertNotEquals("OK",(response.getStatusCode().getReasonPhrase()));
        assertEquals("Bad Request", response.getStatusCode().getReasonPhrase());
    }
    @Test
    public void createUserWithVeryVeryLongPassword() {
        String password = "Geeks@portal20Geeks@portal20Geeks@portal20Geeks@portal20Geeks@portal20Geeks@portal20Geeks@portal20";
        CreateUserRequest r = new CreateUserRequest("testUser", password, password);
        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertNotEquals("OK",(response.getStatusCode().getReasonPhrase()));
        assertEquals("Bad Request", response.getStatusCode().getReasonPhrase());
    }

    @Test
    public void testFindByUserNameFound() {
        final ResponseEntity<User> response = userController.findByUserName("testUser");
        assertNotNull(response);
        assertNotEquals("Not Found", response.getStatusCode().getReasonPhrase());
        assertEquals("OK", response.getStatusCode().getReasonPhrase());
        User u = response.getBody();
        assertNotNull(u);
        assertNotEquals("aniss", u.getUsername());
        assertEquals("testUser", u.getUsername());
    }

    @Test
    public void testFindByUserNameNotFound() {
        final ResponseEntity<User> response = userController.findByUserName("aniss");
        assertNotNull(response);
        assertNotEquals("OK",(response.getStatusCode().getReasonPhrase()));
        assertEquals("Not Found", response.getStatusCode().getReasonPhrase());
    }

    @Test
    public void testFindUserByIdFound() {
        final ResponseEntity<User> response = userController.findById(0L);
        assertNotNull(response);
        assertNotEquals("Not Found", response.getStatusCode().getReasonPhrase());
        assertEquals("OK", response.getStatusCode().getReasonPhrase() );
        System.out.println(response.getStatusCode().getReasonPhrase());
        User userFound = response.getBody();
        assertNotNull(userFound);
        assertEquals(0, userFound.getId());
        assertEquals("testUser", userFound.getUsername());
    }

    @Test
    public void testfindByIdNotFound() {
        final ResponseEntity<User> response = userController.findById(1L);
        assertNotNull(response);
        assertNotEquals("OK",(response.getStatusCode().getReasonPhrase()));
        assertEquals("Not Found", response.getStatusCode().getReasonPhrase());

    }
}
