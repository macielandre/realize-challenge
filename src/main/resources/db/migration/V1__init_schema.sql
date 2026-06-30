BEGIN;

CREATE TABLE `accounts` (
    `id` binary(16) NOT NULL,
    `balance` int NOT NULL,
    `name` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `transfers` (
    `id` binary(16) NOT NULL,
    `amount` int NOT NULL,
    `incoming_account_id` binary(16) NOT NULL,
    `outcoming_account_id` binary(16) NOT NULL,
    `status` enum('done', 'error', 'processing') NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `transfers_accounts_FK` FOREIGN KEY (`incoming_account_id`) REFERENCES `accounts` (`id`),
    CONSTRAINT `transfers_accounts_FK_1` FOREIGN KEY (`outcoming_account_id`) REFERENCES `accounts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

COMMIT;