package me.abdul.axi_api.controllers;

import jdk.jfr.Category;
import me.abdul.axi_api.entities.FormCategory;
import me.abdul.axi_api.services.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/category")
@RestController
public class CategroyController {

    private final CategoryService categoryService;

    public CategroyController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<FormCategory>> getAllCategories() {
        List<FormCategory> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }


}
