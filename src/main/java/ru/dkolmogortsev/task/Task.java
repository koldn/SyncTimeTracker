package ru.dkolmogortsev.task;

import com.google.common.base.Objects;
import java.io.Serializable;
import javax.annotation.Nullable;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

/**
 * Created by dkolmogortsev on 2/11/17.
 */
@Indexed
public class Task implements Serializable
{
    public static final String NO_PROJECT = "Without project";
    private final long id;
    @Field(store = Store.YES, analyze = Analyze.NO, indexNullAs = Field.DEFAULT_NULL_TOKEN)
    private String description;
    @Nullable
    @Field(store = Store.YES, analyze = Analyze.NO, indexNullAs = Field.DEFAULT_NULL_TOKEN)
    private String taskName;

    public Task(String description, String taskName) {
        this.description = description;
        this.taskName = taskName;
        this.id = Objects.hashCode(taskName, description);
    }

    public String getDescription()
    {
        return description;
    }

    @Nullable
    public String getTaskName()
    {
        return taskName;
    }

    public long getId()
    {
        return id;
    }

    @Override
    public String toString() {
        return String.format("Task [d:%s]", description);
    }
}
