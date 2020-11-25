package com.cs322.services;

import com.cs322.models.User;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

@Service
@DependsOn("indexing")
public class LuceneSearchingService {
    private final Logger log = getLogger(this.getClass());


    private IndexSearcher indexSearcher;

    @Value("${indexing.path}")
    public void setIndexPath(String indexPath) {
        this.indexPath = indexPath;
    }

    private String indexPath;

    @PostConstruct
    public void setUp() {
        try {
            Directory indexDirectory = FSDirectory.open(new File(indexPath));
            IndexReader indexReader = DirectoryReader.open(indexDirectory);
            indexSearcher = new IndexSearcher(indexReader);
//            User user = getUserB("president");
        } catch (IOException e) {
            log.error("setUp() error " + e.getMessage());
        }
    }

    public User getUser(String username) {
        try {
            Term t = new Term("username", username);
            Query query = new TermQuery(t);
            TopDocs topDocs = indexSearcher.search(query, 10);
            Document doc = indexSearcher.doc(topDocs.scoreDocs[0].doc);
            System.out.println(doc.get("username"));
            return User.builder()
                    .id(UUID.fromString(doc.get("id")))
                    .email(doc.get("email"))
                    .username(doc.get("username"))
                    .password(doc.get("password"))
                    .position(doc.get("position"))
                    .department(doc.get("department"))
                    .location(doc.get("location"))
                    .firstName(doc.get("firstName"))
                    .lastName(doc.get("lastName"))
                    .phone(doc.get("phone"))
                    .photoUrl(doc.get("photoUrl")).build();
        } catch (IOException e) {
            log.error("");
            e.printStackTrace();
        }
        return null;
    }


    public User getUserB(String username) {
        try {

            BooleanQuery booleanQuery = new BooleanQuery();
            Query query1 = new TermQuery(new Term("firstName", "FNam"));
            booleanQuery.add(query1, BooleanClause.Occur.SHOULD);
            TopDocs topDocs = indexSearcher.search(booleanQuery, 10);
            Document doc = indexSearcher.doc(topDocs.scoreDocs[0].doc);
            System.out.println(doc.get("username"));
            return User.builder()
                    .id(UUID.fromString(doc.get("id")))
                    .email(doc.get("email"))
                    .username(doc.get("username"))
                    .password(doc.get("password"))
                    .position(doc.get("position"))
                    .department(doc.get("department"))
                    .location(doc.get("location"))
                    .firstName(doc.get("firstName"))
                    .lastName(doc.get("lastName"))
                    .phone(doc.get("phone"))
                    .photoUrl(doc.get("photoUrl")).build();
        } catch (IOException e) {
            log.error("");
            e.printStackTrace();
        }
        return null;
    }
}