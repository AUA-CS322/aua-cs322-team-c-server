package am.aua.cs322.orgchart.services;

import am.aua.cs322.orgchart.models.User;
import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@DependsOn("indexing")
@Log4j2
public class LuceneSearchingService {
    private IndexSearcher indexSearcher;

    @Value("${indexing.path}")
    public void setIndexPath(String indexPath) {
        this.indexPath = indexPath;
    }

    private String indexPath;
    private Gson gson;


    @PostConstruct
    public void setUp() {
        try {
            gson = new Gson();
            Directory indexDirectory = FSDirectory.open(new File(indexPath));
            IndexReader indexReader = DirectoryReader.open(indexDirectory);
            indexSearcher = new IndexSearcher(indexReader);
//            User user = getUser("president");
        } catch (IOException e) {
            log.error("setUp() error " + e.getMessage());
        }
    }

    public User getUser(String username) {
        try {
            Query query =
                    new PrefixQuery(new Term("username", username));

            TopDocs topDocs = indexSearcher.search(query, 1);
            Document doc = indexSearcher.doc(topDocs.scoreDocs[0].doc);
            String s = doc.get("json");
            User user = gson.fromJson(s, User.class);
            log.debug("searchUsers() found user -> " + user);
            return user;
        } catch (IOException e) {
            log.error("searchUser() error " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<User> searchUsers(String strQuery) {

        List<User> userList = new ArrayList<>();
        try {
            TopDocs topDocs = search(strQuery);
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document doc = indexSearcher.doc(scoreDoc.doc);
                String s = doc.get("json");
                User user = gson.fromJson(s, User.class);
                log.debug("searchUsers() found user -> " + user);
                userList.add(user);
            }
            return userList;
        } catch (IOException e) {
            log.error("searchUser() error " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private TopDocs search(String strQuery) throws IOException {
        BooleanQuery booleanQuery = new BooleanQuery();
        Query query1 = new PrefixQuery(new Term("lastName", strQuery));
        Query query2 = new PrefixQuery(new Term("firstName", strQuery));
        Query query3 = new PrefixQuery(new Term("username", strQuery));
        booleanQuery.add(query1, BooleanClause.Occur.SHOULD);
        booleanQuery.add(query2, BooleanClause.Occur.SHOULD);
        booleanQuery.add(query3, BooleanClause.Occur.SHOULD);

        return indexSearcher.search(booleanQuery, 4);
    }
}