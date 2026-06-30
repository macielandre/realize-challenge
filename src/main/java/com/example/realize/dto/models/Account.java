package com.example.realize.dto.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Account {
    @Id
    @JdbcTypeCode(Types.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "balance", nullable = false)
    private int balance;

    @Column(name = "name", length = 150, nullable = false)
    private String name;
}
