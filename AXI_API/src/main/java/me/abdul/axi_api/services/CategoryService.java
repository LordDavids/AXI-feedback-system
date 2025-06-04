package me.abdul.axi_api.services;

import jdk.jfr.Category;
import me.abdul.axi_api.entities.FormCategory;
import me.abdul.axi_api.repos.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<FormCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

}
