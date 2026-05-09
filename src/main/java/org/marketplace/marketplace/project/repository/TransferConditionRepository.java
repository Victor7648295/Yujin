package org.marketplace.marketplace.project.repository;

import org.marketplace.marketplace.project.model.TransferType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransferConditionRepository extends JpaRepository<TransferType, Long> {

    Optional<TransferType> findByName(String name);
}
