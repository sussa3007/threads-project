package com.challenge.project.generatedata.entity;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "counting")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Counting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Setter
    private Long counting;
}
