package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryTest {

    @InjectMocks
    private ProductRepository productRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setProductId("test-id");
        product.setProductName("Test Product");
        product.setProductQuantity(10);
    }

    @Test
    void testCreate() {
        Product created = productRepository.create(product);
        assertEquals(product.getProductId(), created.getProductId());
        assertEquals(product.getProductName(), created.getProductName());
    }

    @Test
    void testFindById_Found() {
        productRepository.create(product);
        Product found = productRepository.findById(product.getProductId());
        assertNotNull(found);
        assertEquals(product.getProductId(), found.getProductId());
    }

    @Test 
    void testFindById_NotFound() {
        Product found = productRepository.findById("non-existent");
        assertNull(found);
    }

    @Test
    void testUpdate_Success() {
        productRepository.create(product);
        
        Product updated = new Product();
        updated.setProductId(product.getProductId());
        updated.setProductName("Updated Name");
        updated.setProductQuantity(20);
        
        productRepository.update(updated);
        
        Product found = productRepository.findById(product.getProductId());
        assertEquals("Updated Name", found.getProductName());
        assertEquals(20, found.getProductQuantity());
    }

    @Test
    void testDelete_Success() {
        productRepository.create(product);
        productRepository.delete(product.getProductId());
        assertNull(productRepository.findById(product.getProductId()));
    }

    @Test
    void testFindAll_EmptyRepository() {
        Iterator<Product> iterator = productRepository.findAll();
        assertFalse(iterator.hasNext());
    }

    @Test
    void testFindAll_MultipleProducts() {
        Product product1 = new Product();
        product1.setProductId("1");
        Product product2 = new Product();
        product2.setProductId("2");
        
        productRepository.create(product1);
        productRepository.create(product2);
        
        Iterator<Product> iterator = productRepository.findAll();
        int count = 0;
        while(iterator.hasNext()) {
            iterator.next();
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    void testDelete_ProductNotFound() {
        productRepository.delete("non-existent");
        // Should not throw exception
    }
}
