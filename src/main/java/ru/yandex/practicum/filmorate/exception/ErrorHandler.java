package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({ValidationExceptions.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotValidArgsException(Exception e) {
        log.info("Ошибка валидации: {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), e.getMessage());
    }

    @ExceptionHandler({NotFoundExceptions.class, HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(Exception e) {
        log.info("Элемент не найден: {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerException(Throwable e) {
        log.info("Внутренняя ошибка сервера: ", e);
        return new ErrorResponse(e.getMessage(), e.getMessage());
    }
}
