package org.ecommerce.project.service;

import org.ecommerce.project.payload.ProductDTO;
import org.ecommerce.project.payload.ProductResponse;

public interface ProductService {
    ProductDTO addProduct(Long categoryId, ProductDTO productDTO);

    ProductResponse getAllProducts();

    ProductResponse getProductsByCategory(Long categoryId);

    ProductResponse getAllProductsWrtKeywordSearch(String keyword);

    ProductDTO updateProduct(ProductDTO productDTO, Long productId);

    String deleteProduct(Long productId);
}
