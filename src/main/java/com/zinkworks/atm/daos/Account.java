package com.zinkworks.atm.daos;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(
        name = "accounts",
        uniqueConstraints = @UniqueConstraint(name = "uc_account_number", columnNames = {"accountNumber"})
)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @NotNull(message = "Account Number can not be null!")
    private Long accountNumber;
    @Column(nullable = false)
    @NotNull(message = "Pin can not be null!")
    private Integer pin;
    @Column(nullable = false)
    private Integer openingBalance;
    @Column(nullable = false)
    private Integer overDraft;

}
