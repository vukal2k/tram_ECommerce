package com.example.demo;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ItemControllerTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemController itemController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetItems() {
        Item item1 = new Item(1L, "Item1", new BigDecimal("10.00"), "Description1");
        Item item2 = new Item(2L, "Item2", new BigDecimal("20.00"), "Description2");
        List<Item> items = Arrays.asList(item1, item2);

        when(itemRepository.findAll()).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItems();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals("Item1", response.getBody().get(0).getName());
        assertEquals("Item2", response.getBody().get(1).getName());

        verify(itemRepository, times(1)).findAll();
    }

    @Test
    public void testGetItemById() {
        Item item = new Item(1L, "Item1", new BigDecimal("10.00"), "Description1");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Item1", response.getBody().getName());

        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetItemByIdNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetItemsByName() {
        Item item = new Item();
        item.setName("Item1");
        item.setId(1L);
        item.setPrice(new BigDecimal("10.00"));
        item.setDescription( "Description1");
        List<Item> items = Arrays.asList(item);

        when(itemRepository.findByName("Item1")).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Item1");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals("Item1", response.getBody().get(0).getName());

        verify(itemRepository, times(1)).findByName("Item1");
    }

    @Test
    public void testGetItemsByNameNotFound() {
        when(itemRepository.findByName("NonExistent")).thenReturn(null);

        ResponseEntity<List<Item>> response = itemController.getItemsByName("NonExistent");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(itemRepository, times(1)).findByName("NonExistent");
    }
}
