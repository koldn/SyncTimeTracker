package ru.dkolmogortsev.task;

import java.util.UUID;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import javax.annotation.Nullable;

/**
 * Created by dkolmogortsev on 2/11/17.
 */
@Indexed
public class Task {
    public static final String NO_PROJECT = "Without project";
    @Field(store = Store.YES, analyze = Analyze.NO, indexNullAs = Field.DEFAULT_NULL_TOKEN)
    private String description;

    public String getDescription() {
        return description;
    }

    @Nullable
    public String getTaskName() {
        return taskName;
    }

    @Nullable
    @Field(store = Store.YES, analyze = Analyze.NO, indexNullAs = Field.DEFAULT_NULL_TOKEN)
    private String taskName;

    public String getUUID()
    {
        return UUID;
    }

    private final String UUID;

    public Task(String description, String taskName) {
        this.description = description;
        this.taskName = taskName;
        this.UUID = java.util.UUID.randomUUID().toString();
    }

    @Override
    public String toString() {
        return String.format("Task [d:%s]", description);
    }
}
