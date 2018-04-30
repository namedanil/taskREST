package test.moysklad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankAccountService implements IBankAccountService {

    @Autowired
    private JdbcTemplate jtm;

    @Override
    public List<BankAccount> getAll() {
        String sql = "SELECT * FROM BankAccounts";
        List<BankAccount> bankAccounts = jtm.query(sql, new BeanPropertyRowMapper(BankAccount.class));
        return bankAccounts;
    }

    @Override
    public BankAccount getById(Integer id) {
        String sql = "SELECT * FROM BankAccounts " +
                "WHERE ID=?";
        BankAccount bankAccount;
        try {
            bankAccount = (BankAccount) jtm.queryForObject(sql, new Object[]{id},
                    new BeanPropertyRowMapper(BankAccount.class));
        } catch (EmptyResultDataAccessException ex) {
            return null; //если не находим BankAccount с заданным id, возвращаем null
        }
        return bankAccount;
    }

    @Override
    public String updateBalance(Integer id, Integer credit) {
        String sql = "UPDATE BankAccounts " +
                "SET BALANCE = BALANCE + " + credit.toString() + " " +
                "WHERE ID=" + id.toString();
        Integer isUpdate; //переменная для проверки количества обновлённых строк
        try {
            isUpdate = jtm.update(sql);
        } catch (DataIntegrityViolationException ex) { //пытаемся снять больше значения balance
            return errInsufFunds;
        }
        if (isUpdate < 1) { //проверяем, что ни одна строка не обновилась
            return errIdNotRegistered;
        }
        return Success;
    }

    @Override
    public String addAccount(Integer id) {
        String sql = "INSERT INTO BankAccounts(ID) " +
                "VALUES(" + id.toString() + ")";
        try {
            jtm.execute(sql);
        } catch (DuplicateKeyException ex) { //пытаемся зарегестрировать уже существующий id
            return errIdNotAvailable;
        } catch (DataIntegrityViolationException | NumberFormatException ex) { //пытаемся зарегистрировать BankAccount с некорректным id
            return errIncorrectId;
        }
        return Success;
    }

    @Override
    public String removeAccount(Integer id) {
        String sql = "DELETE FROM BankAccounts " +
                "WHERE ID = " + id.toString();
        jtm.update(sql);
        return SuccessDelete;
    }

}
