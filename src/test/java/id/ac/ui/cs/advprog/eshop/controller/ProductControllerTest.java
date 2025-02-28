package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.model.Car;
import id.ac.ui.cs.advprog.eshop.service.ProductService;
import id.ac.ui.cs.advprog.eshop.service.CarServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest({ProductController.class, CarController.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private CarServiceImpl carService;

    private Product product;
    private Car car;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setProductId("test-id");
        product.setProductName("Test Product");
        product.setProductQuantity(10);

        car = new Car();
        car.setCarId("test-car-id");
        car.setCarName("Test Car");
        car.setCarColor("Red");
        car.setCarQuantity(5);
    }

    @Test
    void createProduct_Success() throws Exception {
        when(productService.create(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/product/create")
                .param("productName", "Test Product")
                .param("productQuantity", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product/list"));

        verify(productService).create(any(Product.class));
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
        when(productService.findAll()).thenReturn(Arrays.asList(product));

        mockMvc.perform(get("/product/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("productList"))
                .andExpect(model().attributeExists("products"));

        verify(productService).findAll();
    }

    @Test
    void deleteProduct_Success() throws Exception {
        mockMvc.perform(post("/product/delete/{id}", product.getProductId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product/list"));

        verify(productService).delete(product.getProductId());
    }

    @Test
    void editProduct_Success() throws Exception {
        when(productService.findById(product.getProductId())).thenReturn(product);

        mockMvc.perform(get("/product/edit/{id}", product.getProductId()))
                .andExpect(status().isOk())
                .andExpect(view().name("editProduct"))
                .andExpect(model().attributeExists("product"));

        verify(productService).findById(product.getProductId());
    }

    @Test
    void editProductPost_Success() throws Exception {
        mockMvc.perform(post("/product/edit")
                .param("productId", product.getProductId())
                .param("productName", "Updated Name")
                .param("productQuantity", "200"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/product/list"));

        verify(productService).update(any(Product.class));
    }

    @Test
    void createProductPage_Success() throws Exception {
        mockMvc.perform(get("/product/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("createProduct"))
                .andExpect(model().attributeExists("product"));
    }

    // Add tests for CarController

    @Test
    void createCar_Success() throws Exception {
        when(carService.create(any(Car.class))).thenReturn(car);

        mockMvc.perform(post("/car/createCar")
                .param("carName", "Test Car")
                .param("carColor", "Red")
                .param("carQuantity", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/car/listCar"));

        verify(carService).create(any(Car.class));
    }

    @Test
    void listCars_Success() throws Exception {
        when(carService.findAll()).thenReturn(Arrays.asList(car));

        mockMvc.perform(get("/car/listCar"))
                .andExpect(status().isOk())
                .andExpect(view().name("carList"))
                .andExpect(model().attributeExists("cars"));

        verify(carService).findAll();
    }

    @Test
    void editCar_Success() throws Exception {
        when(carService.findById(car.getCarId())).thenReturn(car);

        mockMvc.perform(get("/car/editCar/{id}", car.getCarId()))
                .andExpect(status().isOk())
                .andExpect(view().name("editCar"))
                .andExpect(model().attributeExists("car"));

        verify(carService).findById(car.getCarId());
    }

    @Test
    void editCarPost_Success() throws Exception {
        mockMvc.perform(post("/car/editCar")
                .param("carId", car.getCarId())
                .param("carName", "Updated Car")
                .param("carColor", "Blue")
                .param("carQuantity", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/car/listCar"));

        verify(carService).update(any(String.class), any(Car.class));
    }

    @Test
    void deleteCar_Success() throws Exception {
        mockMvc.perform(post("/car/deleteCar")
                .param("carId", car.getCarId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/car/listCar"));

        verify(carService).deleteCarById(car.getCarId());
    }
}