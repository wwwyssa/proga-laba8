package com.lab8.server.commands;


import com.lab8.common.models.Product;
import com.lab8.common.util.User;
import com.lab8.common.util.executions.AnswerString;
import com.lab8.common.util.executions.ExecutionResponse;
import com.lab8.common.validators.NoArgumentsValidator;
import com.lab8.server.managers.CollectionManager;
import com.lab8.server.util.AskingCommand;

public class RemoveGreater extends AskingCommand<NoArgumentsValidator> {
    private final CollectionManager collectionManager;

    public RemoveGreater(CollectionManager collectionManager) {
        super("remove_greater", "удалить из коллекции все элементы, превышающие заданный", new NoArgumentsValidator());
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse innerExecute(Product product, User user) {
        try{
            int count = 0;
            for(long key : collectionManager.getCollection().keySet()){
                if (collectionManager.getCollection().get(key).compareTo(product) > 0){
                    collectionManager.removeProduct(key, user);
                    count++;
                }
            }
            return new ExecutionResponse<>(new AnswerString("Продукты успешно удалены! Удалено " + count + " элементов"));
        } catch (Exception e){
            System.out.println(e);
            return new ExecutionResponse<>(false, new AnswerString("Ошибка ввода данных!"));
        }


    }

}
