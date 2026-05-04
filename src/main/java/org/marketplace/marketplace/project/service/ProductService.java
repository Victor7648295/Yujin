package org.marketplace.marketplace.project.service;


import lombok.RequiredArgsConstructor;
import org.marketplace.marketplace.project.model.Product;
import org.marketplace.marketplace.project.model.ProductStatus;
import org.marketplace.marketplace.project.repository.ProductRepository;
import org.marketplace.marketplace.project.repository.ProductStatusRepository;
import org.marketplace.marketplace.project.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductStatusRepository productStatusRepository;
    private final UserRepository userRepository;

    private static final int IMAGE_SIZE = 200;
    private static final int CORNER_RADIUS = 40;
    // Каталог для загруженных изображений (dev: src/main/resources/static/img)
    private static final Path IMAGE_DIR = Paths.get("src", "main", "resources", "static", "img");

    // Получить все товары
    public List<Product> getAllProducts() {
        return productRepository.findByStatusId(2L);
    }

    // Получить товар по ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }


    // Создать новый товар
    public Product createProduct(Product product, Long userId, MultipartFile photo) {
        if (userId != null) {
            userRepository.findById(userId).ifPresent(product::setUser);
        }
        String savedPath = saveImage(photo);
        if (savedPath != null) {
            product.setImagePath(savedPath);
        }
        if (product.getStatus() == null) {
            product.setStatus(getOrCreateStatus(ProductStatus.PENDING));
        }
        return productRepository.save(product);
    }

    private String saveImage(MultipartFile photo) {
        if (photo == null || photo.isEmpty()) {
            return null;
        }
        try {
            Files.createDirectories(IMAGE_DIR);
            BufferedImage src;
            try (InputStream in = photo.getInputStream()) {
                src = ImageIO.read(in);
            }
            if (src == null) {
                throw new IOException("Неподдерживаемый формат изображения");
            }
            BufferedImage rounded = new BufferedImage(IMAGE_SIZE, IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = rounded.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Маска со скруглёнными углами + рисуем картинку только внутри маски
            g.setComposite(AlphaComposite.Src);
            g.fill(new RoundRectangle2D.Float(0, 0, IMAGE_SIZE, IMAGE_SIZE, CORNER_RADIUS, CORNER_RADIUS));
            g.setComposite(AlphaComposite.SrcAtop);
            g.drawImage(src, 0, 0, IMAGE_SIZE, IMAGE_SIZE, null);
            g.dispose();

            String fileName = UUID.randomUUID() + ".png";
            Path target = IMAGE_DIR.resolve(fileName);
            ImageIO.write(rounded, "png", target.toAbsolutePath().toFile());
            return "/img/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить изображение", e);
        }
    }

    // Обновить товар
    public Optional<Product> updateProduct(Long id, Product updatedProduct, MultipartFile photo) {
        return productRepository.findById(id).map(existing -> {
            existing.setTitle(updatedProduct.getTitle());
            existing.setPrice(updatedProduct.getPrice());
            existing.setRegion(updatedProduct.getRegion());
            existing.setCategory(updatedProduct.getCategory());
            existing.setCondition(updatedProduct.getCondition());
            existing.setPhone(updatedProduct.getPhone());
            existing.setDescription(updatedProduct.getDescription());
            existing.setSellerName(updatedProduct.getSellerName());
            // Меняем картинку только если загрузили новый файл
            String savedPath = saveImage(photo);
            if (savedPath != null) {
                existing.setImagePath(savedPath);
            }
            return productRepository.save(existing);
        });
    }

    // Удалить товар
    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Поиск с фильтрацией
    public List<Product> searchProducts(String region, String category,
                                        String condition, Integer priceFrom, Integer priceTo) {
        return productRepository.searchProducts(region, category, condition, priceFrom, priceTo);
    }

    // Получить все регионы
    public List<String> getAllRegions() {
        return productRepository.findAllRegions();
    }

    // Получить все категории
    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }

    // Фильтрация товаров
    public List<Product> filterProducts(String region, String category,
                                        String condition, Integer priceFrom,
                                        Integer priceTo) {
        // Обработка пустых строк
        region = (region != null && region.isEmpty()) ? null : region;
        category = (category != null && category.isEmpty()) ? null : category;
        condition = (condition != null && condition.isEmpty()) ? null : condition;

        return productRepository.filterProducts(region, category, condition, priceFrom, priceTo);
    }

    // Объявления пользователя по статусу
    public List<Product> getProductsByUserAndStatus(Long userId, String statusName) {
        if (userId == null) {
            return new ArrayList<>();
        }
        return productRepository.findByUser_IdAndStatus_StatusName(userId, statusName);
    }

    // Поиск по названию
    public List<Product> searchByName(String query) {
        if (query == null || query.isEmpty()) {
            return new ArrayList<>();
        }
        return productRepository.searchByTitle(query);
    }

    // Объявления, ожидающие модерации
    public List<Product> getPendingProducts() {
        return productRepository.findByStatus_StatusName(ProductStatus.PENDING);
    }

    // Одобрить объявление
    public boolean approveProduct(Long id) {
        return changeStatus(id, ProductStatus.APPROVED);
    }

    // Отклонить объявление
    public boolean rejectProduct(Long id) {
        return changeStatus(id, ProductStatus.REJECTED);
    }

    private boolean changeStatus(Long productId, String statusName) {
        return productRepository.findById(productId).map(product -> {
            product.setStatus(getOrCreateStatus(statusName));
            productRepository.save(product);
            return true;
        }).orElse(false);
    }

    private ProductStatus getOrCreateStatus(String statusName) {
        return productStatusRepository.findByStatusName(statusName).orElseGet(() -> {
            ProductStatus status = new ProductStatus();
            status.setStatusName(statusName);
            return productStatusRepository.save(status);
        });
    }
}
