package com.lab8.server.commands;

import com.lab8.common.util.User;
import com.lab8.common.util.executions.AnswerString;
import com.lab8.common.util.executions.ExecutionResponse;
import com.lab8.common.validators.IdValidator;
import com.lab8.server.managers.CollectionManager;


/**
 * Команда 'remove_by_id'. Удаляет элемент из коллекции.
 */
public class RemoveById extends Command<IdValidator> {
    private final CollectionManager collectionManager;

    public RemoveById(CollectionManager collectionManager) {
        super("remove_by_id <ID>", "удалить элемент из коллекции по ID", 1, new IdValidator());
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public ExecutionResponse innerExecute(String args, User user) {
        long id = -1;
        try {
            id = Long.parseLong(args);
        } catch (NumberFormatException e) {
            return new ExecutionResponse<>(false, new AnswerString("ID не распознан"));
        }

        System.out.println(collectionManager.getCollection().toString());

        if (collectionManager.getById(id) == null)

            return new ExecutionResponse<>(false, new AnswerString("Не существующий ID"));
        try {
            ExecutionResponse resp = collectionManager.removeProduct(id, user);
            if (resp.getExitCode()) {
                return new ExecutionResponse<>(new AnswerString("Продукт успешно удалён!"));
            } else {
                return new ExecutionResponse(false, new AnswerString("Не удалось удалить продукт: " + resp.getAnswer()));
            }

        } catch (Exception e) {
            return new ExecutionResponse<>(false, new AnswerString("Не удалось удалить продукт: " + e.getMessage()));
        }

    }
}