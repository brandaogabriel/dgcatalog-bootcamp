package com.devgabriel.dgcatalog.tests.repositories;

import com.devgabriel.dgcatalog.entities.Category;
import com.devgabriel.dgcatalog.entities.Product;
import com.devgabriel.dgcatalog.repositories.ProductRepository;
import com.devgabriel.dgcatalog.tests.factory.ProductFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@DataJpaTest
class ProductRepositoryTests {

	@Autowired
	private ProductRepository repository;
	
	private long existingId;
	private long nonExistingId;
	private long countTotalProducts;
	private long countPCGamerProducts;
	private long countCategory2Products;
	private Pageable pageable;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L;
		countPCGamerProducts = 21L;
		countCategory2Products = 2L;
		pageable = PageRequest.of(0, 10);
	}

	@Test
	void saveShouldPersistWithAutoincrementWhenIdIsNull() {
		Product product = ProductFactory.createProduct();
		product.setId(null);

		product = repository.save(product);
		Optional<Product> result = repository.findById(product.getId());

		Assertions.assertNotNull(result);
		Assertions.assertEquals(countTotalProducts + 1L, product.getId());
		Assertions.assertSame(result.get(), product);
	}

	@Test
	void findShouldReturnProductsWhenNameExists() {
		String searchName = "PC Gamer";

		Page<Product> result = repository.find(null, searchName, pageable);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countPCGamerProducts, result.getTotalElements());
	}

	@Test
	void findShouldReturnProductsWhenNameExistsIgnoringCase() {
		String searchName = "pC GaMeR";

		Page<Product> result = repository.find(null, searchName, pageable);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countPCGamerProducts, result.getTotalElements());
	}

	@Test
	void findShouldReturnAllProductsWhenNameIsEmpty() {
		String searchName = "";

		Page<Product> result = repository.find(null, searchName, pageable);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countTotalProducts, result.getTotalElements());
	}

	@Test
	void findShouldReturnReturnNothingWhenNameDoesNotExist() {
		String searchName = "Camera";

		Page<Product> result = repository.find(null, searchName, pageable);

		Assertions.assertTrue(result.isEmpty());
	}

	@Test
	void findShouldReturnOnlySelectedCategoryWhenCategoryInformed() {
		List<Category> categories = new ArrayList<>();
		categories.add(new Category(2L, null));

		Page<Product> result = repository.find(categories, "", pageable);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countCategory2Products, result.getTotalElements());
	}

	@Test
	void findShouldReturnAllProductsWhenCategoryNotInformed() {
		List<Category> categories = null;
		Page<Product> result = repository.find(categories, "", pageable);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countTotalProducts, result.getTotalElements());
	}

	@Test
	void deleteShouldDeleteObjectWhenIdExists() {
		repository.deleteById(existingId);

		Optional<Product> result = repository.findById(existingId);

		Assertions.assertFalse(result.isPresent());
	}

	@Test
	void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesntExist() {
		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> repository.deleteById(nonExistingId));
	}
}
