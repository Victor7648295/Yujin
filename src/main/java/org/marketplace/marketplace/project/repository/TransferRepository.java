package org.marketplace.marketplace.project.repository;


import org.marketplace.marketplace.project.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    //Поиск по id
    Optional<Transfer> findById(Long id);

    //Сохранение продукта
    Transfer save(Transfer transfer);

    //Удаление по id
    void deleteById(Long id);

    //Проверить сузествует ли продукт
    boolean existsById(Long id);

    // Поиск по названию (частичное совпадение)
    List<Transfer> findByTitleContainingIgnoreCase(String title);

    // Поиск по статусу модерации
    List<Transfer> findByStatus_StatusName(String statusName);

    // Поиск по региону
    List<Transfer> findByRegion(String region);

    // Поиск по категории
    List<Transfer> findByCategory(String category);

    // Поиск по состоянию (по названию)
    List<Transfer> findByCondition_Name(String name);

    // Поиск по цене в диапазоне
    List<Transfer> findByPriceBetween(Integer minPrice, Integer maxPrice);

    // Получить все уникальные регионы
    @Query("SELECT DISTINCT p.region FROM Transfer p ORDER BY p.region")
    List<String> findAllRegions();

    // Получить все уникальные категории
    @Query("SELECT DISTINCT p.category FROM Transfer p ORDER BY p.category")
    List<String> findAllCategories();

    // Сложный поиск с фильтрацией (JPQL)
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

    // Поиск с фильтрацией
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

    // Поиск по названию
    @Query("SELECT p FROM Transfer p WHERE p.status.id = 8 AND " +
            "LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Transfer> searchByTitle(@Param("query") String query);

    List<Transfer> findByStatusId(Long statusId);

    // Объявления пользователя по статусу (для страницы "Мои объявления")
    List<Transfer> findByUser_IdAndStatus_StatusName(Long userId, String statusName);

    // Все объявления пользователя, отсортированные по статусу
    List<Transfer> findByUser_IdOrderByStatus_StatusNameAsc(Long userId);

    // Объявления, опубликованные в диапазоне дат (для отчёта)
    @Query("SELECT p FROM Transfer p WHERE p.createdAt >= :from AND p.createdAt < :to " +
            "ORDER BY p.status.statusName ASC, p.createdAt DESC")
    List<Transfer> findPublishedBetween(@Param("from") LocalDateTime from,
                                        @Param("to") LocalDateTime to);
}
