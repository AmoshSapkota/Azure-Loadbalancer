package com.project.webapp.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Product entity
 * Tests entity creation, getters, setters, and business logic
 */
@DisplayName("Product Model Tests")
class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
    }

    @Test
    @DisplayName("Should create product with default constructor")
    void testDefaultConstructor() {
        assertNotNull(product);
        assertEquals(0, product.getProdId());
        assertNull(product.getProdName());
        assertEquals(0, product.getPrice());
        assertNull(product.getCategory());
    }

    @Test
    @DisplayName("Should create product with all args constructor")
    void testAllArgsConstructor() {
        Product product = new Product(1, "Laptop", 1000, "Electronics");
        
        assertEquals(1, product.getProdId());
        assertEquals("Laptop", product.getProdName());
        assertEquals(1000, product.getPrice());
        assertEquals("Electronics", product.getCategory());
    }

    @Test
    @DisplayName("Should set and get product ID")
    void testProdIdSetterGetter() {
        product.setProdId(123);
        assertEquals(123, product.getProdId());
    }

    @Test
    @DisplayName("Should set and get product name")
    void testProdNameSetterGetter() {
        product.setProdName("Smartphone");
        assertEquals("Smartphone", product.getProdName());
    }

    @Test
    @DisplayName("Should set and get product price")
    void testPriceSetterGetter() {
        product.setPrice(500);
        assertEquals(500, product.getPrice());
    }

    @Test
    @DisplayName("Should set and get product category")
    void testCategorySetterGetter() {
        product.setCategory("Mobile");
        assertEquals("Mobile", product.getCategory());
    }

    @Test
    @DisplayName("Should handle null product name")
    void testNullProductName() {
        product.setProdName(null);
        assertNull(product.getProdName());
    }

    @Test
    @DisplayName("Should handle empty product name")
    void testEmptyProductName() {
        product.setProdName("");
        assertEquals("", product.getProdName());
    }

    @Test
    @DisplayName("Should handle negative price")
    void testNegativePrice() {
        product.setPrice(-100);
        assertEquals(-100, product.getPrice());
    }

    @Test
    @DisplayName("Should handle zero price")
    void testZeroPrice() {
        product.setPrice(0);
        assertEquals(0, product.getPrice());
    }

    @Test
    @DisplayName("Should test equals and hashCode with Lombok")
    void testEqualsAndHashCode() {
        Product product1 = new Product(1, "Laptop", 1000, "Electronics");
        Product product2 = new Product(1, "Laptop", 1000, "Electronics");
        Product product3 = new Product(2, "Mouse", 50, "Electronics");
        
        assertEquals(product1, product2);
        assertNotEquals(product1, product3);
        assertEquals(product1.hashCode(), product2.hashCode());
    }

    @Test
    @DisplayName("Should test toString method with Lombok")
    void testToString() {
        Product product = new Product(1, "Laptop", 1000, "Electronics");
        String toString = product.toString();
        
        assertTrue(toString.contains("prodId=1"));
        assertTrue(toString.contains("prodName=Laptop"));
        assertTrue(toString.contains("price=1000"));
        assertTrue(toString.contains("category=Electronics"));
    }

    @Test
    @DisplayName("Should handle special characters in product name")
    void testSpecialCharactersInName() {
        String specialName = "Product@#$%^&*()";
        product.setProdName(specialName);
        assertEquals(specialName, product.getProdName());
    }

    @Test
    @DisplayName("Should handle very long product name")
    void testLongProductName() {
        String longName = "A".repeat(1000);
        product.setProdName(longName);
        assertEquals(longName, product.getProdName());
        assertEquals(1000, product.getProdName().length());
    }
}
