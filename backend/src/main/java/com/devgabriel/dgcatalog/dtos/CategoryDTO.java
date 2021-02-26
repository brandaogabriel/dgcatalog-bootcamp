package com.devgabriel.dgcatalog.dtos;

import com.devgabriel.dgcatalog.entities.Category;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class CategoryDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	@Size(min = 4, max = 10, message = "Nome da categoria deve ter entre 4 e 10 caracteres")
	@NotBlank(message = "Nome da categoria é obrigatório")
	private String name;

	public CategoryDTO() {
	}

	public CategoryDTO(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public CategoryDTO(Category entity) {
		this.id = entity.getId();
		this.name = entity.getName();
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
}
