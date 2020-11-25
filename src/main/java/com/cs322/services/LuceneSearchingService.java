package com.cs322.services;

import com.cs322.models.User;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

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
            User user = getUser("president");
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
            User build = new User.builder()
                    .email(doc.get("email"))
                    .username(doc.get("username"))
                    .position(doc.get("position"))
                    .department(doc.get("department"))
                    .location(doc.get("location"))
                    .firstName(doc.get("firstname"))
                    .lastName(doc.get("lastname"))
                    .phone(doc.get("phone"))
                    .photoUrl(doc.get("photoUrl")).build();
            System.out.println();
        } catch (IOException e) {
            log.error("");
            e.printStackTrace();
        }
        return null;
    }
}