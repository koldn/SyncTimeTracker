package ru.dkolmogortsev.task.search

import org.apache.lucene.store.Directory
import org.apache.lucene.store.MMapDirectory
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Created by dkolmogortsev on 10/16/17.
 */
object DirectoryProvider
{
    val directory : Directory
    init {
        directory = MMapDirectory(Paths.get(".sync_data/lucene"))
    }
}