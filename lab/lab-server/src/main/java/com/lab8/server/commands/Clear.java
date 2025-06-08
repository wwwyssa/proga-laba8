package com.lab8.server.commands;

import com.lab8.common.util.User;
import com.lab8.common.util.executions.AnswerString;
import com.lab8.common.util.executions.ExecutionResponse;
import com.lab8.common.validators.NoArgumentsValidator;
import com.lab8.server.managers.CollectionManager;


/**
 * Команда 'clear'. Очищает коллекцию.
 *
 * @author dim0n4eg
 */
public class Clear extends Command<NoArgumentsValidator> {
    private final CollectionManager collectionManager;

    public Clear(CollectionManager collectionManager) {
        super("clear", "очистить коллекцию", 0, new NoArgumentsValidator());
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public ExecutionResponse innerExecute(String arguments, User user) {
        collectionManager.clear(user);
        return new ExecutionResponse(new AnswerString("Коллекция очищена!"));
    }
}