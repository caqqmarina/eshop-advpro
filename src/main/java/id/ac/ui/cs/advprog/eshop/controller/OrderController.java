package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Order;
import id.ac.ui.cs.advprog.eshop.model.Payment;
import id.ac.ui.cs.advprog.eshop.model.Product;
import id.ac.ui.cs.advprog.eshop.service.OrderService;
import id.ac.ui.cs.advprog.eshop.service.PaymentService;
import id.ac.ui.cs.advprog.eshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private PaymentService paymentService;

    @GetMapping("/create")
    public String createOrderPage(Model model) {
        List<Product> products = productService.findAll();
        model.addAttribute("products", products);
        return "createOrder";
    }

    @GetMapping("/history")
    public String orderHistoryForm(Model model) {
        model.addAttribute("authorName", "");
        return "orderHistoryForm";
    }

    @PostMapping("/history")
    public String showOrderHistory(@RequestParam String authorName, Model model) {
        List<Order> orders = orderService.findAllByAuthor(authorName);
        model.addAttribute("orders", orders);
        model.addAttribute("authorName", authorName);
        return "orderHistory";
    }

    @GetMapping("/pay/{orderId}")
    public String paymentOrderPage(@PathVariable String orderId, Model model) {
        Order order = orderService.findById(orderId);
        if (order == null) {
            return "redirect:/order/history";
        }
        
        model.addAttribute("order", order);
        model.addAttribute("bankTransfer", new HashMap<String, String>());
        model.addAttribute("voucher", new HashMap<String, String>());
        return "payOrder";
    }

    @PostMapping("/pay/{orderId}")
    public String processPayment(
            @PathVariable String orderId,
            @RequestParam String paymentMethod,
            @RequestParam(required = false) String bankName,
            @RequestParam(required = false) String referenceCode,
            @RequestParam(required = false) String voucherCode,
            Model model) {
        
        Order order = orderService.findById(orderId);
        if (order == null) {
            return "redirect:/order/history";
        }
        
        Map<String, String> paymentData = new HashMap<>();
        if ("Bank Transfer".equals(paymentMethod)) {
            paymentData.put("bankName", bankName);
            paymentData.put("referenceCode", referenceCode);
        } else if ("Voucher".equals(paymentMethod)) {
            paymentData.put("voucherCode", voucherCode);
        }
        
        Payment payment = paymentService.addPayment(order, paymentMethod, paymentData);
        
        model.addAttribute("payment", payment);
        model.addAttribute("order", order);
        return "paymentConfirmation";
    }
}