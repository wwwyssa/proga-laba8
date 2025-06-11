package com.lab8.server.commands;


import com.lab8.common.models.Product;
import com.lab8.common.util.User;
import com.lab8.common.util.executions.ExecutionResponse;
import com.lab8.common.validators.IdValidator;
import com.lab8.common.validators.NoArgumentsValidator;
import com.lab8.server.Server;
import com.lab8.server.managers.CollectionManager;
import com.lab8.server.util.AskingCommand;

/**
 * Класс команды для обновления значения элемента коллекции по его id.
 */
public class Update extends AskingCommand<NoArgumentsValidator>{
    private final CollectionManager collectionManager;

    /**
     * Конструктор команды update.
     */

    public Update(CollectionManager collectionManager) {
        super("update " + " id {element}", "update product", new NoArgumentsValidator());
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду обновления элемента коллекции.
     *
     * @param product Элемент коллекции.
     * @return Статус выполнения команды.
     */
    @Override
    protected ExecutionResponse innerExecute(Product product, User user) {
        Server.logger.info("Выполняется команда update с id: " + product.getId() + " от пользователя: " + user.getName());
        return collectionManager.update(product, user);
    }
}