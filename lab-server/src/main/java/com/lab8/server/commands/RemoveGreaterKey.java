package com.lab8.server.commands;


import com.lab8.common.util.User;
import com.lab8.common.util.executions.AnswerString;
import com.lab8.common.util.executions.ExecutionResponse;
import com.lab8.common.validators.IdValidator;
import com.lab8.server.managers.CollectionManager;

public class RemoveGreaterKey extends Command<IdValidator> {
    private final CollectionManager collectionManager;

    public RemoveGreaterKey(CollectionManager collectionManager) {
        super("remove_greater_key", "удалить из коллекции все элементы, ключ которых превышает заданный", 1, new IdValidator());
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse innerExecute(String arguments, User user) {
        try {
            String[] args = arguments.split(" ", 2);
            System.out.println(args[0]);
            int key = Integer.parseInt(arguments);
            for (long keyToRemove : collectionManager.getCollection().keySet()) {
                System.out.println(keyToRemove + " " + key);
                if (keyToRemove > key) {
                    collectionManager.removeProduct(keyToRemove, user);
                }
            }
            return new ExecutionResponse<>(true, new AnswerString("Все элементы с ключом больше " + key + " успешно удалены."));
        } catch (NumberFormatException e) {
            return new ExecutionResponse<>(false,new AnswerString( "Ключ должен быть числом."));
        }
    }
}
