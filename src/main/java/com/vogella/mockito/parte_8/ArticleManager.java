package com.vogella.mockito;

public class ArticleManager {
    private final User user;
    private final ArticleDatabase database;

    public ArticleManager(User user, ArticleDatabase database) {
        this.user = user;
        this.database = database;
    }

    public void initialize() {
        database.saveArticle("Welcome Article for " + user.getName());
    }
}