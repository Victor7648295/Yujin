package org.marketplace.marketplace.project.service;

import lombok.RequiredArgsConstructor;
import org.marketplace.marketplace.project.model.TransferStatus;
import org.marketplace.marketplace.project.model.ReportData;
import org.marketplace.marketplace.project.model.Transfer;
import org.marketplace.marketplace.project.repository.TransferRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Формирует отчёт по объявлениям за заданный диапазон дат: загружает
 * опубликованные в этом интервале продукты, группирует их по статусу
 * (PENDING / APPROVED / REJECTED) и собирает соответствующие
 * человекочитаемые описания статусов в {@link ReportData}.
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransferRepository transferRepository;

    public ReportData generate(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Укажите обе даты");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Дата начала позже даты окончания");
        }

        LocalDateTime start = from.atStartOfDay();
        LocalDateTime endExclusive = to.plusDays(1).atStartOfDay();
        List<Transfer> transfers = transferRepository.findPublishedBetween(start, endExclusive);

        Map<String, List<Transfer>> groups = new LinkedHashMap<>();
        groups.put(TransferStatus.PENDING, new ArrayList<>());
        groups.put(TransferStatus.APPROVED, new ArrayList<>());
        groups.put(TransferStatus.REJECTED, new ArrayList<>());

        Map<String, String> descriptions = new LinkedHashMap<>();
        descriptions.put(TransferStatus.PENDING, "Ожидает модерации");
        descriptions.put(TransferStatus.APPROVED, "Активное объявление");
        descriptions.put(TransferStatus.REJECTED, "Отклонено модерацией");

        for (Transfer p : transfers) {
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

        return new ReportData(from, to, transfers.size(), groups, descriptions);
    }
}
