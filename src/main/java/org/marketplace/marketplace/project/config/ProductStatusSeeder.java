package org.marketplace.marketplace.project.config;

import lombok.RequiredArgsConstructor;
import org.marketplace.marketplace.project.model.ProductStatus;
import org.marketplace.marketplace.project.repository.ProductStatusRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductStatusSeeder implements CommandLineRunner {

    private final ProductStatusRepository productStatusRepository;

    @Override
    public void run(String... args) {
        ensureStatus(ProductStatus.PENDING);
        ensureStatus(ProductStatus.APPROVED);
        ensureStatus(ProductStatus.REJECTED);
    }

    private void ensureStatus(String statusName) {
        productStatusRepository.findByStatusName(statusName).orElseGet(() -> {
            ProductStatus status = new ProductStatus();
            status.setStatusName(statusName);
            return productStatusRepository.save(status);
        });
    }
}
