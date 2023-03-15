package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private ItemRepository itemRepoMock = mock(ItemRepository.class);

    @Before
    public void setUp(){
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepoMock);
        Item item = new Item(1L, "Kobe Beef", BigDecimal.valueOf(35.5), "Wagyu beef from the Tajima strain of Japanese Black cattle," );

        when(itemRepoMock.findAll()).thenReturn(Collections.singletonList(item));
        when(itemRepoMock.findById(1L)).thenReturn(java.util.Optional.of(item));
        when(itemRepoMock.findByName("Kobe Beef")).thenReturn(Collections.singletonList(item));

    }

    @Test
    public void testGetItems() {
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals("OK", response.getStatusCode().getReasonPhrase());
        assertNotEquals("Not Found", response.getStatusCode().getReasonPhrase());
    }

    @Test
    public void testGetItemByIdSuccess() {
        ResponseEntity<Item> response = itemController.getItemById(1L);
        assertNotNull(response);
        assertNotEquals("Not Found", response.getStatusCode().getReasonPhrase());
        assertEquals("OK", response.getStatusCode().getReasonPhrase());
    }

    @Test
    public void testGetItemByIdNoTFound() {
        ResponseEntity<Item> response = itemController.getItemById(2L);
        assertNotNull(response);
        assertNotEquals("OK",(response.getStatusCode().getReasonPhrase()));
        assertEquals("Not Found", response.getStatusCode().getReasonPhrase());
    }

    @Test
    public void testGetItemByNameSuccess() {
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Kobe Beef");
        assertNotNull(response);
        assertNotEquals("Not Found", response.getStatusCode().getReasonPhrase());
        assertEquals("OK", response.getStatusCode().getReasonPhrase());

    }

    @Test
    public void getItemByNameNotFound() {
        ResponseEntity<List<Item>> response = itemController.getItemsByName("American Beef");
        assertNotNull(response);
        assertNotEquals("OK",(response.getStatusCode().getReasonPhrase()));
        assertEquals("Not Found", response.getStatusCode().getReasonPhrase());
    }
}
