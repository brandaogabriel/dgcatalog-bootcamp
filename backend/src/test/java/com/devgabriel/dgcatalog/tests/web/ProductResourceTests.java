package com.devgabriel.dgcatalog.tests.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.devgabriel.dgcatalog.dtos.ProductDTO;
import com.devgabriel.dgcatalog.services.ProductService;
import com.devgabriel.dgcatalog.services.exceptions.DatabaseException;
import com.devgabriel.dgcatalog.services.exceptions.ResourceNotFoundException;
import com.devgabriel.dgcatalog.tests.factory.ProductFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class ProductResourceTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ProductService service;

	@Value("${security.oauth2.client.client-id}")
	private String clientId;

	@Value("${security.oauth2.client.client-secret}")
	private String clientSecret;

	private String operatorUsername;
	private String operatorPassword;

	private Long existingId;
	private Long nonExistingId;
	private Long dependentId;
	private ProductDTO newProductDTO;
	private ProductDTO existingProductDTO;
	private PageImpl<ProductDTO> page;


	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 2L;
		dependentId = 3L;
		operatorUsername = "alex@gmail.com";
		operatorPassword = "123456";
		newProductDTO = ProductFactory.createProductDTO(null);
		existingProductDTO = ProductFactory.createProductDTO(existingId);
		page = new PageImpl<>(List.of(existingProductDTO));

		when(service.findAllPaged(any(), anyString(), any())).thenReturn(page);
		when(service.findById(existingId)).thenReturn(existingProductDTO);
		when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

		when(service.insert(any())).thenReturn(existingProductDTO);
		when(service.update(eq(existingId), any())).thenReturn(existingProductDTO);
		when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);

		doNothing().when(service).delete(existingId);
		doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
		doThrow(DatabaseException.class).when(service).delete(dependentId);
	}

	@Test
	void findAllPagedShouldReturnPage() throws Exception {
		mockMvc.perform(get("/products")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").exists());
	}

	@Test
	void findByIdShouldReturnProductWhenIdExists() throws Exception {
		mockMvc.perform(get("/products/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.id").value(existingId));
	}

	@Test
	void findByIdShouldReturnNotFoundWhenIdNotExists() throws Exception {
		mockMvc.perform(get("/products/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	void insertShouldReturnCreatedProductDTOWhenDataIsValid() throws Exception {
		String accessToken = obtainAccessToken(operatorUsername, operatorPassword);
		String jsonBody = objectMapper.writeValueAsString(newProductDTO);

		mockMvc.perform(post("/products")
				.header("Authorization", "Bearer " + accessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").exists())
				.andExpect(status().isCreated());
	}

	@Test
	void insertShouldReturnUnprocessableEntityWhenNameSizeIsInsufficient() throws Exception {
		String accessToken = obtainAccessToken(operatorUsername, operatorPassword);

		newProductDTO.setName("celu");
		String jsonBody = objectMapper.writeValueAsString(newProductDTO);

		mockMvc.perform(post("/products")
				.header("Authorization", "Bearer " + accessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void insertShouldReturnUnprocessableEntityWhenDescriptionIsNull() throws Exception {
		String accessToken = obtainAccessToken(operatorUsername, operatorPassword);

		newProductDTO.setDescription(null);
		String jsonBody = objectMapper.writeValueAsString(newProductDTO);

		mockMvc.perform(post("/products")
				.header("Authorization", "Bearer " + accessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void insertShouldReturnUnprocessableEntityWhenPriceIsNotPositive() throws Exception {
		String accessToken = obtainAccessToken(operatorUsername, operatorPassword);

		newProductDTO.setPrice(-10.90);
		String jsonBody = objectMapper.writeValueAsString(newProductDTO);

		mockMvc.perform(post("/products")
				.header("Authorization", "Bearer " + accessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void insertShouldReturnUnprocessableEntityWhenDataIsFuture() throws Exception {
		String accessToken = obtainAccessToken(operatorUsername, operatorPassword);

		newProductDTO.setDate(Instant.parse("2025-11-30T03:00:00Z"));
		String jsonBody = objectMapper.writeValueAsString(newProductDTO);

		mockMvc.perform(post("/products")
				.header("Authorization", "Bearer " + accessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void updateShouldReturnProductDTOWhenIdExists() throws Exception {
		String accessToken = obtainAccessToken(operatorUsername, operatorPassword);
		String jsonBody = objectMapper.writeValueAsString(newProductDTO);

		String expectedName = newProductDTO.getName();
		Double expectedPrice = newProductDTO.getPrice();

		mockMvc.perform(put("/products/{id}", existingId)
				.header("Authorization", "Bearer " + accessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.id").value(existingId))
				.andExpect(jsonPath("$.name").value(expectedName))
				.andExpect(jsonPath("$.price").value(expectedPrice));
	}


	@Test
	void updateShouldReturnNotFoundWhenIdNotExists() throws Exception {
		String accessToken = obtainAccessToken(operatorUsername, operatorPassword);
		String jsonBody = objectMapper.writeValueAsString(newProductDTO);

		mockMvc.perform(put("/products/{id}", nonExistingId)
				.header("Authorization", "Bearer " + accessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	void deleteShouldReturnNoContentWhenIdExists() throws Exception {
		String accessToken = obtainAccessToken(operatorUsername, operatorPassword);

		mockMvc.perform(delete("/products/{id}", existingId)
				.header("Authorization", "Bearer " + accessToken)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNoContent());
	}

	@Test
	void deleteShouldReturnNotFoundWhenIdNotExists() throws Exception {
		String accessToken = obtainAccessToken(operatorUsername, operatorPassword);

		mockMvc.perform(delete("/products/{id}", nonExistingId)
				.header("Authorization", "Bearer " + accessToken)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	void deleteShouldReturnBadRequestWhenIdIsDependent() throws Exception {
		String accessToken = obtainAccessToken(operatorUsername, operatorPassword);

		mockMvc.perform(delete("/products/{id}", dependentId)
				.header("Authorization", "Bearer " + accessToken)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}


	private String obtainAccessToken(String username, String password) throws Exception {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "password");
		params.add("client_id", clientId);
		params.add("username", username);
		params.add("password", password);

		ResultActions result
				= mockMvc.perform(post("/oauth/token")
				.params(params)
				.with(httpBasic(clientId, clientSecret))
				.accept("application/json;charset=UTF-8"))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"));

		String resultString = result.andReturn().getResponse().getContentAsString();

		JacksonJsonParser jsonParser = new JacksonJsonParser();
		return jsonParser.parseMap(resultString).get("access_token").toString();
	}
}