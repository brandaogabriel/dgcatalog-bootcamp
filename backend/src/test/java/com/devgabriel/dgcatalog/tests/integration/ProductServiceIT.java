package com.devgabriel.dgcatalog.tests.integration;

import com.devgabriel.dgcatalog.dtos.ProductDTO;
import com.devgabriel.dgcatalog.services.ProductService;
import com.devgabriel.dgcatalog.services.exceptions.ResourceNotFoundException;
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
class ProductServiceIT {

	@Autowired
	private ProductService service;

	private long existingId;
	private long nonExistingId;
	private long countTotalProducts;
	private long countPCGamerProducts;
	private PageRequest pageRequest;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		countTotalProducts = 25L;
		countPCGamerProducts = 21L;
		pageRequest = PageRequest.of(0, 10);
	}

	@Test
	void findAllPagedShouldReturnProductsWhenNameExists() {
		String searchName = "PC Gamer";

		Page<ProductDTO> result = service.findAllPaged(0L, searchName, pageRequest);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countPCGamerProducts, result.getTotalElements());
	}

	@Test
	void findAllPagedShouldReturnNothingWhenNameDoesNotExist() {
		String searchName = "Camera";

		Page<ProductDTO> result = service.findAllPaged(0L, searchName, pageRequest);

		Assertions.assertTrue(result.isEmpty());
	}

	@Test
	void findAllPagedShouldReturnAllProductsWhenNameIsEmpty() {
		String searchName = "";

		Page<ProductDTO> result = service.findAllPaged(0L, searchName, pageRequest);

		Assertions.assertFalse(result.isEmpty());
		Assertions.assertEquals(countTotalProducts, result.getTotalElements());
	}

	@Test
	void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> service.delete(existingId));
	}

	@Test
	void deleteShouldThrowResourceNotFoundExceptionWhenIdNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));
	}
}
