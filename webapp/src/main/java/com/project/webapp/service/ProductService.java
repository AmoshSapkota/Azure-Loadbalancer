package com.project.webapp.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.webapp.model.Product;
import com.project.webapp.repository.ProductRepo;

@Service
public class ProductService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    
    @Autowired
    ProductRepo repo;
    
    public List<Product> getProduct() {
        logger.info("Retrieving all products");
        List<Product> products = repo.findAll();
        logger.info("Successfully retrieved {} products", products.size());
        return products;
    }

    public Product getProductById(int prodId) {
        logger.info("Retrieving product with ID: {}", prodId);
        Product product = repo.findById(prodId).orElse(null);
        
        if (product != null) {
            logger.info("Successfully retrieved product: {} (Category: {}, Price: {})", 
                product.getProdName(), product.getCategory(), product.getPrice());
        } else {
            logger.warn("Product not found with ID: {}", prodId);
        }
        
        return product;
    }

    public Product addProduct(Product prod) {
        if (prod == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        
        logger.info("Adding new product: {} (Category: {}, Price: {})", 
            prod.getProdName(), prod.getCategory(), prod.getPrice());
        
        Product savedProduct = repo.save(prod);
        
        logger.info("Successfully added product: {} with ID: {}", 
            savedProduct.getProdName(), savedProduct.getProdId());
            
        return savedProduct;
    }
    
    public Product updateProduct(int prodId, Product prod) {
        logger.info("Updating product with ID: {} to name: {}", prodId, prod.getProdName());
        
        prod.setProdId(prodId);
        Product updatedProduct = repo.save(prod);
        
        logger.info("Successfully updated product with ID: {}", prodId);
        return updatedProduct;
    }
    
    public void deleteProduct(int prodId) {
        logger.info("Deleting product with ID: {}", prodId);
        repo.deleteById(prodId);
        logger.info("Successfully deleted product with ID: {}", prodId);
    }
    
    public Product updateProductPartially(int prodId, Product updates) {
        logger.info("Partially updating product with ID: {}", prodId);
        
        Product existing = getProductById(prodId);
        if (existing != null && existing.getProdId() != 0) {
            boolean hasUpdates = false;
            
            if (updates.getProdName() != null && !updates.getProdName().isEmpty()) {
                existing.setProdName(updates.getProdName());
                hasUpdates = true;
            }
            if (updates.getPrice() > 0) {
                existing.setPrice(updates.getPrice());
                hasUpdates = true;
            }
            if (updates.getCategory() != null && !updates.getCategory().isEmpty()) {
                existing.setCategory(updates.getCategory());
                hasUpdates = true;
            }
            
            if (hasUpdates) {
                Product savedProduct = repo.save(existing);
                logger.info("Successfully partially updated product with ID: {}", prodId);
                return savedProduct;
            } else {
                logger.info("No updates applied to product with ID: {}", prodId);
                return existing;
            }
        } else {
            logger.warn("Product not found for partial update with ID: {}", prodId);
            return null;
        }
    }
}