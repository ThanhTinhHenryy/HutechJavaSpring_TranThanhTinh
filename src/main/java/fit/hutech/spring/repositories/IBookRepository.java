package fit.hutech.spring.repositories;
import fit.hutech.spring.entities.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface IBookRepository extends
        PagingAndSortingRepository<Book, Long>, JpaRepository<Book, Long> {
    @Query("""
SELECT b FROM Book b
WHERE b.title LIKE %?1%
OR b.author LIKE %?1%
OR b.category.name LIKE %?1%
""")
    List<Book> searchBook(String keyword);

    @Query("""
SELECT b FROM Book b
WHERE (:keyword IS NULL OR LOWER(b.title) LIKE %:keyword% OR LOWER(b.author) LIKE %:keyword% OR LOWER(b.category.name) LIKE %:keyword%)
AND (:categoryId IS NULL OR b.category.id = :categoryId)
AND (:minPrice IS NULL OR b.price >= :minPrice)
AND (:maxPrice IS NULL OR b.price <= :maxPrice)
""")
    List<Book> searchAdvanced(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable);
}
