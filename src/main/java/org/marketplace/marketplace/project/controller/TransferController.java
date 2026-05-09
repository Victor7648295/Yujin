package org.marketplace.marketplace.project.controller;

import org.marketplace.marketplace.project.model.Transfer;
import org.marketplace.marketplace.project.repository.UserRepository;
import org.marketplace.marketplace.project.service.CategoryService;
import org.marketplace.marketplace.project.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

/**
 * Публичная витрина объявлений: главная страница, фильтрация и поиск
 * по названию, форма создания/редактирования объявления и его удаление.
 * Также отдаёт телефон продавца по AJAX для модалки звонка.
 */
@Controller
public class TransferController {

    @Autowired
    private TransferService transferService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String index(Model model) {
        List<Transfer> transfers = transferService.getAllProducts();
        model.addAttribute("products", transfers);
        model.addAttribute("regions", transferService.getAllRegions());
        model.addAttribute("categories", transferService.getAllCategories());
        return "index";
    }

    @GetMapping("/filter")
    public String filterProducts(
            @RequestParam(value = "region", required = false) String region,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "condition", required = false) String condition,
            @RequestParam(value = "priceFrom", required = false) Integer priceFrom,
            @RequestParam(value = "priceTo", required = false) Integer priceTo,
            Model model) {

        List<Transfer> transfers = transferService.searchProducts(region, category, condition, priceFrom, priceTo);
        model.addAttribute("products", transfers);
        model.addAttribute("regions", transferService.getAllRegions());
        model.addAttribute("categories", transferService.getAllCategories());

        model.addAttribute("selectedRegion", region);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedCondition", condition);
        model.addAttribute("priceFrom", priceFrom);
        model.addAttribute("priceTo", priceTo);

        return "index";
    }

    @GetMapping("/product/create")
    public String showCreateForm(Model model, Principal principal) {
        model.addAttribute("product", new Transfer());
        model.addAttribute("regions", transferService.getAllRegions());
        model.addAttribute("categories", categoryService.getAllCategories());
        Long currentUserId = (principal == null) ? null
                : userRepository.findByEmail(principal.getName())
                        .map(u -> u.getId()).orElse(null);
        model.addAttribute("currentUserId", currentUserId);
        return "create-product";
    }

    @PostMapping("/product/create")
    public String createProduct(@ModelAttribute Transfer transfer,
                                @RequestParam(value = "userId", required = false) Long userId,
                                @RequestParam(value = "photo", required = false) MultipartFile photo) {
        transferService.createProduct(transfer, userId, photo);
        return "redirect:/";
    }

    @GetMapping("/product/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Transfer> transfer = transferService.getProductById(id);
        if (transfer.isPresent()) {
            model.addAttribute("product", transfer.get());
            model.addAttribute("regions", transferService.getAllRegions());
            model.addAttribute("categories", transferService.getAllCategories());
            return "edit-product";
        }
        return "redirect:/";
    }

    @PostMapping("/product/edit/{id}")
    public String updateProduct(@PathVariable Long id,
                                @ModelAttribute Transfer transfer,
                                @RequestParam(value = "photo", required = false) MultipartFile photo) {
        transferService.updateProduct(id, transfer, photo);
        return "redirect:/my-products";
    }

    @PostMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        transferService.deleteProduct(id);
        return "redirect:/my-products";
    }

    @GetMapping("/api/product/{id}/phone")
    @ResponseBody
    public String getPhoneNumber(@PathVariable Long id) {
        Optional<Transfer> transfer = transferService.getProductById(id);
        return transfer.map(Transfer::getPhone).orElse("");
    }

    @GetMapping("/search-by-name")
    public String searchByName(
            @RequestParam(required = false) String query,
            Model model) {

        List<Transfer> transfers = transferService.searchByName(query);

        model.addAttribute("products", transfers);
        model.addAttribute("regions", transferService.getAllRegions());
        model.addAttribute("categories", transferService.getAllCategories());
        model.addAttribute("searchQuery", query);

        return "index";
    }
}
