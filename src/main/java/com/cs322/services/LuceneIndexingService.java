package com.cs322.services;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

@Service("indexing")
public class LuceneIndexingService {
    private final Logger log = getLogger(this.getClass());

    private List<String> fields;

    @Value("${indexing.fields}")
    private void setFields(List<String> f) {
        this.fields = f;
    }

    @Value("${indexing.path}")
    public void setIndexPath(String indexPath) {
        this.indexPath = indexPath;
    }

    private String indexPath;

    private static IndexWriter indexWriter = null;

    @PostConstruct
    private void setUp() {
        try {
            JsonArray jsonObjects = parseJsonFile();
            openIndex();
            addDocuments(jsonObjects);
            finish();
        } catch (IOException e) {
            log.error("LuceneIndexService() error " + e.getMessage());
            e.printStackTrace();
        }
    }

    private JsonArray parseJsonFile() throws IOException {
        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(
                new InputStreamReader(
                        new ClassPathResource("data/users.json").getInputStream()));
        log.info("pareJsonfile");
        return gson.fromJson(jsonReader, JsonArray.class);
    }

    private void openIndex() {
        try {
            Directory dir = FSDirectory.open(new File(indexPath));
            Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_48, analyzer);
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            indexWriter = new IndexWriter(dir, iwc);
            log.info("openIndex() index open");
        } catch (Exception e) {
            log.error("Error opening the index. " + e.getMessage());
        }
    }

    private void addDocuments(JsonArray jsonObjects) {
        Document doc = new Document();
        jsonObjects.forEach(j -> {
            JsonObject object = (JsonObject) j;
            Set<String> fields = object.keySet();
            fields.stream()
                    .filter(s -> this.fields.contains(s))
                    .forEach(str -> {
                        StringField field = new StringField(str,
                                object.get(str).getAsString(),
                                Field.Store.YES);
                        doc.add(field);
                    });
            log.info("addDocuments() field added to doc " + j);
        });
        try {
            indexWriter.addDocument(doc);
        } catch (IOException ex) {
            log.error("addDocuments() error adding documents to the index " + ex.getMessage());
            System.err.println();
        }
    }

    public void finish() {
        try {
            indexWriter.commit();
            indexWriter.close();
            log.info("finish() index writer commit() and close()");
        } catch (IOException ex) {
            System.err.println("We had a problem closing the index: " + ex.getMessage());
        }
    }
}