package com.lab8.server.util;

import com.lab8.common.util.User;
import com.lab8.common.util.executions.ExecutionResponse;
import com.lab8.common.validators.ArgumentValidator;

public interface Executable {
    /**
     * Выполнить что-либо.
     *
     * @param arguments Аргумент для выполнения
     * @return результат выполнения
     */
    ExecutionResponse execute(String arguments, User user);

    ArgumentValidator getArgumentValidator();

    String getName();
}