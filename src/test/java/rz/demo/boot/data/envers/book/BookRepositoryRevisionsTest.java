package rz.demo.boot.data.envers.book;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.envers.repository.support.DefaultRevisionMetadata;
import org.springframework.data.history.Revision;
import org.springframework.data.history.Revisions;
import org.springframework.test.context.junit4.SpringRunner;
import rz.demo.boot.data.envers.audit.AuditRevisionEntity;
import rz.demo.boot.data.envers.author.Author;
import rz.demo.boot.data.envers.author.AuthorRepository;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Rashidi Zin
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BookRepositoryRevisionsTest {

    @Autowired
    private BookRepository repository;

    private Book book;

    @Autowired
    private AuthorRepository authorRepository;

    private Author author;

    @Before
    public void save() {
        repository.deleteAll();

        author = authorRepository.save(
                Author.builder()
                        .name("Rudyard")
                        .surName("Kipling")
                        .build()
        );

        book = repository.save(
                Book.builder()
                        .author("Rudyard Kipling")
//                        .authorObject(author)
                        .title("Jungle Book")
                        .build()
        );
    }

    @Test
    public void initialRevision() {
        Revisions<Integer, Book> revisions = repository.findRevisions(book.getId());

        assertThat(revisions)
                .isNotEmpty()
                .allSatisfy(revision -> assertThat(revision.getEntity())
                        .extracting(Book::getId, Book::getAuthor, Book::getTitle)
                        .containsExactly(book.getId(), book.getAuthor(), book.getTitle())
                )
                .allSatisfy(revision -> {
                            DefaultRevisionMetadata metadata = (DefaultRevisionMetadata) revision.getMetadata();
                            AuditRevisionEntity revisionEntity = metadata.getDelegate();

                            assertThat(revisionEntity.getUsername()).isEqualTo("wade.wilson");
                        }
                );
    }

    @Test
    public void updateIncreasesRevisionNumber() {
        logRevision(1);

        author.setName("Ramon");
        authorRepository.save(author);

        Optional<Revision<Integer, Author>> authorRevision = authorRepository.findLastChangeRevision(author.getId());

        assertThat(authorRevision)
                .isPresent()
                .hasValueSatisfying(rev ->
                        assertThat(rev.getRevisionNumber()).hasValue(5)
                )
                .hasValueSatisfying(rev ->
                        assertThat(rev.getEntity())
                                .extracting(Author::getName)
                                .isEqualTo("Ramon")
                );

        book.setTitle("If");
        book.setAuthorObject(author);
        repository.save(book);
        logRevision(2);

        Optional<Revision<Integer, Book>> revision = repository.findLastChangeRevision(book.getId());

        assertThat(revision)
                .isPresent()
                .hasValueSatisfying(rev ->
                        assertThat(rev.getRevisionNumber()).hasValue(6)
                )
                .hasValueSatisfying(rev -> {
                    assertThat(rev.getEntity())
                            .extracting(Book::getTitle)
                            .isEqualTo("If");
                    assertThat(rev.getEntity().getAuthorObject())
                            .extracting(Author::getName)
                            .isEqualTo("Ramon");
                        }
                );

    }

    private void logRevision(Integer logNr) {
        System.out.println("LOG_NO: " + logNr);
        Revisions<Integer, Book> revisions = repository.findRevisions(book.getId());
        revisions.forEach(reviter -> {
            System.out.println("REV_NO: " + reviter.getRevisionNumber());
            System.out.println("BOOK: " + reviter.getEntity());
            System.out.println("AUTHOR: " + reviter.getEntity().getAuthorObject());
        });
        System.out.println("LOGNO filtered: " + logNr);
        Stream<Revision<Integer, Book>> filteredRevisions = revisions.stream()
                .filter(reviter -> reviter.getEntity().getAuthorObject() != null).distinct();

        filteredRevisions.forEach( revi -> {
            System.out.println("REV_NO: " + revi.getRevisionNumber());
            System.out.println("BOOK: " + revi.getEntity());
            System.out.println("AUTHOR: " + revi.getEntity().getAuthorObject());
        });
    }

    @Test
    public void deletedItemWillHaveRevisionRetained() {
        repository.delete(book);

        Revisions<Integer, Book> revisions = repository.findRevisions(book.getId());

        assertThat(revisions).hasSize(2);

        Iterator<Revision<Integer, Book>> iterator = revisions.iterator();

        Revision<Integer, Book> initialRevision = iterator.next();
        Revision<Integer, Book> finalRevision = iterator.next();

        assertThat(initialRevision)
                .satisfies(rev -> {
                    assertThat(rev.getEntity())
                            .extracting(Book::getId, Book::getAuthor, Book::getTitle)
                            .containsExactly(book.getId(), book.getAuthor(), book.getTitle());
                    assertThat(rev.getEntity().getAuthorObject())
                            .extracting(Author::getId, Author::getName, Author::getSurName)
                            .containsExactly(author.getId(), author.getName(), author.getSurName());
                        }
                );

        assertThat(finalRevision)
                .satisfies(rev -> {
                    assertThat(rev.getEntity())
                            .extracting(Book::getId, Book::getTitle, Book::getAuthor)
                            .containsExactly(book.getId(), null, null);
                    assertThat(rev.getEntity().getAuthorObject()).isEqualTo(null);
                        }
                );
    }
}
