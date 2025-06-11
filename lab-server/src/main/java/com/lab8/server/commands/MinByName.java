package com.lab8.server.commands;


import com.lab8.common.models.Product;
import com.lab8.common.util.User;
import com.lab8.common.util.executions.AnswerString;
import com.lab8.common.util.executions.ExecutionResponse;
import com.lab8.common.util.executions.ListAnswer;
import com.lab8.common.validators.NoArgumentsValidator;
import com.lab8.server.managers.CollectionManager;

import java.util.ArrayList;
import java.util.List;


public class MinByName extends Command<NoArgumentsValidator> {
    private final CollectionManager collectionManager;

    public MinByName(CollectionManager collectionManager) {
        super("min_by_name", "вывести объект из коллекции, имя которого является минимальным", 0, new NoArgumentsValidator());
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse innerExecute(String arguments, User user) {
        if (collectionManager.getCollection().isEmpty()) { return new ExecutionResponse<>(false, new AnswerString("Коллекция пуста."));}
        Product minProduct = null;
        String minName = null;
        for (Product product : collectionManager.getCollection().values()) {
            if (minName == null || product.getName().compareTo(minName) < 0) {
                minName = product.getName();
                minProduct = product;
            }
        }
        if (minProduct == null) {
            return new ExecutionResponse<>(false, new AnswerString("Произошла ошибка при поиске элемента с минимальным именем."));
        }
        List<Product> products = new ArrayList<>();
        products.add(minProduct);
        return new ExecutionResponse<>(true, new ListAnswer(products));
    }
}
