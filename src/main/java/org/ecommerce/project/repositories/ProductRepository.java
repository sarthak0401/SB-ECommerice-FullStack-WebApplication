package org.ecommerce.project.repositories;

import org.ecommerce.project.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findProductByCategory_CategoryIDOrderByPriceAsc(Long categoryCategoryID);

    List<Product> findProductByProductNameContainingIgnoreCase(String productName);
}
