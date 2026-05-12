package org.trasfermarkt.project.config;

import lombok.RequiredArgsConstructor;
import org.trasfermarkt.project.model.TransferStatus;
import org.trasfermarkt.project.repository.TransferStatusRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransferStatusSeeder implements CommandLineRunner {

    private final TransferStatusRepository transferStatusRepository;

    @Override
    public void run(String... args) {
        ensureStatus(TransferStatus.PENDING, "Ожидает модерации");
        ensureStatus(TransferStatus.APPROVED, "Активное объявление");
        ensureStatus(TransferStatus.REJECTED, "Отклонено модерацией");
    }

    private void ensureStatus(String statusName, String description) {
        TransferStatus status = transferStatusRepository.findByStatusName(statusName)
                .orElseGet(() -> {
                    TransferStatus s = new TransferStatus();
                    s.setStatusName(statusName);
                    return s;
                });
        if (description != null && !description.equals(status.getDescription())) {
            status.setDescription(description);
            transferStatusRepository.save(status);
        } else if (status.getId() == null) {
            transferStatusRepository.save(status);
        }
    }
}
