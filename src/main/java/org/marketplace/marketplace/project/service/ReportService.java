package org.marketplace.marketplace.project.service;

import lombok.RequiredArgsConstructor;
import org.marketplace.marketplace.project.model.Product;
import org.marketplace.marketplace.project.model.ProductStatus;
import org.marketplace.marketplace.project.model.ReportData;
import org.marketplace.marketplace.project.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ProductRepository productRepository;

    public ReportData generate(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Укажите обе даты");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Дата начала позже даты окончания");
        }

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime endExclusive = to.plusDays(1).atStartOfDay();
        List<Product> products = productRepository.findPublishedBetween(start, endExclusive);

        Map<String, List<Product>> groups = new LinkedHashMap<>();
        groups.put(ProductStatus.PENDING, new ArrayList<>());
        groups.put(ProductStatus.APPROVED, new ArrayList<>());
        groups.put(ProductStatus.REJECTED, new ArrayList<>());

        Map<String, String> descriptions = new LinkedHashMap<>();
        descriptions.put(ProductStatus.PENDING, "Ожидает модерации");
        descriptions.put(ProductStatus.APPROVED, "Активное объявление");
        descriptions.put(ProductStatus.REJECTED, "Отклонено модерацией");

        for (Product p : products) {
            String status = (p.getStatus() != null && p.getStatus().getStatusName() != null)
                    ? p.getStatus().getStatusName()
                    : "UNKNOWN";
            groups.computeIfAbsent(status, k -> new ArrayList<>()).add(p);
            if (p.getStatus() != null && p.getStatus().getDescription() != null
                    && !p.getStatus().getDescription().isBlank()) {
                descriptions.put(status, p.getStatus().getDescription());
            } else {
                descriptions.putIfAbsent(status, status);
            }
        }

        return new ReportData(from, to, products.size(), groups, descriptions);
    }
}
