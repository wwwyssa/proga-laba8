package com.lab8.server.commands;

import com.lab8.common.util.User;
import com.lab8.common.validators.NoArgumentsValidator;
import com.lab8.common.util.executions.AnswerString;
import com.lab8.common.util.executions.ExecutionResponse;
import com.lab8.server.managers.CommandManager;
import com.lab8.server.util.Executable;

import java.util.Map;

/**
 * Команда 'help'. Выводит справку по доступным командам
 **/
public class Help extends Command<NoArgumentsValidator>  {
    private final CommandManager commandManager;


    public Help(CommandManager commandManager) {
        super("help", "вывести справку по доступным командам", 0, new NoArgumentsValidator());
        this.commandManager = commandManager;
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    public ExecutionResponse innerExecute(String arguments, User user) {
        String result = "";
        for (Map.Entry<String, Executable> entry : commandManager.getCommands().entrySet()) {
            result += entry.getKey() + " -> " + entry.getValue() + "\n";
        }
        return new ExecutionResponse<>(new AnswerString(result));
    }
}