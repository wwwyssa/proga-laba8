package com.lab8.client.managers;

public enum Commands {
    ADD("add", true),
    ADD_IF_MAX("add_if_max", true),
    CLEAR("clear", false),
    COUNT_BY_PRICE("count_by_price", false),
    EXECUTE_SCRIPT("execute_script", false),
    EXIT("exit", false),
    HELP("help", false),
    INFO("info", false),
    REMOVE_BY_ID("remove_by_id", false),
    REMOVE_GREATER("remove_greater", true),
    SHOW("show", false),
    UPDATE("update", false);

    private final String command;
    private final boolean description;

    Commands(String command, boolean description) {
        this.command = command;
        this.description = description;
    }

    public String getCommand() {
        return command;
    }

    public boolean getDescription() {
        return description;
    }
}
