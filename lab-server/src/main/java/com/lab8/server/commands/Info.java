package com.lab8.server.commands;



import com.lab8.common.util.User;
import com.lab8.common.util.executions.AnswerString;
import com.lab8.common.util.executions.ExecutionResponse;
import com.lab8.common.util.executions.ListAnswer;
import com.lab8.common.validators.NoArgumentsValidator;
import com.lab8.server.managers.CollectionManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Команда 'info'. Выводит информацию о коллекции.
 */
public class Info extends Command<NoArgumentsValidator> {
    private final CollectionManager collectionManager;

    public Info(CollectionManager collectionManager) {
        super("info", "вывести информацию о коллекции", 0, new NoArgumentsValidator());
        this.collectionManager = collectionManager;
    }



    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    @Override
    public ExecutionResponse innerExecute(String arguments, User user) {
        LocalDateTime lastInitTime = collectionManager.getLastInitTime();
        String lastInitTimeString = (lastInitTime == null) ? "в данной сессии инициализации еще не происходило" :
                lastInitTime.toLocalDate().toString() + " " + lastInitTime.toLocalTime().toString();

        LocalDateTime lastSaveTime = collectionManager.getLastSaveTime();
        String lastSaveTimeString = (lastSaveTime == null) ? "в данной сессии сохранения еще не происходило" :
                lastSaveTime.toLocalDate().toString() + " " + lastSaveTime.toLocalTime().toString();

        List<String> list = new ArrayList<>();
        list.add(collectionManager.getCollection().getClass().toString());
        list.add(String.valueOf(collectionManager.getCollection().size()));
        list.add(lastSaveTimeString);
        list.add(lastInitTimeString);
        return new ExecutionResponse<>(new ListAnswer(list));
    }
}