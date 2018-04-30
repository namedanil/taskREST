package test.moysklad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/bankaccount")
public class BankAccountController {

    @Autowired
    private IBankAccountService bankAccounts;

    @RequestMapping("/")
    public List<BankAccount> getAll() { //выводим все BankAccount
        return bankAccounts.getAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public String create(@PathVariable Integer id) {
        String message = bankAccounts.addAccount(id);
        if (message.equals(bankAccounts.Success)) {//проверяем, что аккаунт добавлен
            return message + bankAccounts.getById(id).toString();
        }
        return message;//выводим ошибку
    }

    @RequestMapping(value = "/{id}/deposit/{credit}", method = RequestMethod.PUT)
    public String deposit(@PathVariable Integer id,
                          @PathVariable Integer credit) {
        if (credit < 0) { //исключаем ввод отрицательной суммы
            return bankAccounts.errPositiveDeposit;
        }
        String message = bankAccounts.updateBalance(id, credit);
        try {
            return message + bankAccounts.getById(id).toString();//выводим обновлённые данные
        } catch (NullPointerException ex) {
            return message;//выводим ошибку
        }
    }

    @RequestMapping(value = "/{id}/withdraw/{credit}", method = RequestMethod.PUT)
    public String withdraw(@PathVariable Integer id,
                           @PathVariable Integer credit) {
        if (credit < 0) {//исключаем ввод отрицательной суммы
            return bankAccounts.errPositiveWithdraw;
        }
        String message = bankAccounts.updateBalance(id, -credit);//credit с минусом, потому что деньги снимаем, а не зачисляем
        try {
            return message + bankAccounts.getById(id).toString();//выводим обновлённые данные
        } catch (NullPointerException ex) {
            return message;//выводим ошибку
        }
    }

    @RequestMapping("/{id}/balance")
    public String checkBalance(@PathVariable Integer id) {
        try{
            return bankAccounts.Success + bankAccounts.getById(id).toString();
        }catch (NullPointerException ex){
            return bankAccounts.errIdNotRegistered;//выводим ошибку, если аккаунта с таким id нет
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String delete(@PathVariable Integer id) {
        try {
            BankAccount bankAccount = bankAccounts.getById(id);
            if (bankAccount.getBalance() > 0) {//проверяем наличие денег на балансе аккаунта
                return bankAccounts.errBalanceNotZero + bankAccount.toString();
            }
        }catch (NullPointerException ex){//обрабатываем исключение в случае некорректного id
            return bankAccounts.errIdNotRegistered;
        }
        return bankAccounts.removeAccount(id);
    }
}
