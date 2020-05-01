package rz.demo.boot.data.envers.author;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import rz.demo.boot.data.envers.book.Book;

import java.util.stream.Stream;

/**
 * @author Rashidi Zin
 */
public interface AuthorRepository extends JpaRepository<Author, Long>, RevisionRepository<Author, Long, Integer> {

    Stream<Author> findAllByName(String name);

}
