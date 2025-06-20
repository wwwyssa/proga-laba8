package com.lab8.server.commands;

import com.lab8.common.util.User;
import com.lab8.common.util.executions.AnswerString;
import com.lab8.common.util.executions.ExecutionResponse;
import com.lab8.common.validators.NoArgumentsValidator;
import com.lab8.server.managers.CollectionManager;


/**
 * Класс команды для вывода среднего значения поля manufactureCost для всех элементов коллекции.
 */
public class AverageOfManufactureCost extends Command<NoArgumentsValidator>{
    private final CollectionManager collectionManager;


    /**
     * Конструктор класса
     * @param collectionManager объект менеджера коллекции
     */
    public AverageOfManufactureCost(CollectionManager collectionManager){
        super("average_of_manufacture_cost", "вывести среднее значение поля manufactureCost для всех элементов коллекции", 0, new NoArgumentsValidator());
        this.collectionManager = collectionManager;
    }


    @Override
    public ExecutionResponse innerExecute(String arguments, User user) {
        if (collectionManager.getCollection().isEmpty()) return new ExecutionResponse<>(true, new AnswerString("Коллекция пуста! Среднее значение поля manufactureCost для всех элементов коллекции: 0"));

        double sum = 0;
        for (var product : collectionManager.getCollection().values()) {
            sum += product.getManufactureCost();
        }

        return new ExecutionResponse<>(true, new AnswerString("Среднее значение поля manufactureCost для всех элементов коллекции: " + sum / collectionManager.getCollection().size()));
    }
}
