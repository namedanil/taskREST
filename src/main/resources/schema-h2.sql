CREATE TABLE BankAccounts(
ID BIGINT PRIMARY KEY,
BALANCE BIGINT DEFAULT 0 CONSTRAINT positive_balance CHECK (BALANCE > -1),
CHECK (ID BETWEEN 10000 AND 99999));