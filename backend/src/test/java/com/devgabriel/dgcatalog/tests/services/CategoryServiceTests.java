package com.devgabriel.dgcatalog.tests.services;

import com.devgabriel.dgcatalog.dtos.CategoryDTO;
import com.devgabriel.dgcatalog.entities.Category;
import com.devgabriel.dgcatalog.repositories.CategoryRepository;
import com.devgabriel.dgcatalog.services.CategoryService;
import com.devgabriel.dgcatalog.services.exceptions.DatabaseException;
import com.devgabriel.dgcatalog.services.exceptions.ResourceNotFoundException;
import com.devgabriel.dgcatalog.tests.factory.CategoryFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;


@ExtendWith(SpringExtension.class)
class CategoryServiceTests {

	@InjectMocks
	private CategoryService service;

	@Mock
	private CategoryRepository repository;

	private Category category;
	private CategoryDTO newCategoryDTO;
	private Long existingId;
	private Long nonExistingId;
	private Long dependingId;
	private PageRequest pageRequest;
	private PageImpl<Category> pageImp;

	@BeforeEach
	void setUp() throws Exception {
		category = CategoryFactory.createCategory();
		existingId = 1L;
		nonExistingId = 2L;
		dependingId = 3L;
		newCategoryDTO = CategoryFactory.createCategoryDTO();
		pageRequest = PageRequest.of(0, 10);
		pageImp = new PageImpl<>(List.of(category));

		Mockito.when(repository.findAll(pageRequest)).thenReturn(pageImp);
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(category));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
		Mockito.when(repository.getOne(existingId)).thenReturn(category);
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(category);

		Mockito.doNothing().when(repository).deleteById(existingId);
		Mockito.doThrow(EntityNotFoundException.class).when(repository).getOne(nonExistingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependingId);
	}

	@Test
	void findAllPagedShouldReturnPage() {
		Page<CategoryDTO> page = service.findAllPaged(pageRequest);
		Assertions.assertNotNull(page);
		Assertions.assertEquals(1, page.getTotalElements());
	}

	@Test
	void findByIdShouldReturnCategoryDTOWhenIdExists() {
		CategoryDTO dto = service.findById(existingId);
		Assertions.assertNotNull(dto);
		Assertions.assertEquals(dto.getId(), category.getId());
	}

	@Test
	void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));
	}

	@Test
	void insertShouldReturnCategoryDTOWhenValidData() {
		CategoryDTO dto = service.insert(newCategoryDTO);
		Assertions.assertNotNull(dto);
		Assertions.assertEquals(dto.getId(), category.getId());
	}

	@Test
	void updateShouldReturnCategoryDTOWhenIdExists() {
		CategoryDTO dto = new CategoryDTO();
		CategoryDTO categoryDTO = service.update(existingId, dto);
		Assertions.assertNotNull(categoryDTO);
		Assertions.assertEquals(categoryDTO.getId(), category.getId());
	}

	@Test
	void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		CategoryDTO dto = CategoryFactory.createCategoryDTO();
		Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(nonExistingId, dto));
		Mockito.verify(repository, Mockito.times(1)).getOne(nonExistingId);
	}

	@Test
	void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> service.delete(existingId));
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
	}

	@Test
	void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
	}

	@Test
	void deleteShouldThrowDatabaseExceptionWhenIdIsDependet() {
		Assertions.assertThrows(DatabaseException.class, () -> service.delete(dependingId));
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependingId);
	}
}
