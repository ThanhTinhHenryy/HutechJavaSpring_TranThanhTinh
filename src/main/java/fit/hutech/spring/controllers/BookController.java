package fit.hutech.spring.controllers;

import fit.hutech.spring.entities.Book;
import fit.hutech.spring.daos.Item;
import fit.hutech.spring.services.BookService;
import fit.hutech.spring.services.CartService;
import fit.hutech.spring.services.CategoryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final CategoryService categoryService;
    private final CartService cartService;
    @GetMapping
    public String showAllBooks(@NotNull Model model,
                               @RequestParam(defaultValue = "0")
                               Integer pageNo,
                               @RequestParam(defaultValue = "20")
                               Integer pageSize,
                               @RequestParam(defaultValue = "id")
                               String sortBy) {
        model.addAttribute("books", bookService.getAllBooks(pageNo,
                pageSize, sortBy));
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages",
                bookService.getAllBooks(pageNo, pageSize, sortBy).size() / pageSize);
        model.addAttribute("categories",
                categoryService.getAllCategories());
        return "book/list";
    }
    @GetMapping("/add")
    public String addBookForm(@NotNull Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories",
                categoryService.getAllCategories());
        return "book/add";
    }
    @PostMapping("/add")
    public String addBook(
            @Valid @ModelAttribute("book") Book book,
            @NotNull BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage).toArray(String[]::new);
            model.addAttribute("errors", errors);
            model.addAttribute("categories",
                    categoryService.getAllCategories());
            return "book/add";
        }
        bookService.addBook(book);
        return "redirect:/books";
    }
    @GetMapping("/import")
    public String importForm() {
        return "book/import";
    }
    @PostMapping("/import")
    public String importCsv(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) throws java.io.IOException {
        var reader = new java.io.BufferedReader(new java.io.InputStreamReader(file.getInputStream()));
        String line;
        boolean first = true;
        while((line = reader.readLine()) != null){
            if(first){ first = false; continue; }
            var parts = parseCsvLine(line);
            if(parts.length < 5) continue;
            var b = new Book();
            b.setTitle(parts[1]);
            b.setAuthor(parts[2]);
            try { b.setPrice(parts[3].isEmpty()? null : Double.parseDouble(parts[3])); } catch(Exception ignored){}
            var category = categoryService.findOrCreateByName(parts[4]);
            b.setCategory(category);
            bookService.addBook(b);
        }
        return "redirect:/books";
    }
    private String[] parseCsvLine(String line){
        var list = new java.util.ArrayList<String>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        for(char c : line.toCharArray()){
            if(c=='\"'){ inQuotes = !inQuotes; continue; }
            if(c==',' && !inQuotes){
                list.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        list.add(sb.toString());
        return list.toArray(new String[0]);
    }
    @PostMapping("/add-to-cart")
    public String addToCart(HttpSession session,
                            @RequestParam long id,
                            @RequestParam String name,
                            @RequestParam double price,
                            @RequestParam(defaultValue = "1") int
                                    quantity) {
        var cart = cartService.getCart(session);
        cart.addItems(new Item(id, name, price, quantity));
        cartService.updateCart(session, cart);
        return "redirect:/books";
    }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable long id) {
        bookService.getBookById(id)
                .ifPresentOrElse(
                        book -> bookService.deleteBookById(id),
                        () -> { throw new IllegalArgumentException("Book not found"); });
                            return "redirect:/books";
                        }

    @GetMapping("/edit/{id}")
    public String editBookForm(@NotNull Model model, @PathVariable long id)
    {
        var book = bookService.getBookById(id);
        model.addAttribute("book", book.orElseThrow(() -> new
                IllegalArgumentException("Book not found")));
        model.addAttribute("categories",
                categoryService.getAllCategories());
        return "book/edit";
    }
    @GetMapping("/detail/{id}")
    public String detail(@NotNull Model model, @PathVariable long id) {
        var book = bookService.getBookById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));
        model.addAttribute("book", book);
        return "book/detail";
    }
    @PostMapping("/edit")
    public String editBook(@Valid @ModelAttribute("book") Book book,
                           @NotNull BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            model.addAttribute("categories",
                    categoryService.getAllCategories());
            return "book/edit";
        }
        bookService.updateBook(book);
        return "redirect:/books";
    }

    @GetMapping("/search")
    public String searchBook(
            @NotNull Model model,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy) {
        model.addAttribute("books", bookService.searchBook(keyword));
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages",
                bookService
                        .getAllBooks(pageNo, pageSize, sortBy)
                        .size() / pageSize);
        model.addAttribute("categories",
                categoryService.getAllCategories());
        return "book/list";
    }
}
