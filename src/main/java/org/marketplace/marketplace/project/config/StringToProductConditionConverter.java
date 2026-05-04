package org.marketplace.marketplace.project.config;

import lombok.RequiredArgsConstructor;
import org.marketplace.marketplace.project.model.ProductCondition;
import org.marketplace.marketplace.project.repository.ProductConditionRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StringToProductConditionConverter implements Converter<String, ProductCondition> {

    private final ProductConditionRepository productConditionRepository;

    @Override
    public ProductCondition convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        return productConditionRepository.findByName(source).orElse(null);
    }
}
