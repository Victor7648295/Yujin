package org.trasfermarkt.project.service;

import lombok.RequiredArgsConstructor;
import org.trasfermarkt.project.model.Transfer;
import org.trasfermarkt.project.repository.TransferRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransferPageService {

    private final TransferRepository transferRepository;

    public Optional<Transfer> getProduct(Long id) {
        return transferRepository.findById(id);
    }
}
