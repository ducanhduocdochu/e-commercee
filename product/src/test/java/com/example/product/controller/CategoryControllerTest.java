package com.example.product.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.product.dto.request.CategoryCreationRequest;
import com.example.product.dto.request.CategoryUpdateRequest;
import com.example.product.dto.response.CategoryResponse;
import com.example.product.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
    }

    @Test
    void createCategory_ShouldReturnCategory() throws Exception {
        CategoryCreationRequest request = new CategoryCreationRequest("Laptop");

        CategoryResponse categoryResponse =
                new CategoryResponse("123", "Laptop", LocalDateTime.now(), LocalDateTime.now());

        when(categoryService.createCategory(any())).thenReturn(categoryResponse);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value("123"))
                .andExpect(jsonPath("$.result.name").value("Laptop"));
    }

    @Test
    void getCategory_ShouldReturnCategory() throws Exception {
        String categoryId = "123";
        CategoryResponse categoryResponse =
                new CategoryResponse("123", "Laptop", LocalDateTime.now(), LocalDateTime.now());

        when(categoryService.getCategory(eq(categoryId))).thenReturn(categoryResponse);

        mockMvc.perform(get("/categories/{categoryId}", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value("123"))
                .andExpect(jsonPath("$.result.name").value("Laptop"));
    }

    @Test
    void updateCategory_ShouldReturnUpdatedCategory() throws Exception {
        String categoryId = "123";
        CategoryUpdateRequest request = new CategoryUpdateRequest("Gaming Laptop");

        CategoryResponse categoryResponse =
                new CategoryResponse("123", "Gaming Laptop", LocalDateTime.now(), LocalDateTime.now());

        when(categoryService.updateCategory(eq(categoryId), any())).thenReturn(categoryResponse);

        mockMvc.perform(put("/categories/{categoryId}", categoryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.name").value("Gaming Laptop"));
    }

    @Test
    void deleteCategory_ShouldReturnSuccessMessage() throws Exception {
        String categoryId = "123";
        doNothing().when(categoryService).deleteCategory(eq(categoryId));

        mockMvc.perform(delete("/categories/{categoryId}", categoryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("Category has been deleted"));
    }

    @Test
    void getCategories_ShouldReturnListOfCategories() throws Exception {
        List<CategoryResponse> categories = List.of(
                new CategoryResponse("123", "Laptop", LocalDateTime.now(), LocalDateTime.now()),
                new CategoryResponse("456", "Mobile", LocalDateTime.now(), LocalDateTime.now()));

        when(categoryService.getCategories(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(categories);

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].id").value("123"))
                .andExpect(jsonPath("$.result[1].id").value("456"));
    }
}
