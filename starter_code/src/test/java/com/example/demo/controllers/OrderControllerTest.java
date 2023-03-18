package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private UserController userController;

    private OrderController orderController;
    private final OrderRepository orderRepoMock = mock(OrderRepository.class);
    private final UserRepository userRepoMock = mock(UserRepository.class);
    @Before
    public void setUp(){
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepoMock);
        TestUtils.injectObjects(orderController, "userRepository", userRepoMock);
        userController = new UserController();
        Cart cart = new Cart();
        User user = new User(0, "testUser", "Geeks@portal20",  cart);
        when(userRepoMock.findByUsername("testUser")).thenReturn(user);
        when(userRepoMock.findById(0L)).thenReturn(java.util.Optional.of(user));
        when(userRepoMock.findByUsername("someone")).thenReturn(null);

        Item item = new Item(1L, "Kobe Beef", BigDecimal.valueOf(35.5), "Wagyu beef from the Tajima strain of Japanese Black cattle," );
        List<Item> items = new ArrayList<Item>();
        items.add(item);
        cart.setId(0L);
        cart.setUser(user);
        cart.setItems(items);
        BigDecimal total = BigDecimal.valueOf(35.5);
        cart.setTotal(total);
        user.setCart(cart);
        when(userRepoMock.findByUsername("testUser")).thenReturn(user);
        when(userRepoMock.findByUsername("someone")).thenReturn(null);
    }
    @Test
    public void test_submit_success(){
        ResponseEntity<UserOrder> response = orderController.submit("testUser");
        assertNotNull(response);
        assertNotEquals("Not Found", response.getStatusCode().getReasonPhrase());
        assertEquals("OK", response.getStatusCode().getReasonPhrase());
    }
    @Test
    public void test_submit_failure(){
        ResponseEntity<UserOrder> response = orderController.submit("aniss");
        assertNotNull(response);
        assertNotEquals("OK", response.getStatusCode().getReasonPhrase());
        assertEquals("Not Found", response.getStatusCode().getReasonPhrase());
    }

    @Test
    public void test_submit_failed_user_doesnt_exist_failed(){
        ResponseEntity<List<UserOrder>> submitedOrderByUser = orderController.getOrdersForUser("aniss");
        assertNotNull(submitedOrderByUser);
        assertNotEquals("OK",(submitedOrderByUser.getStatusCode().getReasonPhrase()));
        assertEquals("Not Found", submitedOrderByUser.getStatusCode().getReasonPhrase());
    }
    @Test
    public void test_get_Orders_For_User_failed(){
        ResponseEntity<List<UserOrder>> ordersForUser = orderController.getOrdersForUser("aniss");
        assertNotNull(ordersForUser);
        assertNotEquals("OK",(ordersForUser.getStatusCode().getReasonPhrase()));
        assertEquals("Not Found", ordersForUser.getStatusCode().getReasonPhrase());
    }
    @Test
    public void test_get_orders_for_user_success(){
        ResponseEntity<List<UserOrder>> ordersForUser = orderController.getOrdersForUser("testUser");
        assertNotNull(ordersForUser);
        assertNotEquals("Not Found", ordersForUser.getStatusCode().getReasonPhrase());
        assertEquals("OK", ordersForUser.getStatusCode().getReasonPhrase());
    }


}