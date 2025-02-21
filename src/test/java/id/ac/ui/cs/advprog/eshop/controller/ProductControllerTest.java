package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService service;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setProductId("test-id");
        product.setProductName("Test Product");
        product.setProductQuantity(10);
    }

    @Test
    void createProduct_Success() throws Exception {
        when(service.create(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/product/create")
                .param("productName", "Test Product")
                .param("productQuantity", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product/list"));

        verify(service).create(any(Product.class));
    }

    @Test
    void createProduct_InvalidInput() throws Exception {
        mockMvc.perform(post("/product/create")
                .param("productName", "")
                .param("productQuantity", "-1"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void listProducts_Success() throws Exception {
        when(service.findAll()).thenReturn(Arrays.asList(product));

        mockMvc.perform(get("/product/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("productList"))
                .andExpect(model().attributeExists("products"));

        verify(service).findAll();
    }

    @Test
    void deleteProduct_Success() throws Exception {
        mockMvc.perform(post("/product/delete/{id}", product.getProductId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product/list"));

        verify(service).delete(product.getProductId());
    }

    @Test
    void editProduct_Success() throws Exception {
        when(service.findById(product.getProductId())).thenReturn(product);

        mockMvc.perform(get("/product/edit/{id}", product.getProductId()))
                .andExpect(status().isOk())
                .andExpect(view().name("editProduct"))
                .andExpect(model().attributeExists("product"));

        verify(service).findById(product.getProductId());
    }

    @Test
    void editProductPost_Success() throws Exception {
        mockMvc.perform(post("/product/edit")
                .param("productId", product.getProductId())
                .param("productName", "Updated Name")
                .param("productQuantity", "200"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product/list"));

        verify(service).update(any(Product.class));
    }

    @Test
    void createProductPage_Success() throws Exception {
        mockMvc.perform(get("/product/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("createProduct"))
                .andExpect(model().attributeExists("product"))
                .andDo(print());
    }
}