package org.marketplace.marketplace.project.repository;


import org.marketplace.marketplace.project.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    //Поиск по id
    Optional<Product> findById(Long id);

    //Сохранение продукта
    Product save(Product product);

    //Удаление по id
    void deleteById(Long id);

    //Проверить сузествует ли продукт
    boolean existsById(Long id);

    // Поиск по названию (частичное совпадение)
    List<Product> findByTitleContainingIgnoreCase(String title);

    // Поиск по региону
    List<Product> findByRegion(String region);

    // Поиск по категории
    List<Product> findByCategory(String category);

    // Поиск по состоянию
    List<Product> findByCondition(String condition);

    // Поиск по цене в диапазоне
    List<Product> findByPriceBetween(Integer minPrice, Integer maxPrice);

    // Получить все уникальные регионы
    @Query("SELECT DISTINCT p.region FROM Product p ORDER BY p.region")
    List<String> findAllRegions();

    // Получить все уникальные категории
    @Query("SELECT DISTINCT p.category FROM Product p ORDER BY p.category")
    List<String> findAllCategories();

    // Сложный поиск с фильтрацией (JPQL)
    @Query("SELECT p FROM Product p WHERE " +
            "(:region IS NULL OR :region = '' OR p.region = :region) AND " +
            "(:category IS NULL OR :category = '' OR p.category = :category) AND " +
            "(:condition IS NULL OR :condition = '' OR p.condition = :condition) AND " +
            "(:priceFrom IS NULL OR p.price >= :priceFrom) AND " +
            "(:priceTo IS NULL OR p.price <= :priceTo)")
    List<Product> searchProducts(@Param("region") String region,
                                 @Param("category") String category,
                                 @Param("condition") String condition,
                                 @Param("priceFrom") Integer priceFrom,
                                 @Param("priceTo") Integer priceTo);

    // Поиск с фильтрацией
    @Query("SELECT p FROM Product p WHERE " +
            "(:region IS NULL OR p.region = :region) AND " +
            "(:category IS NULL OR p.category = :category) AND " +
            "(:condition IS NULL OR p.condition = :condition) AND " +
            "(:priceFrom IS NULL OR p.price >= :priceFrom) AND " +
            "(:priceTo IS NULL OR p.price <= :priceTo)")
    List<Product> filterProducts(@Param("region") String region,
                                 @Param("category") String category,
                                 @Param("condition") String condition,
                                 @Param("priceFrom") Integer priceFrom,
                                 @Param("priceTo") Integer priceTo);

    // Поиск по названию
    @Query("SELECT p FROM Product p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> searchByTitle(@Param("query") String query);
}
