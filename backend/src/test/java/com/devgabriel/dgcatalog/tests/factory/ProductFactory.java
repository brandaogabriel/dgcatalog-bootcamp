package com.devgabriel.dgcatalog.tests.factory;

import com.devgabriel.dgcatalog.dtos.ProductDTO;
import com.devgabriel.dgcatalog.entities.Category;
import com.devgabriel.dgcatalog.entities.Product;

import java.time.Instant;

public class ProductFactory {

	public static Product createProduct() {
		Product product = new Product(1L, "Phone", "Good Phone", 800.00, "http://img.com/img.png",
				Instant.parse("2021-01-10T03:00:00Z"));
		product.getCategories().add(new Category(1L, null));

		return product;
	}

	public static ProductDTO createProductDTO() {
		Product product = createProduct();
		return new ProductDTO(product, product.getCategories());
	}

	public static ProductDTO createProductDTO(Long id) {
		ProductDTO dto = createProductDTO();
		dto.setId(id);
		return dto;
	}
}
