package id.ac.ui.cs.advprog.eshop.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    private Payment payment;
    private Order order;
    private Map<String, String> paymentData;

    @BeforeEach
    void setUp() {
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        products.add(product);

        order = new Order("order-123", products, 1708560000L, "John Doe");
        
        paymentData = new HashMap<>();
        paymentData.put("bankName", "BCA");
        paymentData.put("referenceCode", "REF123456");
        
        payment = new Payment("payment-123", "Bank Transfer", "SUCCESS", paymentData, order);
    }

    @Test
    void testCreatePayment() {
        assertEquals("payment-123", payment.getId());
        assertEquals("Bank Transfer", payment.getMethod());
        assertEquals("SUCCESS", payment.getStatus());
        assertEquals(paymentData, payment.getPaymentData());
        assertEquals(order, payment.getOrder());
    }

    @Test
    void testSetStatus() {
        payment.setStatus("REJECTED");
        assertEquals("REJECTED", payment.getStatus());
    }
}