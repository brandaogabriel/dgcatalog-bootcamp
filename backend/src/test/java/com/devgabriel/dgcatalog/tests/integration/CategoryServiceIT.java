package com.devgabriel.dgcatalog.tests.integration;

import com.devgabriel.dgcatalog.dtos.CategoryDTO;
import com.devgabriel.dgcatalog.services.CategoryService;
import com.devgabriel.dgcatalog.services.exceptions.ResourceNotFoundException;
import com.devgabriel.dgcatalog.tests.factory.CategoryFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CategoryServiceIT {

	@Autowired
	private CategoryService service;

	private long existingId;
	private long nonExistingId;
	private long countTotalCategories;
	private PageRequest page;
	private String expectedCategoryName;
	private CategoryDTO existingCategoryDTO;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalCategories = 3;
		page = PageRequest.of(0, 10);
		expectedCategoryName = "Higiene";
		existingCategoryDTO = CategoryFactory.createCategoryDTO();
	}

	@Test
	void findAllPagedShouldReturnPage() {
		Page<CategoryDTO> result = service.findAllPaged(page);
		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countTotalCategories, result.getTotalElements());
	}

	@Test
	void findByIdShouldReturnCategoryDTOWhenIdExists() {
		CategoryDTO dto = service.findById(existingId);
		Assertions.assertNotNull(dto);
		Assertions.assertEquals(existingId, dto.getId());
	}

	@Test
	void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));
	}

	@Test
	void insertShouldReturnCategoryDTOWhenDataIsValid() {
		CategoryDTO dto = CategoryFactory.createCategoryDTO();
		dto = service.insert(dto);
		Assertions.assertNotNull(dto);
		Assertions.assertEquals(expectedCategoryName, dto.getName());
	}

	@Test
	void updateShouldReturnCategoryDTOWhenIdExists() {
		service.update(existingId, existingCategoryDTO);
		Assertions.assertNotNull(existingCategoryDTO);
		Assertions.assertEquals(expectedCategoryName, existingCategoryDTO.getName());
	}

	@Test
	void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(nonExistingId, existingCategoryDTO));
	}

	@Test
	void deleteShouldReturnNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> service.delete(existingId));
	}

	@Test
	void deleteShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));
	}

}
