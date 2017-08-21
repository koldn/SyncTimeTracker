package ru.dkolmogortsev.task.storage

/**
 * Created by dkolmogortsev on 20.07.2017.
 */
interface Storage<T>
{
    fun save(toSave: T)
    fun get(id: Long): T
}