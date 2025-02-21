package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setProductId("test-id");
        product.setProductName("Test Product");
        product.setProductQuantity(10);
    }

    @Test
    void testCreateProduct_Success() {
        when(productRepository.create(any(Product.class))).thenReturn(product);
        Product created = productService.create(product);
        assertNotNull(created.getProductId());
        assertEquals(product.getProductName(), created.getProductName());
        verify(productRepository).create(product);
    }

    @Test
    void testFindAll_Success() {
        List<Product> products = Arrays.asList(product);
        when(productRepository.findAll()).thenReturn(products.iterator());
        List<Product> result = productService.findAll();
        assertEquals(1, result.size());
        assertEquals(product.getProductId(), result.get(0).getProductId());
    }

    @Test
    void testFindAll_EmptyList() {
        when(productRepository.findAll()).thenReturn(Collections.emptyIterator());
        List<Product> result = productService.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void testFindById_NotFound() {
        when(productRepository.findById("non-existent")).thenReturn(null);
        Product result = productService.findById("non-existent");
        assertNull(result);
    }

    @Test
    void testUpdateProduct_Success() {
        Product updatedProduct = new Product();
        updatedProduct.setProductId("test-id");
        updatedProduct.setProductName("Updated Name");
        updatedProduct.setProductQuantity(20);
        
        productService.update(updatedProduct);
        verify(productRepository).update(updatedProduct);
    }

    @Test
    void testUpdate_ProductNotFound() {
        Product nonExistentProduct = new Product();
        nonExistentProduct.setProductId("non-existent");
        

        productService.update(nonExistentProduct);
        
        verify(productRepository).update(nonExistentProduct);
    }

    @Test
    void testDeleteProduct_Success() {
        productService.delete("test-id");
        verify(productRepository).delete("test-id");
    }

    @Test
    void testDelete_ProductNotFound() {
        doNothing().when(productRepository).delete("non-existent");
        productService.delete("non-existent");
        verify(productRepository).delete("non-existent");
    }
}