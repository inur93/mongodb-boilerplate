package com.vormadal.mongodb.options;

import com.mongodb.MongoClient;
import lombok.Getter;

/**
 * Created: 21-09-2018
 * author: Runi
 */
@Getter
public class DbOptions {

    private MongoClient mongoClient = null;
    private String database = "localhost";
    private String[] modelsPackages = null;

    public DbOptions mongoClient(MongoClient client){
        this.mongoClient = client;
        return this;
    }

    public DbOptions database(String database){
        this.database = database;
        return this;
    }

    public DbOptions modelsPackages(String... packages){
        this.modelsPackages = packages;
        return this;
    }

}
