package org.ecommerce.project.controller;

import org.ecommerce.project.payload.ProductDTO;
import org.ecommerce.project.payload.ProductResponse;
import org.ecommerce.project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired
    private ProductService productService;

    // Finding all the products
    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts() {
        ProductResponse productResponse = productService.getAllProducts();
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    // Finding all the products corresponding to a category
    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductByCategory(@PathVariable Long categoryId) {
        ProductResponse productResponse = productService.getProductsByCategory(categoryId);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }


    // Finding all the products with the keyword search
    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getAllProducts_wrt_entered_keyword(@PathVariable String keyword) {
        ProductResponse productResponse = productService.getAllProductsWrtKeywordSearch(keyword);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }


    // Adding the product
    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody ProductDTO productDTO, @PathVariable Long categoryId) {
        ProductDTO productDTO_response = productService.addProduct(categoryId, productDTO);
        return new ResponseEntity<>(productDTO_response, HttpStatus.CREATED);
    }


    // Updating the user (accepting the Profile object and setting it, and in return ProductDTO)
    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateTheProduct(@RequestBody ProductDTO productDTO, @PathVariable Long productId) {
        ProductDTO productDTO_response = productService.updateProduct(productDTO, productId);
        return new ResponseEntity<>(productDTO_response, HttpStatus.OK);
    }


    // Delete a product
    @DeleteMapping("/admin/del/products/{productId}")
    public ResponseEntity<String> deleteProd(@PathVariable Long productId) {
        String response_msg = productService.deleteProduct(productId);
        return new ResponseEntity<>(response_msg, HttpStatus.OK);
    }

}
