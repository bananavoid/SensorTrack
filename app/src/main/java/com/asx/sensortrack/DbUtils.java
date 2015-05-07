package com.asx.sensortrack;

import android.database.MatrixCursor;

import java.util.Collections;
import java.util.List;

public final class DbUtils {
//    public static MatrixCursor getArticlesCursor() {
//        //List<Article> articles = Article.listAll(Article.class); //use for get just all articles
//        List<Article> articles = getArticlesSortedByChangedDate(); //use for get sorted articles
//        Collections.reverse(articles);
//        String[] c_columns = new String[] {
//                "_id",
//                "image",
//                "title",
//                "ingress",
//                "date"
//        };
//        MatrixCursor matrixCursor = new MatrixCursor(c_columns, 0);
//        for (Article article : articles) {
//            matrixCursor.addRow(new String[]{
//                    article.getId().toString(),
//                    article.getImage(),
//                    article.getTitle(),
//                    article.getIngress(),
//                    article.getDate()
//            });
//        }
//
//        return matrixCursor;
//    }
//
//    public static List<Article> getArticlesSortedByChangedDate() {
//        return Article.find(Article.class, null, null, null, "changed", null);
//    }
//
//    public static Article getArticleById(long id) {
//        return Article.findById(Article.class, id);
//    }
}
