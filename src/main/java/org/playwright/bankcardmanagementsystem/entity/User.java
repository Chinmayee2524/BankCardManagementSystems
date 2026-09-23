package org.playwright.bankcardmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
//user is a reserved keyword in sql so we dont use table name as users
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
// "This field is the primary key" — the unique identifier of each row. Every database table needs one (like a file number in the personnel cabinet).
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY) — "The database auto-assigns this number when a row is inserted." IDENTITY strategy = PostgreSQL's auto-increment feature generates it. Why Long? It's nullable + huge (can't overflow easily with IDs). Also the generic type used by Spring Data for JpaRepository<User, Long> lookups.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,
            unique = true,
            length = 100)
    private String email;

    @Column(nullable = false)
    private String password;
//@Enumerated(EnumType.STRING) — critical! Tells Hibernate: "store the enum as its NAME string ('ADMIN'), not as its numeric position (0)." Why it matters: Storing the name string is stable — you can reorder/insert enum values later without corrupting existing rows. Storing the ordinal (number) breaks data when the enum changes. Always use STRING in real projects!
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,
            length = 30)
    private Role role;

    @Column(nullable = false)
    private boolean enabled = true;
}