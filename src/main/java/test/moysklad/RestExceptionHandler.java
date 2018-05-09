package test.moysklad;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import test.moysklad.Exception.BalanceNotZeroException;
import test.moysklad.Exception.PositiveCreditException;
@ControllerAdvice
public class RestExceptionHandler {
    //код 400 - ошибка на стороне клиента
    //код 404 - информация не найдена
    public final static String errNotFound = "Not found.";
    public final static String errBadRequest = "Bad request.";
    public final static String mesPositiveCredit = "The credit must be positive number. ";
    public final static String mesIdNotFound = "The ID entered is not found."; //id не найден
    public final static String mesIdNotAvailable = "The ID entered is not available."; //id занят
    public final static String mesIncorrectId = "The ID must be a five-digit number."; //ограничение на id
    public final static String mesBalanceNotZero = "There is money on this account."; //нельзя удалить, пока balance != 0
    public final static String mesInsufFunds = "Insufficient funds. "; //недостаточно денег для withdraw

    public class ExceptionJSONInfo {
        private String error;
        private String details;
        private String message;

        public ExceptionJSONInfo(String error, String details, String message) {
            this.error = error;
            this.details = details;
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getDetails() {
            return details;
        }

        public void setDetails(String details) {
            this.details = details;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @ExceptionHandler({EmptyResultDataAccessException.class})
    protected ResponseEntity<ExceptionJSONInfo> handleIdNotFound(WebRequest request) {

        return new ResponseEntity<>(new ExceptionJSONInfo(errNotFound, request.getDescription(false), mesIdNotFound),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    protected ResponseEntity<ExceptionJSONInfo> handleInsufFunds(WebRequest request) {

        return new ResponseEntity<>(new ExceptionJSONInfo(errBadRequest, request.getDescription(false),
                mesInsufFunds), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({DuplicateKeyException.class})
    protected ResponseEntity<ExceptionJSONInfo> handleIdNotAvailable(WebRequest request) {

        return new ResponseEntity<>(new ExceptionJSONInfo(errBadRequest, request.getDescription(false),
                mesIdNotAvailable), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NumberFormatException.class})
    protected ResponseEntity<ExceptionJSONInfo> handleIncorrectId(NumberFormatException ex, WebRequest request) {

        return new ResponseEntity<>(new ExceptionJSONInfo(errBadRequest, request.getDescription(false),
                mesIncorrectId+mesPositiveCredit), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({PositiveCreditException.class})
    protected ResponseEntity<ExceptionJSONInfo> handlePositiveCredit(WebRequest request) {

        return new ResponseEntity<>(new ExceptionJSONInfo(errBadRequest, request.getDescription(false),
                mesPositiveCredit), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({BalanceNotZeroException.class})
    protected ResponseEntity<ExceptionJSONInfo> handleBalanceNotZero(WebRequest request) {

        return new ResponseEntity<>(new ExceptionJSONInfo(errBadRequest, request.getDescription(false),
                mesBalanceNotZero), HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler({Exception.class})
//    protected ResponseEntity<Object> handleAllTheRest() {
//        return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
//    }
}
