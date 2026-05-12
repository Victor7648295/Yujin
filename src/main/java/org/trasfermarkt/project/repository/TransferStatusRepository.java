package org.trasfermarkt.project.repository;

import org.trasfermarkt.project.model.TransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransferStatusRepository extends JpaRepository<TransferStatus, Long> {

    Optional<TransferStatus> findByStatusName(String statusName);
}
