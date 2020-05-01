package rz.demo.boot.data.envers.author;

import lombok.*;
import org.hibernate.envers.Audited;
import rz.demo.boot.data.envers.audit.AuditEnabledEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Audited
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Author {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @NotBlank
    private String name;

    @NotBlank
    private String surName;

}
