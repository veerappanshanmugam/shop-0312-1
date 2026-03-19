package com.example.ecommerce.product;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecommerce.exception.NotFoundException;
import com.example.ecommerce.inventory.InventoryRepository;
import com.example.ecommerce.model.Inventory;
import com.example.ecommerce.model.Product;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;

    public ProductController(ProductRepository productRepository,
                             CategoryRepository categoryRepository,
                             InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        // Verify category exists if provided
        if (product.getCategoryId() != null) {
            categoryRepository.findById(product.getCategoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found"));
        }

        Product saved = productRepository.save(product);

        // Initialize inventory for new product
        Inventory inventory = new Inventory(saved.getId(), 0, 0);
        inventoryRepository.save(inventory);

        return saved;
    }

    @GetMapping("/{product_id}")
    public Product getProduct(@PathVariable("product_id") Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    @GetMapping
    public List<Product> listProducts() {
        return productRepository.findAll();
    }
}
