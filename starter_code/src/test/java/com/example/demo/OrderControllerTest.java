package com.example.demo;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    private User testUser;
    private UserOrder testOrder;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");

        Cart testCart = new Cart();
        testCart.setTotal(BigDecimal.valueOf(100));
        testCart.setItems(new ArrayList<>());
        testUser.setCart(testCart);

        when(userRepository.findByUsername("testuser")).thenReturn(testUser);

        testOrder = UserOrder.createFromCart(testCart);
        testOrder.setId(1L);
        testOrder.setUser(testUser);
    }

    @Test
    public void testSubmitOrder_UserFound() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(orderRepository.save(any(UserOrder.class))).thenReturn(testOrder);

        // Act
        ResponseEntity<UserOrder> response = orderController.submit("testuser");

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(testOrder.getTotal(), Objects.requireNonNull(response.getBody()).getTotal());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(orderRepository, times(1)).save(any(UserOrder.class));
    }

    @Test
    public void testSubmitOrder_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(null);

        // Act
        ResponseEntity<UserOrder> response = orderController.submit("nonexistentuser");

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        verify(userRepository, times(1)).findByUsername("nonexistentuser");
        verify(orderRepository, never()).save(any(UserOrder.class));
    }

    @Test
    public void testGetOrdersForUser_UserFound() {
        // Arrange
        List<UserOrder> orders = new ArrayList<>();
        orders.add(testOrder); // Add a test order
        when(userRepository.findByUsername("testuser")).thenReturn(testUser);
        when(orderRepository.findByUser(testUser)).thenReturn(orders);

        // Act
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testuser");

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(testOrder, response.getBody().get(0));
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(orderRepository, times(1)).findByUser(testUser);
    }

    @Test
    public void testGetOrdersForUser_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(null);

        // Act
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("nonexistentuser");

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        verify(userRepository, times(1)).findByUsername("nonexistentuser");
        verify(orderRepository, never()).findByUser(any(User.class));
    }
}
