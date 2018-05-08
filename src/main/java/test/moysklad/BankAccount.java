package test.moysklad;

public class BankAccount {

    private Integer id;
    private Integer balance;

    public BankAccount(){}

    public BankAccount(Integer id){
        balance = 0;
        this.id = id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "id=" + id +
                ", balance=" + balance +
                '}';
    }
}
