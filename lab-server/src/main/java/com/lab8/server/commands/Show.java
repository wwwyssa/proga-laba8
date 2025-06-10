package com.lab8.server.commands;

import com.lab8.common.util.User;
import com.lab8.common.util.executions.ExecutionResponse;
import com.lab8.common.util.executions.ListAnswer;
import com.lab8.common.validators.NoArgumentsValidator;
import com.lab8.server.managers.CollectionManager;

/**
 * Команда 'show'. Выводит все элементы коллекции.
 */
public class Show extends Command<NoArgumentsValidator> {
    private final CollectionManager collectionManager;

    public Show(CollectionManager collectionManager) {
        super("show", "вывести в стандартный поток вывода все элементы коллекции в строковом представлении", 0, new NoArgumentsValidator());
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public ExecutionResponse<ListAnswer> innerExecute(String arguments, User user) {
        return new ExecutionResponse<>(new ListAnswer(collectionManager.getCollection().values().stream().toList()));
    }
}