package rz.demo.boot.data.envers.book;

import lombok.*;
import org.hibernate.envers.Audited;
import rz.demo.boot.data.envers.audit.AuditEnabledEntity;
import rz.demo.boot.data.envers.author.Author;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Rashidi Zin
 */
@Data
@Entity
@Audited
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Book extends AuditEnabledEntity {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @NotBlank
    private String author;

    @OneToOne
    private Author authorObject;

    @NotBlank
    private String title;

}
