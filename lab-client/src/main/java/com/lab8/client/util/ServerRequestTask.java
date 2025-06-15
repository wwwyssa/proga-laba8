package com.lab8.client.util;

import com.lab8.client.managers.ConnectionManager;
import com.lab8.common.util.Request;
import com.lab8.common.util.Response;
import javafx.concurrent.Task;

public class ServerRequestTask extends Task<Response> {
    private final Request request;

    public ServerRequestTask(Request request) {
        this.request = request;
    }

    @Override
    protected Response call() throws Exception {
        ConnectionManager.getInstance().send(request);
        return ConnectionManager.getInstance().receive();
    }
}
