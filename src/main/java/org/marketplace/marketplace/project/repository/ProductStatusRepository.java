package org.marketplace.marketplace.project.repository;

import org.marketplace.marketplace.project.model.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductStatusRepository extends JpaRepository<ProductStatus, Long> {

    Optional<ProductStatus> findByStatusName(String statusName);
}
