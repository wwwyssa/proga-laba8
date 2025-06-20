package com.lab8.server.commands;



import com.lab8.common.models.Product;
import com.lab8.common.util.User;
import com.lab8.common.util.executions.ExecutionResponse;
import com.lab8.common.util.executions.ListAnswer;
import com.lab8.common.validators.NoArgumentsValidator;
import com.lab8.server.managers.CollectionManager;

import java.util.ArrayList;
/**
     * Класс команды для вывода значений поля partNumber всех элементов в порядке возрастания.
     */
    public class PrintFieldAscendingPartNumber extends Command<NoArgumentsValidator> {
        private final CollectionManager collectionManager;

        /**
         * Конструктор класса PrintFieldAscendingPartNumber.
         * @param collectionManager Менеджер коллекции.
         */
        public PrintFieldAscendingPartNumber(CollectionManager collectionManager) {
            super("print_field_ascending_part_number", "вывести значения поля partNumber всех элементов в порядке возрастания", 0, new NoArgumentsValidator());
            this.collectionManager = collectionManager;
        }

        /**
         * Выполняет команду с указанными аргументами.
         *
         * @param arguments Аргументы команды.
         * @return Результат выполнения команды.
         */
        @Override
        public ExecutionResponse<?> innerExecute(String arguments, User user) {
            ArrayList<Product> tmpList = new ArrayList<>();
            for (var product : collectionManager.getCollection().values()) {
                tmpList.add(product);
            }
            tmpList.sort(Product::compareTo);
            return new ExecutionResponse<>(true, new ListAnswer(tmpList));
        }
    }