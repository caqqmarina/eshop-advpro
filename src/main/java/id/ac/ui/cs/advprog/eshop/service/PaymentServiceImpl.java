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

    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private OrderService orderService;

    @Override
    public Payment addPayment(Order order, String method, Map<String, String> paymentData) {
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
    
    private String determinePaymentStatus(String method, Map<String, String> paymentData) {
        if ("Bank Transfer".equals(method)) {
            return validateBankTransfer(paymentData);
        } else if ("Voucher".equals(method)) {
            return validateVoucher(paymentData);
        }
        return "REJECTED";
    }
    
    private String validateBankTransfer(Map<String, String> paymentData) {
        String bankName = paymentData.get("bankName");
        String referenceCode = paymentData.get("referenceCode");
        
        if (bankName == null || bankName.isEmpty() || 
            referenceCode == null || referenceCode.isEmpty()) {
            return "REJECTED";
        }
        
        return "SUCCESS";
    }
    
    private String validateVoucher(Map<String, String> paymentData) {
        String voucherCode = paymentData.get("voucherCode");
        
        if (voucherCode == null || voucherCode.length() != 16) {
            return "REJECTED";
        }
        
        if (!voucherCode.startsWith("ESHOP")) {
            return "REJECTED";
        }
        
        int digitCount = 0;
        for (char c : voucherCode.toCharArray()) {
            if (Character.isDigit(c)) {
                digitCount++;
            }
        }
        
        if (digitCount != 8) {
            return "REJECTED";
        }
        
        return "SUCCESS";
    }
    
    private void updateOrderStatus(String orderId, String paymentStatus) {
        if ("SUCCESS".equals(paymentStatus)) {
            orderService.updateStatus(orderId, OrderStatus.SUCCESS.getValue());
        } else if ("REJECTED".equals(paymentStatus)) {
            orderService.updateStatus(orderId, OrderStatus.FAILED.getValue());
        }
    }
}