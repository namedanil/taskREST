package test.moysklad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import test.moysklad.Exception.BalanceNotZeroException;
import test.moysklad.Exception.PositiveCreditException;

import java.util.List;

@RestController()
@RequestMapping("/bankaccount")
public class BankAccountController {

    @Autowired
    private IBankAccountService bankAccounts;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<BankAccount> getAll() { //выводим все BankAccount
        return bankAccounts.getAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public BankAccount create(@PathVariable Integer id) {
//        Integer ID = Integer.parseInt(id);
        if (id < 10000 | id > 99999) { //исключаем ввод некорректного id
            throw new NumberFormatException();
        }
        return bankAccounts.addAccount(id);
    }

    @RequestMapping(value = "/{id}/deposit/{credit}", method = RequestMethod.PUT)
    public BankAccount deposit(@PathVariable Integer id,
                               @PathVariable Integer credit) {
        if (credit < 0) { //исключаем ввод отрицательной суммы
            throw new PositiveCreditException();
        }
        return bankAccounts.updateBalance(id, credit);
    }

    @RequestMapping(value = "/{id}/withdraw/{credit}", method = RequestMethod.PUT)
    public BankAccount withdraw(@PathVariable Integer id,
                                @PathVariable Integer credit) {
        if (credit < 0) {//исключаем ввод отрицательной суммы
            throw new PositiveCreditException();
        }
        return bankAccounts.updateBalance(id, -credit);//credit с минусом, потому что деньги снимаем, а не зачисляем
    }

    @RequestMapping(value = "/{id}/balance", method = RequestMethod.GET)
    public BankAccount checkBalance(@PathVariable Integer id) {
        return bankAccounts.getById(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public SomeMessage delete(@PathVariable Integer id) {
        BankAccount bankAccount = bankAccounts.getById(id);
        if (bankAccount.getBalance() > 0) {//проверяем наличие денег на балансе аккаунта
            throw new BalanceNotZeroException();
        }
        bankAccounts.removeAccount(id);
        return new SomeMessage("Account was deleted.");
    }

    private static class SomeMessage {
        private String message;

        public SomeMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
