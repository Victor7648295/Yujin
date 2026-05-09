package org.marketplace.marketplace.project.config;

import lombok.RequiredArgsConstructor;
import org.marketplace.marketplace.project.model.TransferCondition;
import org.marketplace.marketplace.project.repository.TransferConditionRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StringToTransferConditionConverter implements Converter<String, TransferCondition> {

    private final TransferConditionRepository transferConditionRepository;

    @Override
    public TransferCondition convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        return transferConditionRepository.findByName(source).orElse(null);
    }
}
