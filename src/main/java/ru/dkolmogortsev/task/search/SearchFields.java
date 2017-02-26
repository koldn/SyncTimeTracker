package ru.dkolmogortsev.task.search;

import ru.dkolmogortsev.task.Task;

import java.util.function.Function;

/**
 * Created by dkolmogortsev on 2/18/17.
 */
public enum SearchFields {
    TASKNAME(Task::getTaskName), DESCRIPTION(Task::getDescription);

    public Function<Task, String> getMapper() {
        return mapper;
    }

    private Function<Task, String > mapper;

    SearchFields(Function<Task,String> toStringMapper){
        this.mapper = toStringMapper;
    }

}
