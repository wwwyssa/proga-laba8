package com.lab8.client.util.exceptions;

/**
 * Выбрасывается, если при регистрации пользователь уже существует.
 * @author maxbarsukov
 */
public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException() {
        super();
    }
}