package com.iyzico.challenge.controller;

import com.iyzico.challenge.entity.Product;
import com.iyzico.challenge.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> productAdd(@RequestBody Product product) {
        try {
            productService.productAdd(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(product.getName() + "is added successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> productRemove(@PathVariable Long id) {
        try {
            productService.productRemove(id);
            return ResponseEntity.status(HttpStatus.OK).body(id + " of product is removed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(e.getMessage());
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<String> productEdit(@RequestBody Product product, @PathVariable Long id) {
        try {
            productService.productEdit(product, id);
            return ResponseEntity.status(HttpStatus.OK).body(product.getName() + " edit successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(e.getMessage());
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Product>> productList() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(productService.productList());
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/buyProduct")
    public ResponseEntity<List<Product>> buyProduct(@RequestBody List<String> productName){
        try{
            return new ResponseEntity<>(productService.buyProduct(productName), HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity(e.getMessage(),HttpStatus.EXPECTATION_FAILED);
        }
    }

}
