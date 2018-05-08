package test.moysklad;

import java.util.List;

public interface IBankAccountService {

    public List<BankAccount> getAll();

    public BankAccount getById(Integer id);

    public BankAccount updateBalance(Integer id, Integer credit);

    public BankAccount addAccount(Integer id);

    public void removeAccount(Integer id);
}
