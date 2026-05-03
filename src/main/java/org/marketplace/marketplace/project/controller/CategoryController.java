package org.marketplace.marketplace.project.controller;

import lombok.RequiredArgsConstructor;
import org.marketplace.marketplace.project.model.Category;
import org.marketplace.marketplace.project.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("newCategory", new Category());
        return "admin/categories";
    }

    @GetMapping("/edit/{id}")
    public String editCategoryForm(@PathVariable Long id, Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("newCategory", new Category());
        model.addAttribute("editId", id);
        return "admin/categories";
    }

    @PostMapping("/create")
    public String createCategory(@Valid @ModelAttribute("newCategory") Category category,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/categories";
        }

        try {
            categoryService.createCategory(category);
            redirectAttributes.addFlashAttribute("successMessage", "Категория успешно создана");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка: " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }

    @PostMapping("/update/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @RequestParam String name,
                                 RedirectAttributes redirectAttributes) {
        try {
            Category category = new Category();
            category.setName(name);
            categoryService.updateCategory(id, category);
            redirectAttributes.addFlashAttribute("successMessage", "Категория успешно обновлена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка: " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Категория успешно удалена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка: " + e.getMessage());
        }

        return "redirect:/admin/categories";
    }
}
