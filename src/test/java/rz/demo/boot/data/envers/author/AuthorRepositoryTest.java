package rz.demo.boot.data.envers.author;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.test.context.junit4.SpringRunner;
import rz.demo.boot.data.envers.RepositoryConfiguration;
import rz.demo.boot.data.envers.audit.AuditConfiguration;
import rz.demo.boot.data.envers.audit.AuditorAwareImpl;
import rz.demo.boot.data.envers.book.Book;
import rz.demo.boot.data.envers.book.BookRepository;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

/**
 * @author Rashidi Zin
 */
@DataJpaTest(includeFilters = @Filter(
        type = ASSIGNABLE_TYPE,
        classes = {AuditorAwareImpl.class, AuditConfiguration.class, RepositoryConfiguration.class}
))
@RunWith(SpringRunner.class)
public class AuthorRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private AuthorRepository repository;

    private Author book;

    @Before
    public void save() {
        book = em.persistAndFlush(
                Author.builder().name("Rudyard").surName("Kipling").build()
        );
    }

    @Test
    public void findAllByAuthor() {
        Stream<Author> booksByAuthor = repository.findAllByName("Rudyard");

        assertThat(booksByAuthor)
                .isNotEmpty()
                .extracting(Author::getName, Author::getSurName)
                .containsExactly(tuple("Rudyard", "Kipling"));
    }

//    @Test
//    public void hasAuditInformation() {
//        assertThat(book)
//                .extracting(Author::getCreatedBy, Author::getCreatedDate, Author::getLastModifiedBy, Author::getLastModifiedDate, Author::getVersion)
//                .isNotNull();
//    }
}