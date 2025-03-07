package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;
    
    @Mock
    private OrderService orderService;
    
    @InjectMocks
    private PaymentServiceImpl paymentService;
    
    private Order order;
    private Map<String, String> bankTransferData;
    private Map<String, String> invalidBankTransferData;
    private Map<String, String> voucherData;
    private Map<String, String> invalidVoucherData;

    @BeforeEach
    void setUp() {

        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setProductId("eb558e9f-1c39-460e-8860-71af6af63bd6");
        products.add(product);
        order = new Order("order-123", products, 1708560000L, "John Doe");

        bankTransferData = new HashMap<>();
        bankTransferData.put("bankName", "BCA");
        bankTransferData.put("referenceCode", "REF123456");

        invalidBankTransferData = new HashMap<>();
        invalidBankTransferData.put("bankName", "BCA");
        invalidBankTransferData.put("referenceCode", "");

        voucherData = new HashMap<>();
        voucherData.put("voucherCode", "ESHOP12345678901");
        
        invalidVoucherData = new HashMap<>();
        invalidVoucherData.put("voucherCode", "INVALID");
    }

    @Test
    void testAddPayment_BankTransfer_Success() {
        - set up return values for mocked methods
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArguments()[0]);
        
 - call the method with valid bank transfer data
        Payment result = paymentService.addPayment(order, "Bank Transfer", bankTransferData);
        
         - verify results
        assertNotNull(result);
        assertEquals("Bank Transfer", result.getMethod());
        assertEquals("SUCCESS", result.getStatus());
        assertEquals(order, result.getOrder());
        verify(orderService).updateStatus(order.getId(), OrderStatus.SUCCESS.getValue());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void testAddPayment_BankTransfer_Rejected_EmptyBankName() {

        bankTransferData.put("bankName", "");
        

        Payment result = paymentService.addPayment(order, "Bank Transfer", bankTransferData);
        
        
        assertEquals("REJECTED", result.getStatus());
        verify(orderService).updateStatus(order.getId(), OrderStatus.FAILED.getValue());
        verify(paymentRepository).save(any(Payment.class));
    }
    
    @Test
    void testAddPayment_BankTransfer_Rejected_NoReferenceCode() {

        Payment result = paymentService.addPayment(order, "Bank Transfer", invalidBankTransferData);
        
        
        assertEquals("REJECTED", result.getStatus());
        verify(orderService).updateStatus(order.getId(), OrderStatus.FAILED.getValue());
        verify(paymentRepository).save(any(Payment.class));
    }
    
    @Test
    void testAddPayment_Voucher_Success() {
       
        voucherData.put("voucherCode", "ESHOP12345678AB");
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArguments()[0]);
        

        Payment result = paymentService.addPayment(order, "Voucher", voucherData);

        assertEquals("SUCCESS", result.getStatus());
        verify(orderService).updateStatus(order.getId(), OrderStatus.SUCCESS.getValue());
        verify(paymentRepository).save(any(Payment.class));
    }
    
    @Test
    void testAddPayment_Voucher_Rejected() {

        Payment result = paymentService.addPayment(order, "Voucher", invalidVoucherData);

        assertEquals("REJECTED", result.getStatus());
        verify(orderService).updateStatus(order.getId(), OrderStatus.FAILED.getValue());
        verify(paymentRepository).save(any(Payment.class));
    }
    
    @Test
    void testSetStatus_Success() {

        Payment payment = new Payment("payment-123", "Bank Transfer", "PENDING", bankTransferData, order);
        when(paymentRepository.save(payment)).thenReturn(payment);

        Payment result = paymentService.setStatus(payment, "SUCCESS");

        assertEquals("SUCCESS", result.getStatus());
        verify(orderService).updateStatus(order.getId(), OrderStatus.SUCCESS.getValue());
        verify(paymentRepository).save(payment);
    }
    
    @Test
    void testSetStatus_Rejected() {

        Payment payment = new Payment("payment-123", "Bank Transfer", "PENDING", bankTransferData, order);
        when(paymentRepository.save(payment)).thenReturn(payment);

        Payment result = paymentService.setStatus(payment, "REJECTED");

        assertEquals("REJECTED", result.getStatus());
        verify(orderService).updateStatus(order.getId(), OrderStatus.FAILED.getValue());
        verify(paymentRepository).save(payment);
    }
    
    @Test
    void testGetPayment() {

        Payment payment = new Payment("payment-123", "Bank Transfer", "SUCCESS", bankTransferData, order);
        when(paymentRepository.findById("payment-123")).thenReturn(payment);
        
        Payment result = paymentService.getPayment("payment-123");

        assertNotNull(result);
        assertEquals("payment-123", result.getId());
        verify(paymentRepository).findById("payment-123");
    }
    
    @Test
    void testGetAllPayments() {

        List<Payment> payments = new ArrayList<>();
        payments.add(new Payment("payment-123", "Bank Transfer", "SUCCESS", bankTransferData, order));
        payments.add(new Payment("payment-456", "Voucher", "REJECTED", invalidVoucherData, order));
        when(paymentRepository.findAll()).thenReturn(payments);

        List<Payment> result = paymentService.getAllPayments();

        assertEquals(2, result.size());
        verify(paymentRepository).findAll();
    }
}