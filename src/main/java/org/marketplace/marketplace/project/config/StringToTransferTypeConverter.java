package org.marketplace.marketplace.project.config;

import lombok.RequiredArgsConstructor;
import org.marketplace.marketplace.project.model.TransferType;
import org.marketplace.marketplace.project.repository.TransferConditionRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StringToTransferTypeConverter implements Converter<String, TransferType> {

    private final TransferConditionRepository transferConditionRepository;

    @Override
    public TransferType convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        return transferConditionRepository.findByName(source).orElse(null);
    }
}
