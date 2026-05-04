package org.marketplace.marketplace.project.service;

import lombok.RequiredArgsConstructor;
import org.marketplace.marketplace.project.model.Product;
import org.marketplace.marketplace.project.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервис страницы детального просмотра объявления: отдаёт продукт
 * по идентификатору контроллеру публичной страницы /product/{id}.
 */
@Service
@RequiredArgsConstructor
public class ProductPageService {

    private final ProductRepository productRepository;

    public Optional<Product> getProduct(Long id) {
        return productRepository.findById(id);
    }
}
