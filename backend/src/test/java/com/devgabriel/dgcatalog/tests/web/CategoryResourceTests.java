package com.devgabriel.dgcatalog.tests.web;

import com.devgabriel.dgcatalog.dtos.CategoryDTO;
import com.devgabriel.dgcatalog.services.CategoryService;
import com.devgabriel.dgcatalog.services.exceptions.ResourceNotFoundException;
import com.devgabriel.dgcatalog.tests.factory.CategoryFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryResourceTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private CategoryService service;

	@Value("${security.oauth2.client.client-id}")
	private String clientId;

	@Value("${security.oauth2.client.client-secret}")
	private String clientSecret;

	private String operatorUsername;
	private String operatorPassword;
	private String accessToken;

	private long existingId;
	private long nonExistingId;
	private CategoryDTO newCategoryDTO;
	private CategoryDTO existingCategoryDTO;
	private PageImpl<CategoryDTO> page;

	@BeforeEach
	void setUp() throws Exception {
		operatorUsername = "alex@gmail.com";
		operatorPassword = "123456";
		accessToken = obtainAccessToken(operatorUsername, operatorPassword);
		existingId = 1L;
		nonExistingId = 2L;
		newCategoryDTO = CategoryFactory.createCategoryDTO(null);
		existingCategoryDTO = CategoryFactory.createCategoryDTO();
		page = new PageImpl<>(List.of(existingCategoryDTO));

		when(service.findAllPaged(any())).thenReturn(page);
		when(service.findById(existingId)).thenReturn(existingCategoryDTO);
		when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);
		when(service.insert(any())).thenReturn(existingCategoryDTO);
		when(service.update(eq(existingId), any())).thenReturn(existingCategoryDTO);
		when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);

		doNothing().when(service).delete(existingId);
		doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
	}

	@Test
	void findAllPagedShouldReturnPage() throws Exception {
		mockMvc.perform(get("/categories")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").exists());
	}

	@Test
	void findByIdShouldReturnCategoryDTOWhenIdExists() throws Exception {
		mockMvc.perform(get("/categories/{id}", existingId)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.id").value(existingId));
	}

	@Test
	void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		mockMvc.perform(get("/categories/{id}", nonExistingId)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNotFound());
	}

	@Test
	void insertShouldReturnCategoryDTOWhenDataIsValid() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(newCategoryDTO);

		mockMvc.perform(post("/categories")
				.header("Authorization", "Bearer " + accessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(status().isCreated());
	}

	@Test
	void insertShouldReturnUnprocessableEntityWhenNameIsNull() throws Exception {
		newCategoryDTO.setName(null);

		String jsonBody = objectMapper.writeValueAsString(newCategoryDTO);

		mockMvc.perform(post("/categories")
				.header("Authorization", "Bearer " + accessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void insertShouldReturnUnprocessableEntityWhenNameIsJustSpaces() throws Exception {
		newCategoryDTO.setName("     ");

		String jsonBody = objectMapper.writeValueAsString(newCategoryDTO);

		mockMvc.perform(post("/categories")
				.header("Authorization", "Bearer " + accessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void insertShouldReturnUnprocessableEntityWhenNameSizeIsInsufficient() throws Exception {
		newCategoryDTO.setName("abc");

		String jsonBody = objectMapper.writeValueAsString(newCategoryDTO);

		mockMvc.perform(post("/categories")
				.header("Authorization", "Bearer " + accessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void updateShouldReturnCategoryDTOWhenIdExists() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(newCategoryDTO);
		String expectedName = newCategoryDTO.getName();

		mockMvc.perform(put("/categories/{id}", existingId)
				.header("Authorization", "Bearer " + accessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.id").value(existingId))
				.andExpect(jsonPath("$.name").value(expectedName));
	}

	@Test
	void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		String jsonBody = objectMapper.writeValueAsString(newCategoryDTO);

		mockMvc.perform(put("/categories/{id}", nonExistingId)
				.header("Authorization", "Bearer " + accessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	void updateShouldReturnUnprocessableEntityWhenNameIsNull() throws Exception {
		newCategoryDTO.setName(null);

		String jsonBody = objectMapper.writeValueAsString(newCategoryDTO);

		mockMvc.perform(put("/categories/{id}", existingId)
				.header("Authorization", "Bearer " + accessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void updateShouldReturnUnprocessableEntityWhenNameIsJustSpaced() throws Exception {
		newCategoryDTO.setName("   ");

		String jsonBody = objectMapper.writeValueAsString(newCategoryDTO);

		mockMvc.perform(put("/categories/{id}", existingId)
				.header("Authorization", "Bearer " + accessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void updateShouldReturnUnprocessableEntityWhenNameSizeIsInsufficient() throws Exception {
	newCategoryDTO.setName("abc");

		String jsonBody = objectMapper.writeValueAsString(newCategoryDTO);

		mockMvc.perform(put("/categories/{id}", existingId)
				.header("Authorization", "Bearer " + accessToken)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isUnprocessableEntity());
	}

	@Test
	void deleteShouldReturnNoContentWhenIdExists() throws Exception {
		mockMvc.perform(delete("/categories/{id}", existingId)
				.header("Authorization", "Bearer " + accessToken)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNoContent());
	}

	@Test
	void deleteShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		mockMvc.perform(delete("/categories/{id}", nonExistingId)
				.header("Authorization", "Bearer " + accessToken)
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNotFound());
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
