package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest{

    private CartController cartController;
    private UserRepository userRepoMock = mock(UserRepository.class);
    private CartRepository cartRepoMock = mock(CartRepository.class);
    private ItemRepository itemRepoMock = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "itemRepository", itemRepoMock);
        TestUtils.injectObjects(cartController, "userRepository", userRepoMock);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepoMock);
        Cart cart = new Cart();
        User user = new User(0, "testUser", "Geeks@portal20",  cart);
        Item item = new Item(1L, "Kobe Beef", BigDecimal.valueOf(35.5), "Wagyu beef from the Tajima strain of Japanese Black cattle," );
        when(userRepoMock.findByUsername("testUser")).thenReturn(user);
        when(itemRepoMock.findById(1L)).thenReturn(java.util.Optional.of(item));

    }

    @Test
    public void test_add_To_cart_success() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest("testUser", 1L, 1);
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertNotNull(response);
        assertNotEquals("Not Found", response.getStatusCode().getReasonPhrase());
        assertEquals("OK", response.getStatusCode().getReasonPhrase());
        Cart c = response.getBody();
        assertNotNull(c);
        assertNull(c.getUser());
        assertNull(c.getId());
        assertEquals(1, c.getItems().size());
        assertEquals(BigDecimal.valueOf(35.5), c.getTotal());
    }



    @Test
    public void test_add_To_cart_failed_item_not_found() {
        ModifyCartRequest modifyCartRequest= new ModifyCartRequest("testUser", 2L, 1);
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertNotNull(response);
        Cart c = response.getBody();
        assertNull(c);
        assertNotEquals("OK",(response.getStatusCode().getReasonPhrase()));
        assertEquals("Not Found", response.getStatusCode().getReasonPhrase());
    }
    @Test
    public void test_add_To_cart_failed_user_not_found() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest("karim", 1L, 2);
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertNotNull(response);
        assertNotEquals("OK",(response.getStatusCode().getReasonPhrase()));
        assertEquals("Not Found", response.getStatusCode().getReasonPhrase());
    }

    @Test
    public void test_remove_from_cart_success() {
        ModifyCartRequest modifyCartRequestToAdd = new ModifyCartRequest("testUser", 1L, 3);
        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequestToAdd);
        assertNotNull(response);
        Cart cartAfterAdd = response.getBody();
        assertEquals(3, cartAfterAdd.getItems().size());
        assertEquals(BigDecimal.valueOf(106.5), cartAfterAdd.getTotal());
        assertNotEquals("Not Found", response.getStatusCode().getReasonPhrase());
        assertEquals("OK", response.getStatusCode().getReasonPhrase());

        ModifyCartRequest modifyCartRequestToRemove = new ModifyCartRequest("testUser", 1L, 1);
        response = cartController.removeFromcart(modifyCartRequestToRemove);
        assertNotNull(response);
        assertNotEquals("Not Found", response.getStatusCode().getReasonPhrase());
        assertEquals("OK", response.getStatusCode().getReasonPhrase());
        Cart cartAfterRemove = response.getBody();
        assertNotNull(cartAfterRemove);
        assertEquals(2, cartAfterRemove.getItems().size());
        assertEquals(BigDecimal.valueOf(71.0), cartAfterRemove.getTotal());
    }

    @Test
    public void test_remove_from_cart_not_found_user() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest("laila", 1L, 1);
        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        assertNotNull(response);
        assertNotEquals("OK",(response.getStatusCode().getReasonPhrase()));
        assertEquals("Not Found", response.getStatusCode().getReasonPhrase());
    }

    @Test
    public void test_remove_from_cart_invalid_item() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest("testUser", 2L, 2);
        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        assertNotNull(response);
        assertNotEquals("OK",(response.getStatusCode().getReasonPhrase()));
        assertEquals("Not Found", response.getStatusCode().getReasonPhrase());
    }

}