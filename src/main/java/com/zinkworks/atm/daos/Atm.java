package com.zinkworks.atm.daos;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name="atms")
public class Atm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long cashTotal;

    @Column(nullable = false)
    private Integer noOfFifty;

    @Column(nullable = false)
    private Integer noOfTwenty;

    @Column(nullable = false)
    private Integer noOfTen;

    @Column(nullable = false)
    private Integer noOfFive;

}
