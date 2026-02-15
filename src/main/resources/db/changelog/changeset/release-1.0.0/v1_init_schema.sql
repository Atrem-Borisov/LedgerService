-- accounts
CREATE TABLE accounts (
                          id UUID PRIMARY KEY,
                          name VARCHAR(255) NOT NULL UNIQUE,
                          type VARCHAR(20) NOT NULL,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- transactions
CREATE TABLE transactions (
                              id UUID PRIMARY KEY,
                              transaction_date TIMESTAMP NOT NULL,
                              description TEXT NOT NULL,
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- transaction_entries
CREATE TABLE transaction_entries (
                                     id UUID PRIMARY KEY,
                                     transaction_id UUID NOT NULL REFERENCES transactions(id) ON DELETE CASCADE,
                                     account_id UUID NOT NULL REFERENCES accounts(id) ON DELETE RESTRICT,
                                     entry_type VARCHAR(10) NOT NULL,
                                     amount NUMERIC(19,4) NOT NULL CHECK (amount > 0)
);

-- Indexes
CREATE INDEX idx_transaction_entries_account_id
    ON transaction_entries(account_id);

CREATE INDEX idx_transaction_entries_transaction_id
    ON transaction_entries(transaction_id);
