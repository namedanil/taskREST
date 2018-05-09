package test.moysklad;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import test.moysklad.Exception.BalanceNotZeroException;
import test.moysklad.Exception.PositiveCreditException;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static test.moysklad.RestExceptionHandler.*;

@RunWith(SpringRunner.class)
@WebMvcTest(BankAccountController.class)
public class BankAccountControllerTest {

    public final static String PATH = "/bankaccount/";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BankAccountController bankAccountController;

    @Test
    public void getAll() throws Exception {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(11111);

        List<BankAccount> bankAccounts = singletonList(bankAccount);

        given(bankAccountController.getAll()).willReturn(bankAccounts);

        mockMvc.perform(get(PATH)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bankAccount.getId())));
    }

    @Test
    public void create() throws Exception {
        BankAccount bankAccount = new BankAccount(11111);

        given(bankAccountController.create(bankAccount.getId()))
                .willReturn(bankAccount);
        //добавляем с корректный id
        mockMvc.perform(post(PATH + bankAccount.getId())
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(bankAccount.getId())));
        //добавляем некорректный id (буквы)
        mockMvc.perform(post(PATH + "asd")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error", is(errBadRequest)))
                .andExpect(jsonPath("message", is(mesIncorrectId + mesPositiveCredit)));

        //добавляем некорректный id (выход за диапозон 10000-99999)
        bankAccount.setId(9999);
        given(bankAccountController.create(bankAccount.getId()))
                .willThrow(new NumberFormatException());
        mockMvc.perform(post(PATH + bankAccount.getId())
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error", is(errBadRequest)))
                .andExpect(jsonPath("message", is(mesIncorrectId + mesPositiveCredit)));

        //добавляем id, который уже зарегистрирован
        bankAccount.setId(11111);
        given(bankAccountController.create(bankAccount.getId()))
                .willThrow(new DuplicateKeyException(""));
        mockMvc.perform(post(PATH + bankAccount.getId())
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error", is(errBadRequest)))
                .andExpect(jsonPath("message", is(mesIdNotAvailable)));
    }

    @Test
    public void deposit() throws Exception {
        BankAccount bankAccountBefore = new BankAccount(11111);
        BankAccount bankAccountAfter = new BankAccount(11111);
        Integer credit = 777;
        bankAccountAfter.setBalance(777);

        given(bankAccountController.deposit(bankAccountBefore.getId(), credit))
                .willReturn(bankAccountAfter);
        //корректный deposit
        mockMvc.perform(put(PATH + bankAccountAfter.getId()
                + "/deposit/" + credit)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(11111)))
                .andExpect(jsonPath("balance", is(777)));

        //некорректный deposit (отрицательный credit)
        given(bankAccountController.deposit(bankAccountBefore.getId(), -credit))
                .willThrow(new PositiveCreditException());
        mockMvc.perform(put(PATH + bankAccountAfter.getId()
                + "/deposit/" + -credit)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error", is(errBadRequest)))
                .andExpect(jsonPath("message", is(mesPositiveCredit)));
    }

    @Test
    public void withdraw() throws Exception {
        BankAccount bankAccountBefore = new BankAccount(11111);
        BankAccount bankAccountAfter = new BankAccount(11111);
        Integer credit = 777;
        bankAccountBefore.setBalance(credit);

        given(bankAccountController.withdraw(bankAccountBefore.getId(), credit))
                .willReturn(bankAccountAfter);
        //корректный withdraw
        mockMvc.perform(put(PATH + bankAccountAfter.getId()
                + "/withdraw/" + credit)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(11111)))
                .andExpect(jsonPath("balance", is(0)));

        //некорректный withdraw (отрицательный credit)
        given(bankAccountController.withdraw(bankAccountBefore.getId(), -credit))
                .willThrow(new PositiveCreditException());
        mockMvc.perform(put(PATH + bankAccountAfter.getId()
                + "/withdraw/" + -credit)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error", is(errBadRequest)))
                .andExpect(jsonPath("message", is(mesPositiveCredit)));

        //некорректный withdraw (balance < credit)
        given(bankAccountController.withdraw(bankAccountBefore.getId(),
                credit*credit))
                .willThrow(new DataIntegrityViolationException(""));
        mockMvc.perform(put(PATH + bankAccountAfter.getId()
                + "/withdraw/" + credit*credit)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error", is(errBadRequest)))
                .andExpect(jsonPath("message", is(mesInsufFunds)));
    }

    @Test
    public void checkBalance() throws Exception {
        BankAccount bankAccount = new BankAccount(11111);

        given(bankAccountController.checkBalance(bankAccount.getId()))
                .willReturn(bankAccount);
        //корректная проверка баланса
        mockMvc.perform(get(PATH + bankAccount.getId().toString() + "/balance")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(bankAccount.getId())))
                .andExpect(jsonPath("balance", is(bankAccount.getBalance())));

        //некорректная проверка (id не существует)
        given(bankAccountController.checkBalance(bankAccount.getId()))
                .willThrow(new EmptyResultDataAccessException(0));
        mockMvc.perform(get(PATH + bankAccount.getId().toString() + "/balance")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error", is(errNotFound)))
                .andExpect(jsonPath("message", is(mesIdNotFound)));
    }

    @Test
    public void delete() throws Exception {
        BankAccount bankAccount = new BankAccount(11111);

        given(bankAccountController.delete(bankAccount.getId()))
                .willReturn(new BankAccountController.SomeMessage("Account was deleted."));
        //корректное удаление
        mockMvc.perform(MockMvcRequestBuilders.delete(PATH + bankAccount.getId())
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message", is("Account was deleted.")));

        //удаление аккаунта с ненулевым балансом
        bankAccount.setBalance(777);
        given(bankAccountController.delete(bankAccount.getId()))
                .willThrow(new BalanceNotZeroException());
        mockMvc.perform(MockMvcRequestBuilders.delete(PATH + bankAccount.getId())
                .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error", is(errBadRequest)))
                .andExpect(jsonPath("message", is(mesBalanceNotZero)));
    }
}