package com.lab8.common.util.executions;

import com.lab8.common.util.ValidAnswer;
import java.io.Serial;
import java.io.Serializable;

public record AnswerString(String value) implements ValidAnswer<String>, Serializable {

    @Serial
    private static final long serialVersionUID = 14L;

    @Override
    public String getAnswer() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}