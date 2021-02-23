package com.devgabriel.dgcatalog.tests.services;


import com.devgabriel.dgcatalog.dtos.ProductDTO;
import com.devgabriel.dgcatalog.entities.Product;
import com.devgabriel.dgcatalog.repositories.ProductRepository;
import com.devgabriel.dgcatalog.services.ProductService;
import com.devgabriel.dgcatalog.services.exceptions.DatabaseException;
import com.devgabriel.dgcatalog.services.exceptions.ResourceNotFoundException;
import com.devgabriel.dgcatalog.tests.factory.ProductFactory;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;


@ExtendWith(SpringExtension.class)
class ProductServiceTests {

	@InjectMocks
	private ProductService service;

	@Mock
	private ProductRepository repository;

	private long existingId;
	private long nonExistingId;
	private long dependingId;
	private Product product;
	private PageImpl<Product> page;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		dependingId = 2L;
		product = ProductFactory.createProduct();
		page = new PageImpl<>(List.of(product));

    //Find Products repository mock
		Mockito.when(repository.find(ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.any()))
			.thenReturn(page);
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
		Mockito.when(repository.getOne(existingId)).thenReturn(product);
		Mockito.doThrow(EntityNotFoundException.class).when(repository).getOne(nonExistingId);

		// Save product repository mock
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

		// Delete product repository mock
		Mockito.doNothing().when(repository).deleteById(existingId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependingId);
	}

	@Test
	void findAllPagedShouldReturnPage() {
		PageRequest request = PageRequest.of(0, 10);
		Page<ProductDTO> page = service.findAllPaged(0L, "", request);

		Assertions.assertNotNull(page);
		Assertions.assertEquals(1, page.getTotalElements());
		Mockito.verify(repository, Mockito.times(1)).find(null, "", request);
	}

	@Test
	void findByIdShouldReturnProductDTOWhenIdExists() {
		ProductDTO productDTO = service.findById(existingId);
		Assertions.assertNotNull(productDTO);
		Assertions.assertEquals(productDTO.getId(), product.getId());
	}

	@Test
	void findByIdShouldThrowResourceNotFoundExceptionWhenIdNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));
	}

	@Test
	void updateShouldReturnProductDTOWhenIdExists() {
		ProductDTO dto = new ProductDTO();
		ProductDTO productDTO = service.update(existingId, dto);
		Assertions.assertNotNull(productDTO);
		Assertions.assertEquals(productDTO.getId(), product.getId());
	}

	@Test
	void updateShouldThrowResourceNotFoundExceptionWhenIdNotExists() {
		ProductDTO dto = ProductFactory.createProductDTO();
		Assertions.assertThrows(ResourceNotFoundException.class, () -> service.update(nonExistingId, dto));
	}

	@Test
	void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> service.delete(existingId));
		Mockito.verify(repository, Mockito.times(1)).deleteById(existingId);
	}

	@Test
	void deleteShouldThrowResourceNotFoundExceptionWhenIdNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistingId);
	}

	@Test
	void deleteShouldThrowDatabaseExceptionWhenIdIsDependent() {
		Assertions.assertThrows(DatabaseException.class, () -> service.delete(dependingId));
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependingId);
	}
}
