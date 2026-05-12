package org.trasfermarkt.project.service;


import lombok.RequiredArgsConstructor;
import org.trasfermarkt.project.model.TransferStatus;
import org.trasfermarkt.project.model.Transfer;
import org.trasfermarkt.project.repository.TransferStatusRepository;
import org.trasfermarkt.project.repository.TransferRepository;
import org.trasfermarkt.project.repository.UserRepository;
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
public class TransferService {

    private final TransferRepository transferRepository;
    private final TransferStatusRepository transferStatusRepository;
    private final UserRepository userRepository;

    private static final int IMAGE_SIZE = 200;
    private static final int CORNER_RADIUS = 40;
    private static final Path IMAGE_DIR = Paths.get("src", "main", "resources", "static", "img");

    public List<Transfer> getAllProducts() {
        return transferRepository.findByStatusId(8L);
    }

    public Optional<Transfer> getProductById(Long id) {
        return transferRepository.findById(id);
    }

    public Transfer createProduct(Transfer transfer, Long userId, MultipartFile photo) {
        if (userId != null) {
            userRepository.findById(userId).ifPresent(transfer::setUser);
        }
        String savedPath = saveImage(photo);
        if (savedPath != null) {
            transfer.setImagePath(savedPath);
        }
        if (transfer.getStatus() == null) {
            transfer.setStatus(getOrCreateStatus(TransferStatus.PENDING));
        }
        return transferRepository.save(transfer);
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

    public Optional<Transfer> updateProduct(Long id, Transfer updatedTransfer, MultipartFile photo) {
        return transferRepository.findById(id).map(existing -> {
            existing.setTitle(updatedTransfer.getTitle());
            existing.setPrice(updatedTransfer.getPrice());
            existing.setRegion(updatedTransfer.getRegion());
            existing.setCategory(updatedTransfer.getCategory());
            existing.setCondition(updatedTransfer.getCondition());
            existing.setPhone(updatedTransfer.getPhone());
            existing.setDescription(updatedTransfer.getDescription());
            existing.setSellerName(updatedTransfer.getSellerName());
            String savedPath = saveImage(photo);
            if (savedPath != null) {
                existing.setImagePath(savedPath);
            }
            return transferRepository.save(existing);
        });
    }

    public boolean deleteProduct(Long id) {
        if (transferRepository.existsById(id)) {
            transferRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Transfer> searchProducts(String region, String category,
                                         String condition, Integer priceFrom, Integer priceTo) {
        return transferRepository.searchProducts(region, category, condition, priceFrom, priceTo);
    }

    public List<String> getAllRegions() {
        return transferRepository.findAllRegions();
    }

    public List<String> getAllCategories() {
        return transferRepository.findAllCategories();
    }

    public List<Transfer> filterProducts(String region, String category,
                                         String condition, Integer priceFrom,
                                         Integer priceTo) {
        region = (region != null && region.isEmpty()) ? null : region;
        category = (category != null && category.isEmpty()) ? null : category;
        condition = (condition != null && condition.isEmpty()) ? null : condition;

        return transferRepository.filterProducts(region, category, condition, priceFrom, priceTo);
    }

    public List<Transfer> getProductsByUserAndStatus(Long userId, String statusName) {
        if (userId == null) {
            return new ArrayList<>();
        }
        return transferRepository.findByUser_IdAndStatus_StatusName(userId, statusName);
    }

    public List<Transfer> getProductsByUser(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        return transferRepository.findByUser_IdOrderByStatus_StatusNameAsc(userId);
    }

    public List<Transfer> searchByName(String query) {
        if (query == null || query.isEmpty()) {
            return new ArrayList<>();
        }
        return transferRepository.searchByTitle(query);
    }

    public List<Transfer> getPendingProducts() {
        return transferRepository.findByStatus_StatusName(TransferStatus.PENDING);
    }

    public boolean approveProduct(Long id) {
        return changeStatus(id, TransferStatus.APPROVED);
    }

    public boolean rejectProduct(Long id) {
        return changeStatus(id, TransferStatus.REJECTED);
    }

    private boolean changeStatus(Long transferId, String statusName) {
        return transferRepository.findById(transferId).map(transfer -> {
            transfer.setStatus(getOrCreateStatus(statusName));
            transferRepository.save(transfer);
            return true;
        }).orElse(false);
    }

    private TransferStatus getOrCreateStatus(String statusName) {
        return transferStatusRepository.findByStatusName(statusName).orElseGet(() -> {
            TransferStatus status = new TransferStatus();
            status.setStatusName(statusName);
            return transferStatusRepository.save(status);
        });
    }
}
