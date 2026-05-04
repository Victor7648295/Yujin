package org.marketplace.marketplace.project.repository;

import org.marketplace.marketplace.project.model.ProductCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductConditionRepository extends JpaRepository<ProductCondition, Long> {

    Optional<ProductCondition> findByName(String name);
}
