package com.project.webapp.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.webapp.model.Product;
import com.project.webapp.service.ProductService;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import jakarta.annotation.PostConstruct;

@RestController
public class ProductController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    
    @Autowired
    ProductService service;
    
    @Autowired
    private Tracer tracer;
    
    @Autowired
    private Meter meter;
    
    // Custom metrics for business logic
    private LongCounter productOperationsCounter;
    private LongHistogram productPriceHistogram;
    
    @PostConstruct
    public void initializeMetrics() {
        productOperationsCounter = meter
            .counterBuilder("product_operations_total")
            .setDescription("Total number of product operations")
            .build();
            
        productPriceHistogram = meter
            .histogramBuilder("product_price_distribution")
            .setDescription("Distribution of product prices")
            .setUnit("currency")
            .ofLongs()
            .build();
            
        logger.info("Custom product metrics initialized");
    }

    @RequestMapping("/products")
    public ResponseEntity<List<Product>> getProduct() {
        Span span = tracer.spanBuilder("product.list").startSpan();
        try (Scope scope = span.makeCurrent()) {
            logger.info("GET /products - Retrieving all products");
            
            List<Product> products = service.getProduct();
            
            // Custom metrics and span attributes
            productOperationsCounter.add(1, Attributes.builder()
                .put("operation", "list")
                .put("status", "success")
                .build());
            
            span.setAttribute("product.count", products.size());
            span.setAttribute("operation.type", "list_all");
            
            logger.info("GET /products - Successfully returned {} products", products.size());
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            productOperationsCounter.add(1, Attributes.builder()
                .put("operation", "list")
                .put("status", "error")
                .build());
            logger.error("GET /products - Error retrieving products", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } finally {
            span.end();
        }
    }

    @RequestMapping("/products/{prodId}")
    public ResponseEntity<Product> getProductById(@PathVariable int prodId) {
        try {
            logger.info("GET /products/{} - Retrieving product by ID", prodId);
            Product product = service.getProductById(prodId);
            
            if (product != null && product.getProdId() != 0) {
                logger.info("GET /products/{} - Product found: {}", prodId, product.getProdName());
                return ResponseEntity.ok(product);
            } else {
                logger.warn("GET /products/{} - Product not found", prodId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("GET /products/{} - Error retrieving product", prodId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@RequestBody Product prod) {
        Span span = tracer.spanBuilder("product.create").startSpan();
        try (Scope scope = span.makeCurrent()) {
            // Basic validation
            if (prod.getProdName() == null || prod.getProdName().trim().isEmpty()) {
                span.setAttribute("validation.error", "empty_name");
                productOperationsCounter.add(1, Attributes.builder()
                    .put("operation", "create")
                    .put("status", "validation_error")
                    .build());
                return ResponseEntity.badRequest().build();
            }
            if (prod.getPrice() < 0) {
                span.setAttribute("validation.error", "negative_price");
                productOperationsCounter.add(1, Attributes.builder()
                    .put("operation", "create")
                    .put("status", "validation_error")
                    .build());
                return ResponseEntity.badRequest().build();
            }
            
            Product savedProduct = service.addProduct(prod);
            
            // Custom metrics and span attributes
            productOperationsCounter.add(1, Attributes.builder()
                .put("operation", "create")
                .put("status", "success")
                .put("category", prod.getCategory() != null ? prod.getCategory() : "unknown")
                .build());
            
            productPriceHistogram.record((long) prod.getPrice(), Attributes.builder()
                .put("category", prod.getCategory() != null ? prod.getCategory() : "unknown")
                .put("operation", "create")
                .build());
            
            span.setAttribute("product.id", savedProduct.getProdId());
            span.setAttribute("product.name", savedProduct.getProdName());
            span.setAttribute("product.price", savedProduct.getPrice());
            span.setAttribute("product.category", savedProduct.getCategory());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        } catch (Exception e) {
            span.recordException(e);
            span.setAttribute("error", true);
            productOperationsCounter.add(1, Attributes.builder()
                .put("operation", "create")
                .put("status", "error")
                .build());
            return ResponseEntity.badRequest().build();
        } finally {
            span.end();
        }
    }
    
    @PutMapping("/products/{prodId}")
    public ResponseEntity<Product> updateProduct(@PathVariable int prodId, @RequestBody Product prod){
        Product updatedProduct = service.updateProduct(prodId, prod);
        return ResponseEntity.ok(updatedProduct);
    }
    
    @DeleteMapping("/products/{prodId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int prodId){
        service.deleteProduct(prodId);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/products/{prodId}")
    public ResponseEntity<Product> updateProductPartially(@PathVariable int prodId, @RequestBody Product prod) {
        Product updatedProduct = service.updateProductPartially(prodId, prod);
        return ResponseEntity.ok(updatedProduct);
    }
}