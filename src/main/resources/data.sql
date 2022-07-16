-- Create 2 Accounts

insert into accounts (id, account_number, pin, opening_balance, over_draft) values (1, 123456789, 1234, 800, 200);

insert into accounts (id, account_number, pin, opening_balance, over_draft) values (2, 987654321, 4321, 1230, 150);

-- Create 1 Atm

insert into atms (id, cash_total, no_of_fifty, no_of_twenty, no_of_ten, no_of_five) values (1, 1500, 10, 30, 30, 20);
