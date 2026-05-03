package org.marketplace.marketplace.project.service;


import lombok.RequiredArgsConstructor;
import org.marketplace.marketplace.project.model.Product;
import org.marketplace.marketplace.project.model.ProductStatus;
import org.marketplace.marketplace.project.repository.ProductRepository;
import org.marketplace.marketplace.project.repository.ProductStatusRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductStatusRepository productStatusRepository;

    // Получить все товары
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Получить товар по ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    // Создать новый товар
    public Product createProduct(Product product) {
        if (product.getStatus() == null) {
            product.setStatus(getOrCreateStatus(ProductStatus.PENDING));
        }
        return productRepository.save(product);
    }

    // Обновить товар
    public Optional<Product> updateProduct(Long id, Product updatedProduct) {
        return productRepository.findById(id).map(existing -> {
            existing.setTitle(updatedProduct.getTitle());
            existing.setPrice(updatedProduct.getPrice());
            existing.setRegion(updatedProduct.getRegion());
            existing.setCategory(updatedProduct.getCategory());
            existing.setCondition(updatedProduct.getCondition());
            existing.setImagePath(updatedProduct.getImagePath());
            existing.setPhone(updatedProduct.getPhone());
            existing.setDescription(updatedProduct.getDescription());
            existing.setSellerName(updatedProduct.getSellerName());
            return productRepository.save(existing);
        });
    }

    // Удалить товар
    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Поиск с фильтрацией
    public List<Product> searchProducts(String region, String category,
                                        String condition, Integer priceFrom, Integer priceTo) {
        return productRepository.searchProducts(region, category, condition, priceFrom, priceTo);
    }

    // Получить все регионы
    public List<String> getAllRegions() {
        return productRepository.findAllRegions();
    }

    // Получить все категории
    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }

    // Фильтрация товаров
    public List<Product> filterProducts(String region, String category,
                                        String condition, Integer priceFrom,
                                        Integer priceTo) {
        // Обработка пустых строк
        region = (region != null && region.isEmpty()) ? null : region;
        category = (category != null && category.isEmpty()) ? null : category;
        condition = (condition != null && condition.isEmpty()) ? null : condition;

        return productRepository.filterProducts(region, category, condition, priceFrom, priceTo);
    }

    // Поиск по названию
    public List<Product> searchByName(String query) {
        if (query == null || query.isEmpty()) {
            return new ArrayList<>();
        }
        return productRepository.searchByTitle(query);
    }

    // Объявления, ожидающие модерации
    public List<Product> getPendingProducts() {
        return productRepository.findByStatus_StatusName(ProductStatus.PENDING);
    }

    // Одобрить объявление
    public boolean approveProduct(Long id) {
        return changeStatus(id, ProductStatus.APPROVED);
    }

    // Отклонить объявление
    public boolean rejectProduct(Long id) {
        return changeStatus(id, ProductStatus.REJECTED);
    }

    private boolean changeStatus(Long productId, String statusName) {
        return productRepository.findById(productId).map(product -> {
            product.setStatus(getOrCreateStatus(statusName));
            productRepository.save(product);
            return true;
        }).orElse(false);
    }

    private ProductStatus getOrCreateStatus(String statusName) {
        return productStatusRepository.findByStatusName(statusName).orElseGet(() -> {
            ProductStatus status = new ProductStatus();
            status.setStatusName(statusName);
            return productStatusRepository.save(status);
        });
    }
}
