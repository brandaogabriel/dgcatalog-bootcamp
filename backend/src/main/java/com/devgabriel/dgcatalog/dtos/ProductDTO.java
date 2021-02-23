package com.devgabriel.dgcatalog.dtos;

import com.devgabriel.dgcatalog.entities.Category;
import com.devgabriel.dgcatalog.entities.Product;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProductDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	@Size(min = 5, max = 30, message = "Nome deve ter entre 5 e 30 caracteres")
	@NotBlank(message = "Nome requerido")
	private String name;

	@NotBlank(message = "Descrição requerida")
	private String description;

	@Positive(message = "Preço deve ser um valor positivo")
	private Double price;
	private String imgUrl;

	@PastOrPresent(message = "A data do produto não poder ser futura")
	private Instant date;

	@NotEmpty(message = "Produto sem categoria não é permitido")
	private List<CategoryDTO> categories = new ArrayList<>();

	public ProductDTO(){
	}

	public ProductDTO(Long id, String name, String description, Double price, String imgUrl, Instant date) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.imgUrl = imgUrl;
		this.date = date;
	}

	public ProductDTO(Product entity) {
		this.id = entity.getId();
		this.name = entity.getName();
		this.description = entity.getDescription();
		this.price = entity.getPrice();
		this.imgUrl = entity.getImgUrl();
		this.date = entity.getDate();
	}

	public ProductDTO(Product entity, Set<Category> categories) {
		this(entity);
		categories.forEach(category -> this.categories.add(new CategoryDTO(category)));
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public Instant getDate() {
		return date;
	}

	public void setDate(Instant date) {
		this.date = date;
	}

	public List<CategoryDTO> getCategories() {
		return categories;
	}

	public void setCategories(List<CategoryDTO> categories) {
		this.categories = categories;
	}
}
