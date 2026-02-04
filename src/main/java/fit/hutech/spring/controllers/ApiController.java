package fit.hutech.spring.controllers;

import fit.hutech.spring.services.BookService;
import fit.hutech.spring.services.CartService;
import fit.hutech.spring.services.CategoryService;
import fit.hutech.spring.viewmodels.BookGetVm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ApiController {
    private final BookService bookService;
    private final CategoryService categoryService;
    private final CartService cartService;

    @GetMapping("/books")
    public ResponseEntity<List<BookGetVm>> getAllBooks(Integer pageNo,
            Integer pageSize, String sortBy) {
        return ResponseEntity.ok(bookService.getAllBooks(
                pageNo == null ? 0 : pageNo, pageSize == null ? 20 : pageSize,
                sortBy == null ? "id" : sortBy)
                .stream()
                .map(BookGetVm::from)
                .toList());
    }

    @GetMapping("/books/id/{id}")
    public ResponseEntity<BookGetVm> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id)
                .map(BookGetVm::from)
                .orElse(null));
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<BookGetVm> getBookByIdShort(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id)
                .map(BookGetVm::from)
                .orElse(null));
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBookById(@PathVariable Long id) {
        bookService.deleteBookById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/books/search")
    public ResponseEntity<List<BookGetVm>> searchBooks(String keyword) {
        return ResponseEntity.ok(bookService.searchBook(keyword)
                .stream()
                .map(BookGetVm::from)
                .toList());
    }

    @GetMapping(value = "/books/export/csv")
    public ResponseEntity<String> exportBooksCsv() {
        var books = bookService.getAllBooks(0, Integer.MAX_VALUE, "id");
        StringBuilder sb = new StringBuilder();
        sb.append("id,title,author,price,category").append("\n");
        books.forEach(b -> sb
                .append(b.getId()).append(',')
                .append(escapeCsv(b.getTitle())).append(',')
                .append(escapeCsv(b.getAuthor())).append(',')
                .append(b.getPrice() == null ? "" : b.getPrice()).append(',')
                .append(b.getCategory() == null ? "" : escapeCsv(b.getCategory().getName()))
                .append("\n"));
        return ResponseEntity.ok()
                .header("Content-Type", "text/csv; charset=UTF-8")
                .header("Content-Disposition", "attachment; filename=books.csv")
                .body(sb.toString());
    }

    private String escapeCsv(String s){
        if (s == null) return "";
        String v = s.replace("\"","\"\"");
        if (v.contains(",") || v.contains("\"") || v.contains("\n")) {
            return "\"" + v + "\"";
        }
        return v;
    }
}
