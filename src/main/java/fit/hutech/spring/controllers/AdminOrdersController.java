package fit.hutech.spring.controllers;

import fit.hutech.spring.repositories.IInvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrdersController {
    private final IInvoiceRepository invoiceRepository;

    @GetMapping
    public String list(Model model){
        var list = invoiceRepository.findAll();
        model.addAttribute("orders", list);
        return "admin/orders/list";
    }
    @GetMapping("/{id}/confirm")
    public String confirm(@PathVariable Long id){
        invoiceRepository.findById(id).ifPresent(inv -> {
            inv.setStatus("CONFIRMED");
            invoiceRepository.save(inv);
        });
        return "redirect:/admin/orders";
    }
    @GetMapping("/{id}/pay")
    public String markPaid(@PathVariable Long id){
        invoiceRepository.findById(id).ifPresent(inv -> {
            inv.setStatus("PAID");
            invoiceRepository.save(inv);
        });
        return "redirect:/admin/orders";
    }
    @GetMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id){
        invoiceRepository.findById(id).ifPresent(inv -> {
            inv.setStatus("CANCELED");
            invoiceRepository.save(inv);
        });
        return "redirect:/admin/orders";
    }
}
