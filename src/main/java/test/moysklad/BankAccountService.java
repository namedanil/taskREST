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
        BankAccount bankAccount = (BankAccount) jtm.queryForObject(sql, new Object[]{id},
                new BeanPropertyRowMapper(BankAccount.class));
        return bankAccount;
    }

    @Override
    public BankAccount updateBalance(Integer id, Integer credit) {
        String sql = "UPDATE BankAccounts " +
                "SET BALANCE = BALANCE + " + credit.toString() + " " +
                "WHERE ID=" + id.toString();
        jtm.update(sql);
        return getById(id);
    }

    @Override
    public BankAccount addAccount(Integer id) {
        String sql = "INSERT INTO BankAccounts(ID) " +
                "VALUES(" + id.toString() + ")";
        jtm.execute(sql);
        return getById(id);
    }

    @Override
    public void removeAccount(Integer id) {
        String sql = "DELETE FROM BankAccounts " +
                "WHERE ID = " + id.toString();
        jtm.update(sql);
    }
}
