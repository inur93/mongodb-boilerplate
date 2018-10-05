package com.vormadal.mongodb.options;

import com.mongodb.ServerAddress;
import lombok.Getter;

/**
 * Created: 21-09-2018
 * author: Runi
 */
@Getter
public class DbOptions {
    private ServerAddress[] dbServers = new ServerAddress[]{
            new ServerAddress("localhost", 27017)
    };
    private String database = "localhost";
    private String[] modelsPackages = null;

    public DbOptions dbServers(ServerAddress... addresses){
        this.dbServers = addresses;
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
