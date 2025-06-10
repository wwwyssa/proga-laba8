package com.lab8.common.util.executions;

import com.lab8.common.util.ValidAnswer;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ListAnswer implements ValidAnswer<List<?>>, Serializable {
    @Serial
    private static final long serialVersionUID = 14L;
    private final List<?> value;

    public ListAnswer(List<?> value) {
        this.value = value;
    }

    @Override
    public List<?> getAnswer() {
        return value;
    }
}