package com.devgabriel.dgcatalog.services;

import com.devgabriel.dgcatalog.dtos.CategoryDTO;
import com.devgabriel.dgcatalog.entities.Category;
import com.devgabriel.dgcatalog.repositories.CategoryRepository;
import com.devgabriel.dgcatalog.services.exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll() {
		List<Category> categories = repository.findAll();
		return categories.stream().map(CategoryDTO::new).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Optional<Category> obj = repository.findById(id);
		Category category = obj.orElseThrow(() -> new EntityNotFoundException("Category not found"));
		return new CategoryDTO(category);
	}

}
