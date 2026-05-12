package org.trasfermarkt.project.repository;


import org.trasfermarkt.project.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    Optional<Transfer> findById(Long id);

    Transfer save(Transfer transfer);

    void deleteById(Long id);

    boolean existsById(Long id);

    List<Transfer> findByStatus_StatusName(String statusName);

    @Query("SELECT DISTINCT p.region FROM Transfer p ORDER BY p.region")
    List<String> findAllRegions();

    @Query("SELECT DISTINCT p.category FROM Transfer p ORDER BY p.category")
    List<String> findAllCategories();

    @Query("SELECT p FROM Transfer p WHERE " +
            "p.status.id = 8 AND " +
            "(:region IS NULL OR :region = '' OR p.region = :region) AND " +
            "(:category IS NULL OR :category = '' OR p.category = :category) AND " +
            "(:condition IS NULL OR :condition = '' OR p.condition.name = :condition) AND " +
            "(:priceFrom IS NULL OR p.price >= :priceFrom) AND " +
            "(:priceTo IS NULL OR p.price <= :priceTo)")
    List<Transfer> searchProducts(@Param("region") String region,
                                  @Param("category") String category,
                                  @Param("condition") String condition,
                                  @Param("priceFrom") Integer priceFrom,
                                  @Param("priceTo") Integer priceTo);

    @Query("SELECT p FROM Transfer p WHERE " +
            "(:region IS NULL OR p.region = :region) AND " +
            "(:category IS NULL OR p.category = :category) AND " +
            "(:condition IS NULL OR p.condition.name = :condition) AND " +
            "(:priceFrom IS NULL OR p.price >= :priceFrom) AND " +
            "(:priceTo IS NULL OR p.price <= :priceTo)")
    List<Transfer> filterProducts(@Param("region") String region,
                                  @Param("category") String category,
                                  @Param("condition") String condition,
                                  @Param("priceFrom") Integer priceFrom,
                                  @Param("priceTo") Integer priceTo);

    @Query("SELECT p FROM Transfer p WHERE p.status.id = 8 AND " +
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Transfer> searchByTitle(@Param("query") String query);

    List<Transfer> findByStatusId(Long statusId);

    List<Transfer> findByUser_IdAndStatus_StatusName(Long userId, String statusName);

    List<Transfer> findByUser_IdOrderByStatus_StatusNameAsc(Long userId);

    @Query("SELECT p FROM Transfer p WHERE p.createdAt >= :from AND p.createdAt < :to " +
            "ORDER BY p.status.statusName ASC, p.createdAt DESC")
    List<Transfer> findPublishedBetween(@Param("from") LocalDateTime from,
                                        @Param("to") LocalDateTime to);
}
