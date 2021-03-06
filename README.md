# taskREST или реализация REST API по работе со счетами
### Список возможных команд:
+ GET /bankaccount/ - Просмотреть данные всех аккаунтов. Без входных параметров.
  Возможные ответы:
    - список счетов в формате JSON
    - пустой список, если в базе нет ни одного счета
+ POST /bankaccount/{id} - Завести новый счет. На вход команда принимает параметр номер счета - 5-ти значное число.
  Возможные ответы:
    - статус 200 и счет
    - статус 400, так как введенный id уже зарегистрирован
    - статус 400, так как введено не 5-ти значное число
    - статус 400, так как введено не число
+ PUT /bankaccount/{id}/deposit/{credit} - Внести сумму на счет. На вход команда принимает 2 параметра - номер счета (id) и сумму к  зачислению (credit).
  Возможные ответы:
    - статус 200 и сам счет
    - статус 400, так как credit отрицательное число
    - статус 400, так как credit не число
    - статус 400, так как id не 5-ти значное число
    - статус 400, так как id не зарегистрирован
    - статус 400, так как id не число
+ PUT /bankaccount/{id}/withdraw/{credit} - Снять сумму со счет. На вход команда принимает 2 параметра - номер счета (id) и сумму снятия (credit).
  Возможные ответы:
    - статус 200 и сам счет
    - статус 400, так как credit отрицательное число
    - статус 400, так как credit не число
    - статус 400, так как id не 5-ти значное число
    - статус 400, так как id не зарегистрирован
    - статус 400, так как id не число
    - статус 400, так как сумма снятия больше баланса счета и сам счет
+ GET /bankaccount/{id}/balance - Узнать баланс. На вход команда принимает параметр номер счета - 5-ти значное число.
  Возможные ответы:
    - статус 200 и счет
    - статус 404, так как счет с введенным ID еще не зарегистрирован
+ DELETE /bankaccount/{id} - Удалить счет. На вход команда принимает параметр номер счета - 5-ти значное число.
  Возможные ответы:
    - статус 200 и сообщение о успешном удалении
    - статус 400, так как введенный id еще не зарегистрирован
    - статус 400, так как введено не 5-ти значное число
    - статус 400, так как введено не число
### Инструкция по сборке, настройке, конфигурированию и развертыванию приложения:
+ Скачиваем проект
+ Запускаем его в IDE
+ В терминале IDE прописываем "mvn spring-boot:run"  
  **или**  
  "mvn clean package", а потом запускаем jar файл "java -jar target/taskREST-0.1.0.jar"
+ Далее в консоли (с помощью curl) или с помощью приложений типа Advanced REST Client проверяем команды  
Пример curl-запроса:
  **curl -X POST "http://localhost:8086/bankaccount/10001"**  
По умолчанию настроен порт 8086, но его можно поменять в файле **application.yml**
+ Появился тест **BankAccountControllerTest.java**, который проверяет основные запросы и обработку ошибок.
Запускается он непосредственно в IDE.
