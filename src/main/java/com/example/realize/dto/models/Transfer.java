package com.example.realize.dto.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.UUID;

@Entity
@Table(name = "transfers")
@Getter
@Setter
public class Transfer {
    @Id
    @JdbcTypeCode(Types.BINARY)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "amount", nullable = false)
    private int amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "incoming_account_id",
            referencedColumnName = "id",
            nullable = false,
            columnDefinition = "BINARY(16)"
    )
    private Account incomingAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "outcoming_account_id",
            referencedColumnName = "id",
            nullable = false,
            columnDefinition = "BINARY(16)"
    )
    private Account outcomingAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransferStatus status;
}