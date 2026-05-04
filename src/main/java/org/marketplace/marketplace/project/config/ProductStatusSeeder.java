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
        ensureStatus(ProductStatus.PENDING, "Ожидает модерации");
        ensureStatus(ProductStatus.APPROVED, "Активное объявление");
        ensureStatus(ProductStatus.REJECTED, "Отклонено модерацией");
    }

    private void ensureStatus(String statusName, String description) {
        ProductStatus status = productStatusRepository.findByStatusName(statusName)
                .orElseGet(() -> {
                    ProductStatus s = new ProductStatus();
                    s.setStatusName(statusName);
                    return s;
                });
        if (description != null && !description.equals(status.getDescription())) {
            status.setDescription(description);
            productStatusRepository.save(status);
        } else if (status.getId() == null) {
            productStatusRepository.save(status);
        }
    }
}
