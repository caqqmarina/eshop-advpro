package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/detail")
    public String showPaymentDetailForm(Model model) {
        model.addAttribute("paymentId", "");
        return "paymentDetailForm";
    }

    @GetMapping("/detail/{paymentId}")
    public String showPaymentDetail(@PathVariable String paymentId, Model model) {
        Payment payment = paymentService.getPayment(paymentId);
        if (payment == null) {
            return "redirect:/payment/detail";
        }
        
        model.addAttribute("payment", payment);
        return "paymentDetail";
    }

    @GetMapping("/admin/list")
    public String showAllPayments(Model model) {
        List<Payment> payments = paymentService.getAllPayments();
        model.addAttribute("payments", payments);
        return "adminPaymentList";
    }

    @GetMapping("/admin/detail/{paymentId}")
    public String showAdminPaymentDetail(@PathVariable String paymentId, Model model) {
        Payment payment = paymentService.getPayment(paymentId);
        if (payment == null) {
            return "redirect:/payment/admin/list";
        }
        
        model.addAttribute("payment", payment);
        return "adminPaymentDetail";
    }

    @PostMapping("/admin/set-status/{paymentId}")
    public String setPaymentStatus(
            @PathVariable String paymentId,
            @RequestParam String status,
            Model model) {
        
        Payment payment = paymentService.getPayment(paymentId);
        if (payment == null) {
            return "redirect:/payment/admin/list";
        }
        
        payment = paymentService.setStatus(payment, status);
        
        model.addAttribute("payment", payment);
        return "redirect:/payment/admin/detail/" + paymentId;
    }
}