package org.ecommerce.project.service;

import org.ecommerce.project.exceptions.ResourceNotFoundException;
import org.ecommerce.project.model.Category;
import org.ecommerce.project.model.Product;
import org.ecommerce.project.payload.ProductDTO;
import org.ecommerce.project.payload.ProductResponse;
import org.ecommerce.project.repositories.CategoryRepo;
import org.ecommerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImplementation implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO) {
        Category category = categoryRepo.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        // Product product1 = new Product(); // We dont need to create a new object, we can directly use the argument object product


        // THis below thing gives ERROR -> because the productDTO is NOT saved to the database and therefore productId is NOT generated, its null as of now
        // Product product = productRepository.findById(productDTO.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product not found", "Product", productDTO.getProductId()));


        // Calculating discount & Setting the discounted price to special price variable
        double discount = productDTO.getDiscount();
        double price = productDTO.getPrice();
        double splPrice = price - price * discount / 100;

        // Setting the category and the specialPrice to the product

        Product product = modelMapper.map(productDTO, Product.class);

        product.setCategory(category);
        product.setSpecialPrice(splPrice);
        product.setImage("default.png");


        Product productSaved = productRepository.save(product);
        return modelMapper.map(productSaved, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);

        return productResponse;
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId) {
        List<Product> products = productRepository.findProductByCategory_CategoryIDOrderByPriceAsc(categoryId);
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);

        return productResponse;
    }

    @Override
    public ProductResponse getAllProductsWrtKeywordSearch(String keyword) {
        List<Product> products = productRepository.findProductByProductNameContainingIgnoreCase(keyword);
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();
        return new ProductResponse(productDTOS); // IMP : See here we are setting productDTOS in ProductResponse class using its lombok all args constructor
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        Product productRetrieved = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found", "Product", productId));

        productRetrieved.setProductName(productDTO.getProductName());
        productRetrieved.setDescription(productDTO.getDescription());
        productRetrieved.setPrice(productDTO.getPrice());
        productRetrieved.setDiscount(productDTO.getDiscount());
        productRetrieved.setSpecialPrice(productDTO.getPrice() - productDTO.getPrice() * productDTO.getDiscount() / 100);
        productRetrieved.setQuantity(productDTO.getQuantity());

        Product savedProd = productRepository.save(productRetrieved);
        return modelMapper.map(savedProd, ProductDTO.class);

    }

    @Override
    public String deleteProduct(Long productId) {
        productRepository.deleteById(productId);
        return "Product with id " + productId + " deleted successfully!";
    }
}
