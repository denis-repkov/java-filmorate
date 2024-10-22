package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.filmorate.exception.ValidationExceptions;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Тестирование контроллера пользователей")
class UserControllerTest {
    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private UserService userService;
    private UserController userController;
    private User user;

    @BeforeEach
    public void beforeEach() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userService);

        user = new User();
        user.setEmail("11@email.ru");
        user.setLogin("login");
        user.setName("name");
        user.setBirthday(LocalDate.now());
    }

    @Test
    @DisplayName("Если адрес эл.почты не содержит @ - получаем ошибку")
    void ifNotContainsDogError() {
        user.setEmail("11email.ru");
        assertThrows(ValidationExceptions.class, () -> userController.createUser(user));
    }

    @Test
    @DisplayName("Если указан пустой адрес эл.почты - получаем ошибку")
    void ifEmptyEmailError() {
        user.setEmail("");
        assertThrows(ValidationExceptions.class, () -> userController.createUser(user));
    }

    @Test
    @DisplayName("Если указан пустой логин - получаем ошибку")
    void ifEmptyLoginError() {
        user.setLogin("");
        assertThrows(ValidationExceptions.class, () -> userController.createUser(user));
    }

    @Test
    @DisplayName("Если логин содержит пробел - получаем ошибку")
    void ifContainsSpaceLoginError() {
        user.setLogin("login login");
        assertThrows(ValidationExceptions.class, () -> userController.createUser(user));
    }

    @Test
    @DisplayName("Если дата рождения указана в будущем - получаем ошибку")
    void ifBirthdayAfterNowError() {
        user.setBirthday(LocalDate.now().plusDays(1));
        assertThrows(ValidationExceptions.class, () -> userController.createUser(user));
    }

}