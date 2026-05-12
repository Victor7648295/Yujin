package org.trasfermarkt.project.config;

import lombok.RequiredArgsConstructor;
import org.trasfermarkt.project.model.TransferType;
import org.trasfermarkt.project.repository.TransferTypeRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StringToTransferTypeConverter implements Converter<String, TransferType> {

    private final TransferTypeRepository transferTypeRepository;

    @Override
    public TransferType convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        return transferTypeRepository.findByName(source).orElse(null);
    }
}
