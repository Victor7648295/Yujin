package org.marketplace.marketplace.project.service;

import lombok.RequiredArgsConstructor;
import org.marketplace.marketplace.project.model.Transfer;
import org.marketplace.marketplace.project.repository.TransferRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервис страницы детального просмотра объявления: отдаёт продукт
 * по идентификатору контроллеру публичной страницы /product/{id}.
 */
@Service
@RequiredArgsConstructor
public class TransferPageService {

    private final TransferRepository transferRepository;

    public Optional<Transfer> getProduct(Long id) {
        return transferRepository.findById(id);
    }
}
