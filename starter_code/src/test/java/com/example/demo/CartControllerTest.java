package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
public class CartControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private CartController cartController;

    private User testUser;
    private Cart testCart;
    private Item testItem;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        // Create a test user and set up a cart for the user
        testUser = new User();
        testUser.setUsername("testuser");
        testCart = new Cart();
        testUser.setCart(testCart);

        // Create a test item
        testItem = new Item();
        testItem.setId(1L);
        testItem.setName("test item");
        testItem.setPrice(BigDecimal.valueOf(100.0));
    }

    @Test
    public void testAddToCart_UserNotFound() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("nonexistentuser");
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepository.findByUsername("nonexistentuser")).thenReturn(null);

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testAddToCart_ItemNotFound() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testuser");
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testAddToCart_Success() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testuser");
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, testCart.getItems().size()); // Verify two items were added
    }

    @Test
    public void testRemoveFromCart_UserNotFound() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("nonexistentuser");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepository.findByUsername("nonexistentuser")).thenReturn(null);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testRemoveFromCart_ItemNotFound() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testuser");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testRemoveFromCart_Success() {
        // Add item to cart before removing it
        testCart.addItem(testItem);
        testCart.addItem(testItem); // Add the item twice

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testuser");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, testCart.getItems().size()); // One item should be removed
    }
}
