package com.project.webapp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.webapp.model.Product;
import com.project.webapp.service.ProductService;

@RestController
public class HomeController {

    @Autowired
    private ProductService productService;

    @GetMapping("/api/products")
    public List<Product> products() {
        return productService.getProduct();
    }
}
