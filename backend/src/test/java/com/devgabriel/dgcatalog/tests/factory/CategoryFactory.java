package com.devgabriel.dgcatalog.tests.factory;

import com.devgabriel.dgcatalog.dtos.CategoryDTO;
import com.devgabriel.dgcatalog.entities.Category;

public class CategoryFactory {

	public static Category createCategory() {
		return new Category(1L, "Higiene");
	}

	public static CategoryDTO createCategoryDTO() {
		Category category = createCategory();
		return new CategoryDTO(category);
	}

	public static CategoryDTO createCategoryDTO(Long id) {
		CategoryDTO categoryDTO = createCategoryDTO();
		categoryDTO.setId(id);
		return categoryDTO;
	}
}
