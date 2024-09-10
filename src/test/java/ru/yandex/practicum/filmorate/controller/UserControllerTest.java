package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationExceptions;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Тестирование контроллера пользователей")
class UserControllerTest {
    private UserController userController;
    private User user;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController();
        user = User.builder().email("11@email.ru").login("login").name("name").birthday(LocalDate.now()).build();
    }

    @Test
    @DisplayName("Если адрес эл.почты не содержит @ - получаем ошибку")
    void ifNotContainsDogError() {
        user.setEmail("11email.ru");
        assertThrows(ValidationExceptions.class, () -> userController.create(user));
    }

    @Test
    @DisplayName("Если указан пустой адрес эл.почты - получаем ошибку")
    void ifEmptyEmailError() {
        user.setEmail("");
        assertThrows(ValidationExceptions.class, () -> userController.create(user));
    }

    @Test
    @DisplayName("Если указан пустой логин - получаем ошибку")
    void ifEmptyLoginError() {
        user.setLogin("");
        assertThrows(ValidationExceptions.class, () -> userController.create(user));
    }

    @Test
    @DisplayName("Если логин содержит пробел - получаем ошибку")
    void ifContainsSpaceLoginError() {
        user.setLogin("login login");
        assertThrows(ValidationExceptions.class, () -> userController.create(user));
    }

    @Test
    @DisplayName("Если дата рождения указана в будущем - получаем ошибку")
    void ifBirthdayAfterNowError() {
        user.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationExceptions.class, () -> userController.create(user));
    }

}