package com.lab8.client.managers;

import com.lab8.client.util.Console;
import com.lab8.client.util.DefaultConsole;
import com.lab8.client.util.ElementValidator;
import com.lab8.client.util.FileConsole;
import com.lab8.common.models.Product;
import com.lab8.common.util.Pair;
import com.lab8.common.util.Request;
import com.lab8.common.util.Response;
import com.lab8.common.util.User;
import com.lab8.common.util.executions.AnswerString;
import com.lab8.common.util.executions.ExecutionResponse;
import com.lab8.common.validators.ArgumentValidator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class ExecuteScript {
    static Console console = new DefaultConsole();
    static ConnectionManager networkManager = ConnectionManager.getInstance();
    static int scriptStackCounter=0;

    Commands commands;

    public static ExecutionResponse runScript(String fileName, User user) {
        try {
            scriptStackCounter++;
            if (scriptStackCounter > 100) {
                scriptStackCounter--;
                return new ExecutionResponse(false, new AnswerString("Превышена максимальная глубина рекурсии!"));
            }
            if (fileName.isEmpty()) {
                scriptStackCounter--;
                return new ExecutionResponse(false, new AnswerString( "У команды execute_script должен быть ровно один аргумент!\nПример корректного ввода: execute_script file_name"));
            }
            console.println("Запуск скрипта '" + fileName + "'");
            try  {
                File file = new File(fileName);
                Console FileConsole = new FileConsole(file);
                Scanner scanner = new Scanner(file);
                while (scriptStackCounter > 0) {
                    String line = scanner.nextLine();
                    if (!scanner.hasNextLine()) break;
                    if (!line.equals("exit")) {

                        Request request = prepareRequest(FileConsole, line, user);
                        if (request == null) {
                            return new ExecutionResponse(false, new AnswerString( "Выполнение скрипта остановлено"));
                        }
                        networkManager.send(request);
                        Response response = networkManager.receive();
                        ExecutionResponse commandStatus = response.getExecutionStatus();

                        if (response.getExecutionStatus().getExitCode()) {
                            console.println(commandStatus.getAnswer().getAnswer());
                        } else {
                            if (!commandStatus.getAnswer().equals("Выполнение скрипта приостановлено.")) {
                                console.printError(commandStatus.getAnswer().getAnswer());
                            }
                            return new ExecutionResponse(false, new AnswerString("Выполнение скрипта приостановлено."));
                        }
                    } else {
                        scriptStackCounter--;
                        return new ExecutionResponse(true, new AnswerString("Скрипт успешно выполнен."));
                    }
                }
            } catch (FileNotFoundException e) {
                return new ExecutionResponse(false, new AnswerString("Не удаётся найти файл скрипта!"));
            } catch (IllegalArgumentException e) {
                return new ExecutionResponse(false,new AnswerString("Произошла ошибка при чтении данных из файла скрипта!"));
            } catch (Exception e) {
                return new ExecutionResponse(false,new AnswerString( ""));
            }
            return new ExecutionResponse(true, new AnswerString(""));
        } catch (Exception e) {
            return new ExecutionResponse(false, new AnswerString( "Произошла ошибка при запуске скрипта!"));
        }
    }

    private static Request prepareRequest(Console console, String inputCommand, User user) {
        String[] commands = (inputCommand.trim() + " ").split(" ", 2);
        if (Commands.valueOf(commands[0].toUpperCase()).getDescription()) {
            return askingRequest(console, inputCommand, user); // Если команда требует построчного ввода
        } else if (commands[0].equals("execute_script")) {
            ExecutionResponse scriptStatus = runScript(commands[1].trim(), user);
            if (!scriptStatus.getExitCode() && (!scriptStatus.getAnswer().getAnswer().equals("Выполнение скрипта остановлено") || (scriptStackCounter == 99))) {
                console.printError(scriptStatus.getAnswer());
                return null;
            }
            return null;
        } else {
            return new Request(inputCommand, user);
        }
    }

    private static Request askingRequest(Console console, String inputCommand, User user) {
        ElementValidator elementValidator = new ElementValidator();
        Pair<ExecutionResponse, Product> validationStatusPair = elementValidator.validateAsking(console, Math.abs(new Random().nextLong()) + 1);
        if (!validationStatusPair.getFirst().getExitCode()) {
            console.printError(validationStatusPair.getFirst().getAnswer());
            return null;
        } else {
            return new Request(inputCommand, validationStatusPair.getSecond(), user);
        }
    }

}
