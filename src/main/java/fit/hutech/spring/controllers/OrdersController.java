package fit.hutech.spring.controllers;

import fit.hutech.spring.repositories.IInvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class OrdersController {
    private final IInvoiceRepository invoiceRepository;

    @GetMapping("/orders")
    public String orders(Authentication authentication, Model model){
        var username = authentication == null ? null : authentication.getName();
        var invoices = username == null
                ? java.util.List.of()
                : invoiceRepository.findByUser_UsernameOrderByInvoiceDateDesc(username);
        model.addAttribute("invoices", invoices);
        return "orders/list";
    }
    @GetMapping("/orders/pay/{id}")
    public String pay(Authentication authentication, @org.springframework.web.bind.annotation.PathVariable Long id){
        var username = authentication == null ? null : authentication.getName();
        if(username == null) return "redirect:/orders";
        invoiceRepository.findById(id).ifPresent(inv -> {
            if(inv.getUser() != null && username.equals(inv.getUser().getUsername())
                    && "CONFIRMED".equals(inv.getStatus())){
                inv.setStatus("PAID");
                invoiceRepository.save(inv);
            }
        });
        return "redirect:/orders";
    }
}
