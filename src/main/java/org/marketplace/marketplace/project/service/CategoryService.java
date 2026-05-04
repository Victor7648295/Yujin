package org.marketplace.marketplace.project.service;

import lombok.RequiredArgsConstructor;
import org.marketplace.marketplace.project.model.Category;
import org.marketplace.marketplace.project.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Бизнес-логика управления категориями: чтение, создание, обновление
 * и удаление; гарантирует уникальность названия категории.
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderByIdDesc();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));
    }

    public Category createCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("Категория с таким названием уже существует");
        }
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = getCategoryById(id);

        if (!category.getName().equals(categoryDetails.getName()) &&
                categoryRepository.existsByName(categoryDetails.getName())) {
            throw new RuntimeException("Категория с таким названием уже существует");
        }

        category.setName(categoryDetails.getName());
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }

}
