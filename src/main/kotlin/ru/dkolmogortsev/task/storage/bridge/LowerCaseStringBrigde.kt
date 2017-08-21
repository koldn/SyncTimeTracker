package ru.dkolmogortsev.task.storage.bridge

import org.apache.lucene.document.Document
import org.hibernate.search.bridge.FieldBridge
import org.hibernate.search.bridge.LuceneOptions
import org.hibernate.search.bridge.StringBridge
import org.slf4j.LoggerFactory

/**
 * Created by dkolmogortsev on 20.07.2017.
 */
class LowerCaseStringBrigde : StringBridge, FieldBridge
{
    init
    {
        LoggerFactory.getLogger(this::class.java).info("Initializing bridge")
    }

    override fun set(name: String?, value: Any?, document: Document?, luceneOptions: LuceneOptions?)
    {
        luceneOptions!!.addFieldToDocument(name, objectToString(value), document)
    }

    override fun objectToString(`object`: Any?): String
    {
        return `object`.toString().toLowerCase()
    }
}