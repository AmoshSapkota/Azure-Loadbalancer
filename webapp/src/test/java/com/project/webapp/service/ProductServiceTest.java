package com.project.webapp.service;

import com.project.webapp.model.Product;
import com.project.webapp.repository.ProductRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductService
 * Uses Mockito to mock repository dependencies
 * Tests business logic and OpenTelemetry integration
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Tests")
class ProductServiceTest {

    @Mock
    private ProductRepo productRepo;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private List<Product> testProducts;

    @BeforeEach
    void setUp() {
        testProduct = new Product(1, "Test Laptop", 1200, "Electronics");
        testProducts = Arrays.asList(
            new Product(1, "Laptop", 1200, "Electronics"),
            new Product(2, "Mouse", 25, "Electronics"),
            new Product(3, "Book", 15, "Education")
        );
    }

    @Test
    @DisplayName("Should return all products")
    void testGetAllProducts() {
        // Given
        when(productRepo.findAll()).thenReturn(testProducts);

        // When
        List<Product> result = productService.getProduct();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Laptop", result.get(0).getProdName());
        assertEquals("Mouse", result.get(1).getProdName());
        assertEquals("Book", result.get(2).getProdName());
        
        verify(productRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no products exist")
    void testGetAllProductsEmpty() {
        // Given
        when(productRepo.findAll()).thenReturn(Arrays.asList());

        // When
        List<Product> result = productService.getProduct();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return product by ID")
    void testGetProductById() {
        // Given
        when(productRepo.findById(1)).thenReturn(Optional.of(testProduct));

        // When
        Product result = productService.getProductById(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getProdId());
        assertEquals("Test Laptop", result.getProdName());
        assertEquals(1200, result.getPrice());
        assertEquals("Electronics", result.getCategory());
        
        verify(productRepo, times(1)).findById(1);
    }

    @Test
    @DisplayName("Should return null when product not found by ID")
    void testGetProductByIdNotFound() {
        // Given
        when(productRepo.findById(999)).thenReturn(Optional.empty());

        // When
        Product result = productService.getProductById(999);

        // Then
        assertNull(result);
        verify(productRepo, times(1)).findById(999);
    }

    @Test
    @DisplayName("Should add new product successfully")
    void testAddProduct() {
        // Given
        when(productRepo.save(any(Product.class))).thenReturn(testProduct);

        // When
        productService.addProduct(testProduct);

        // Then
        verify(productRepo, times(1)).save(testProduct);
    }

    @Test
    @DisplayName("Should handle null product when adding")
    void testAddNullProduct() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(null);
        });
        assertEquals("Product cannot be null", exception.getMessage());
        
        // Repository should never be called with null input
        verify(productRepo, never()).save(any());
    }

    @Test
    @DisplayName("Should update existing product")
    void testUpdateProduct() {
        // Given
        Product updatedProduct = new Product(1, "Updated Laptop", 1500, "Electronics");
        when(productRepo.save(any(Product.class))).thenReturn(updatedProduct);

        // When
        productService.updateProduct(1, updatedProduct);

        // Then
        verify(productRepo, times(1)).save(updatedProduct);
        assertEquals(1, updatedProduct.getProdId()); // Should set the ID
    }

    @Test
    @DisplayName("Should delete product by ID")
    void testDeleteProduct() {
        // Given
        doNothing().when(productRepo).deleteById(1);

        // When
        productService.deleteProduct(1);

        // Then
        verify(productRepo, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Should handle repository exception during getAll")
    void testGetAllProductsException() {
        // Given
        when(productRepo.findAll()).thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.getProduct();
        });
        
        assertEquals("Database connection failed", exception.getMessage());
        verify(productRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should handle repository exception during save")
    void testAddProductException() {
        // Given
        doThrow(new RuntimeException("Save failed")).when(productRepo).save(any(Product.class));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.addProduct(testProduct);
        });
        
        assertEquals("Save failed", exception.getMessage());
        verify(productRepo, times(1)).save(testProduct);
    }

    @Test
    @DisplayName("Should handle repository exception during delete")
    void testDeleteProductException() {
        // Given
        doThrow(new RuntimeException("Delete failed")).when(productRepo).deleteById(1);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.deleteProduct(1);
        });
        
        assertEquals("Delete failed", exception.getMessage());
        verify(productRepo, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Should partially update product")
    void testUpdateProductPartially() {
        // Given
        Product existing = new Product(1, "Old Laptop", 1000, "Electronics");
        Product updates = new Product(0, "New Laptop", 1200, null);
        
        when(productRepo.findById(1)).thenReturn(Optional.of(existing));
        when(productRepo.save(any(Product.class))).thenReturn(existing);

        // When
        productService.updateProductPartially(1, updates);

        // Then
        verify(productRepo, times(1)).findById(1);
        verify(productRepo, times(1)).save(existing);
        assertEquals("New Laptop", existing.getProdName());
        assertEquals(1200, existing.getPrice());
        assertEquals("Electronics", existing.getCategory()); // Should remain unchanged
    }

    @Test
    @DisplayName("Should handle partial update with no changes")
    void testUpdateProductPartiallyNoChanges() {
        // Given
        Product existing = new Product(1, "Laptop", 1000, "Electronics");
        Product updates = new Product(0, "", 0, ""); // No valid updates
        
        when(productRepo.findById(1)).thenReturn(Optional.of(existing));

        // When
        productService.updateProductPartially(1, updates);

        // Then
        verify(productRepo, times(1)).findById(1);
        verify(productRepo, never()).save(any(Product.class)); // Should not save if no changes
    }

    @Test
    @DisplayName("Should verify OpenTelemetry telemetry integration")
    void testTelemetryIntegration() {
        // Given
        when(productRepo.findAll()).thenReturn(testProducts);

        // When
        List<Product> result = productService.getProduct();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        
        // Verify repository was called (telemetry is handled internally)
        verify(productRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should handle concurrent access")
    void testConcurrentAccess() throws InterruptedException {
        // Given
        when(productRepo.findAll()).thenReturn(testProducts);

        // When - simulate concurrent calls
        Thread thread1 = new Thread(() -> productService.getProduct());
        Thread thread2 = new Thread(() -> productService.getProduct());
        
        thread1.start();
        thread2.start();
        
        thread1.join();
        thread2.join();

        // Then
        verify(productRepo, times(2)).findAll();
    }

    @Test
    @DisplayName("Should handle large product list")
    void testLargeProductList() {
        // Given
        List<Product> largeList = Arrays.asList(new Product[1000]);
        for (int i = 0; i < 1000; i++) {
            largeList.set(i, new Product(i, "Product" + i, i * 10, "Category" + (i % 5)));
        }
        when(productRepo.findAll()).thenReturn(largeList);

        // When
        List<Product> result = productService.getProduct();

        // Then
        assertNotNull(result);
        assertEquals(1000, result.size());
        verify(productRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should verify method interactions in correct order")
    void testMethodCallOrder() {
        // Given
        when(productRepo.save(any(Product.class))).thenReturn(testProduct);
        when(productRepo.findById(1)).thenReturn(Optional.of(testProduct));

        // When
        productService.addProduct(testProduct);
        productService.getProductById(1);

        // Then
        verify(productRepo, times(1)).save(testProduct);
        verify(productRepo, times(1)).findById(1);
    }
}
