package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PaymentRepositoryTest {

    private PaymentRepository paymentRepository;
    private Payment payment;
    private Order order;

    @BeforeEach
    void setUp() {
        paymentRepository = new PaymentRepository();

        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        products.add(product);
        order = new Order("order-123", products, 1708560000L, "John Doe");
        
        Map<String, String> paymentData = new HashMap<>();
        paymentData.put("bankName", "BCA");
        paymentData.put("referenceCode", "REF123456");
        
        payment = new Payment("payment-123", "Bank Transfer", "SUCCESS", paymentData, order);
    }

    @Test
    void testSaveCreate() {
        Payment savedPayment = paymentRepository.save(payment);
        assertEquals(payment.getId(), savedPayment.getId());
        
        Payment foundPayment = paymentRepository.findById(payment.getId());
        assertNotNull(foundPayment);
        assertEquals(payment.getId(), foundPayment.getId());
        assertEquals(payment.getMethod(), foundPayment.getMethod());
        assertEquals(payment.getStatus(), foundPayment.getStatus());
    }

    @Test
    void testSaveUpdate() {
        paymentRepository.save(payment);
        
        payment.setStatus("REJECTED");
        Payment updatedPayment = paymentRepository.save(payment);
        
        assertEquals("REJECTED", updatedPayment.getStatus());
        
        Payment foundPayment = paymentRepository.findById(payment.getId());
        assertEquals("REJECTED", foundPayment.getStatus());
    }

    @Test
    void testFindByIdIfIdFound() {
        paymentRepository.save(payment);
        
        Payment foundPayment = paymentRepository.findById(payment.getId());
        assertNotNull(foundPayment);
        assertEquals(payment.getId(), foundPayment.getId());
    }

    @Test
    void testFindByIdIfIdNotFound() {
        Payment foundPayment = paymentRepository.findById("non-existent");
        assertNull(foundPayment);
    }

    @Test
    void testFindAll_EmptyRepository() {
        List<Payment> allPayments = paymentRepository.findAll();
        assertTrue(allPayments.isEmpty());
    }

    @Test
    void testFindAll_MultiplePayments() {
        paymentRepository.save(payment);
        
        Map<String, String> voucherData = new HashMap<>();
        voucherData.put("voucherCode", "ESHOP1234567890");
        Payment payment2 = new Payment("payment-456", "Voucher", "SUCCESS", voucherData, order);
        paymentRepository.save(payment2);
        
        List<Payment> allPayments = paymentRepository.findAll();
        assertEquals(2, allPayments.size());
    }
}