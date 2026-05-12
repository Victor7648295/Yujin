package org.trasfermarkt.project.repository;

import org.trasfermarkt.project.model.TransferType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransferTypeRepository extends JpaRepository<TransferType, Long> {

    Optional<TransferType> findByName(String name);
}
