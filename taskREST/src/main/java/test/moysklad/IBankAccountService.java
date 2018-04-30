package test.moysklad;

import java.util.List;

public interface IBankAccountService {
    //код 200 - всё ок
    //код 400 - ошибка на стороне клиента
    //код 404 - информация не найдена
    //код 500 - ошибка на стороне сервера
    public final static String Success = "200\nSuccess!\n";
    public final static String errPositiveDeposit = "400\nError!\nThe deposit must be positive.";
    public final static String errPositiveWithdraw = "400\nError!\nThe withdraw must be positive.";
    public final static String errIdNotRegistered = "404\nError!\nThe ID entered is not registered."; //id не найден
    public final static String errIdNotAvailable = "500\nError!\nThe ID entered is not available."; //id занят
    public final static String errIncorrectId = "400\nError!\nThe ID must be a five-digit number."; //ограничение на id
    public final static String errBalanceNotZero = "500\nError!\nThere is money on this account.\n"; //нельзя удалить, пока balance != 0
    public final static String errInsufFunds = "400\nError!\nInsufficient funds.\n"; //недостаточно денег для withdraw
    public final static String SuccessDelete = "200\nSuccess!\nAccount was deleted.";

    public List<BankAccount> getAll();

    public BankAccount getById(Integer id);

    public String updateBalance(Integer id, Integer credit);

    public String addAccount(Integer id);

    public String removeAccount(Integer id);
}
