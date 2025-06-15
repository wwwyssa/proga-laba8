package com.lab8.server.commands;


import com.lab8.common.models.Product;
import com.lab8.common.util.User;
import com.lab8.common.util.executions.AnswerString;
import com.lab8.common.util.executions.ExecutionResponse;
import com.lab8.common.validators.NoArgumentsValidator;
import com.lab8.server.managers.CollectionManager;
import com.lab8.server.util.AskingCommand;

import static java.lang.Thread.sleep;

/**
 * Класс, представляющий команду добавления нового продукта в коллекцию.
 */
public class Add  extends AskingCommand<NoArgumentsValidator> {
    private final CollectionManager collectionManager;

    /**
     * Конструктор для создания объекта Add.
     * @param collectionManager менеджер коллекции для управления продуктами
     */
    public Add(CollectionManager collectionManager) {
        super("add {Product}", "add new Product to collection", new NoArgumentsValidator());
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду добавления нового продукта в коллекцию.
     * @return результат выполнения команды
     */
    @Override
    public ExecutionResponse innerExecute(Product product, User user) {
        product.setCreator(user.getName());
        try{
            sleep(5000)   ; // Имитируем задержку для демонстрации
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Восстанавливаем прерывание
            return new ExecutionResponse(false, new AnswerString("Команда была прервана."));
        }
        System.out.println(product);
        System.out.println("User: " + user.getName());
        collectionManager.addProduct(product, user);
        return new ExecutionResponse(true, new AnswerString("Элемент успешно добавлен в коллекцию!"));
    }
}