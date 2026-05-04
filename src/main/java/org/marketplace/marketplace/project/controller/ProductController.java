package org.marketplace.marketplace.project.controller;

import org.marketplace.marketplace.project.model.Product;
import org.marketplace.marketplace.project.repository.UserRepository;
import org.marketplace.marketplace.project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository userRepository;

    // Главная страница - все товары
    @GetMapping("/")
    public String index(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        model.addAttribute("regions", productService.getAllRegions());
        model.addAttribute("categories", productService.getAllCategories());
        return "index";
    }

    // Поиск и фильтрация
    @GetMapping("/filter")
    public String filterProducts(
            @RequestParam(value = "region", required = false) String region,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "condition", required = false) String condition,
            @RequestParam(value = "priceFrom", required = false) Integer priceFrom,
            @RequestParam(value = "priceTo", required = false) Integer priceTo,
            Model model) {

        List<Product> products = productService.searchProducts(region, category, condition, priceFrom, priceTo);
        model.addAttribute("products", products);
        model.addAttribute("regions", productService.getAllRegions());
        model.addAttribute("categories", productService.getAllCategories());

        // Сохраняем параметры фильтра для отображения в форме
        model.addAttribute("selectedRegion", region);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedCondition", condition);
        model.addAttribute("priceFrom", priceFrom);
        model.addAttribute("priceTo", priceTo);

        return "index";
    }

    // Страница детального просмотра товара
    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "product-detail";
        } else {
            return "redirect:/";
        }
    }

    // Форма создания объявления (GET)
    @GetMapping("/product/create")
    public String showCreateForm(Model model, Principal principal) {
        model.addAttribute("product", new Product());
        model.addAttribute("regions", productService.getAllRegions());
        model.addAttribute("categories", productService.getAllCategories());
        Long currentUserId = (principal == null) ? null
                : userRepository.findByEmail(principal.getName())
                        .map(u -> u.getId()).orElse(null);
        model.addAttribute("currentUserId", currentUserId);
        return "create-product";
    }

    // Создание товара (POST)
    @PostMapping("/product/create")
    public String createProduct(@ModelAttribute Product product,
                                @RequestParam(value = "userId", required = false) Long userId,
                                @RequestParam(value = "photo", required = false) MultipartFile photo) {
        productService.createProduct(product, userId, photo);
        return "redirect:/";
    }

    // Страница редактирования (GET)
    @GetMapping("/product/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            model.addAttribute("regions", productService.getAllRegions());
            model.addAttribute("categories", productService.getAllCategories());
            return "edit-product";
        }
        return "redirect:/";
    }

    // Обновление товара (POST)
    @PostMapping("/product/edit/{id}")
    public String updateProduct(@PathVariable Long id,
                                @ModelAttribute Product product,
                                @RequestParam(value = "photo", required = false) MultipartFile photo) {
        productService.updateProduct(id, product, photo);
        return "redirect:/my-products";
    }

    // Удаление товара
    @PostMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/my-products";
    }

    // API для получения телефона (через AJAX)
    @GetMapping("/api/product/{id}/phone")
    @ResponseBody
    public String getPhoneNumber(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(Product::getPhone).orElse("");
    }

    // ПОИСК ПО НАЗВАНИЮ (только по имени товара)
    @GetMapping("/search-by-name")
    public String searchByName(
            @RequestParam(required = false) String query,
            Model model) {

        List<Product> products = productService.searchByName(query);

        model.addAttribute("products", products);
        model.addAttribute("regions", productService.getAllRegions());
        model.addAttribute("categories", productService.getAllCategories());
        model.addAttribute("searchQuery", query);  // Сохраняем поисковый запрос для отображения

        return "index";
    }
}
