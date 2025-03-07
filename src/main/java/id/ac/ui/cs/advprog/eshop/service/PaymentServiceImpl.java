package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.repository.PaymentRepository;
import id.ac.ui.cs.advprog.eshop.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final String SUCCESS_STATUS = "SUCCESS";
    private static final String REJECTED_STATUS = "REJECTED";
    private static final String BANK_TRANSFER_METHOD = "Bank Transfer";
    private static final String VOUCHER_METHOD = "Voucher";
    
    private static final String BANK_NAME_KEY = "bankName";
    private static final String REFERENCE_CODE_KEY = "referenceCode";
    private static final String VOUCHER_CODE_KEY = "voucherCode";
    
    private static final String VOUCHER_PREFIX = "ESHOP";
    private static final int VOUCHER_CODE_LENGTH = 16;
    private static final int REQUIRED_DIGIT_COUNT = 8;

    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private OrderService orderService;

    @Override
    public Payment addPayment(Order order, String method, Map<String, String> paymentData) {
        validateInputs(order, method, paymentData);
        
        String paymentId = UUID.randomUUID().toString();
        String status = determinePaymentStatus(method, paymentData);
        
        Payment payment = new Payment(paymentId, method, status, paymentData, order);
        paymentRepository.save(payment);
        
        updateOrderStatus(order.getId(), status);
        
        return payment;
    }

    @Override
    public Payment setStatus(Payment payment, String status) {
        payment.setStatus(status);
        paymentRepository.save(payment);
        
        updateOrderStatus(payment.getOrder().getId(), status);
        
        return payment;
    }

    @Override
    public Payment getPayment(String paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    private void validateInputs(Order order, String method, Map<String, String> paymentData) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        if (method == null || method.isEmpty()) {
            throw new IllegalArgumentException("Payment method cannot be null or empty");
        }
        if (paymentData == null) {
            throw new IllegalArgumentException("Payment data cannot be null");
        }
    }
    
    private String determinePaymentStatus(String method, Map<String, String> paymentData) {
        if (BANK_TRANSFER_METHOD.equals(method)) {
            return validateBankTransfer(paymentData);
        } else if (VOUCHER_METHOD.equals(method)) {
            return validateVoucher(paymentData);
        }
        return REJECTED_STATUS;
    }
    
    private String validateBankTransfer(Map<String, String> paymentData) {
        String bankName = paymentData.get(BANK_NAME_KEY);
        String referenceCode = paymentData.get(REFERENCE_CODE_KEY);
        
        if (isNullOrEmpty(bankName) || isNullOrEmpty(referenceCode)) {
            return REJECTED_STATUS;
        }
        
        return SUCCESS_STATUS;
    }
    
    private String validateVoucher(Map<String, String> paymentData) {
        String voucherCode = paymentData.get(VOUCHER_CODE_KEY);
        
        if (!isValidVoucherCode(voucherCode)) {
            return REJECTED_STATUS;
        }
        
        return SUCCESS_STATUS;
    }
    
    // Extract validation logic to a separate method for better testability
    protected boolean isValidVoucherCode(String voucherCode) {
        // Check for null, length, and prefix in one go
        if (isNullOrEmpty(voucherCode) || 
            voucherCode.length() != VOUCHER_CODE_LENGTH || 
            !voucherCode.startsWith(VOUCHER_PREFIX)) {
            return false;
        }
        
        // Check digit count
        int digitCount = countDigits(voucherCode);
        return digitCount == REQUIRED_DIGIT_COUNT;
    }
    
    private boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
    
    private int countDigits(String str) {
        return (int) str.chars().filter(Character::isDigit).count();
    }
    
    private void updateOrderStatus(String orderId, String paymentStatus) {
        if (SUCCESS_STATUS.equals(paymentStatus)) {
            orderService.updateStatus(orderId, OrderStatus.SUCCESS.getValue());
        } else if (REJECTED_STATUS.equals(paymentStatus)) {
            orderService.updateStatus(orderId, OrderStatus.FAILED.getValue());
        }
    }
    
    public static void main(String[] args) {
        Map<String, String> voucherData = new HashMap<>();
        voucherData.put("voucherCode", "ESHOP1234ABCD567"); 
    }
}