package fit.hutech.spring.services;

import fit.hutech.spring.entities.Book;
import fit.hutech.spring.repositories.IBookRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE,
        rollbackFor = {Exception.class, Throwable.class})
public class BookService {
    private final IBookRepository bookRepository;
//    public List<Book> getAllBooks(Integer pageNo,
//                                  Integer pageSize,
//                                  String sortBy) {
//        return bookRepository.findAllBooks(pageNo, pageSize, sortBy);
//    }
public List<Book> getAllBooks(Integer pageNo, Integer pageSize, String sortBy) {
    // Tạo đối tượng phân trang và sắp xếp
    Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

    // Gọi phương thức findAll mặc định của JpaRepository
    Page<Book> pagedResult = bookRepository.findAll(pageable);

    // Trả về danh sách list từ đối tượng Page
    return pagedResult.hasContent() ? pagedResult.getContent() : new ArrayList<Book>();
}
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }
    public void addBook(Book book) {
        bookRepository.save(book);
    }
    public void updateBook(@NotNull Book book) {
        Book existingBook = bookRepository.findById(book.getId())
                .orElse(null);
        Objects.requireNonNull(existingBook).setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setPrice(book.getPrice());
        existingBook.setCategory(book.getCategory());
        existingBook.setImageUrl(book.getImageUrl());
        bookRepository.save(existingBook);
    }
    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }

    public List<Book> searchBook(String keyword) {
        return bookRepository.searchBook(keyword);
    }

    public List<Book> searchAdvanced(Integer pageNo, Integer pageSize, String sortBy,
                                     String keyword, Long categoryId, Double minPrice, Double maxPrice){
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));
        String kw = keyword == null ? null : keyword.trim().toLowerCase();
        return bookRepository.searchAdvanced(kw, categoryId, minPrice, maxPrice, pageable);
    }
}
