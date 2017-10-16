package ru.dkolmogortsev.task.search

import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.LongPoint
import org.apache.lucene.document.TextField
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import ru.dkolmogortsev.task.Task
import javax.xml.soap.Text

/**
 * Created by dkolmogortsev on 10/16/17.
 */
class Indexer {
    fun index(task : Task){
            IndexWriter(DirectoryProvider.directory, IndexWriterConfig()).use { writer ->
                val doc = Document()
                doc.add(TextField("name",task.taskName, Field.Store.YES))
                doc.add(TextField("desc", task.description, Field.Store.YES))
                doc.add(LongPoint("id", task.id))
                writer.addDocument(doc)
                writer.commit()
            }
    }
}