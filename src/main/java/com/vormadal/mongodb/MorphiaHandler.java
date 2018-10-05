package com.vormadal.mongodb;

import com.mongodb.MongoClient;
import com.vormadal.mongodb.exceptions.MorphiaException;
import com.vormadal.mongodb.options.DaoOptions;
import com.vormadal.mongodb.options.DbOptions;
import lombok.extern.slf4j.Slf4j;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import static java.util.Arrays.asList;


/**
 * Created: 21-09-2018
 * author: Runi
 */
@Slf4j
public class MorphiaHandler implements DbProvider {

    private Datastore datastore;

    public MorphiaHandler(SetupHandler setupHandler, DbOptions options) throws MorphiaException {
        if (options.getDbServers() == null) {
            throw new MorphiaException("Environment variable: MONGODB_URI not set - contact Sysadmin");
        }

        log.info("Connecting to mongo servers...");
        MongoClient client = options.getDbServers().length > 1 ?
                new MongoClient(options.getDbServers()[0]) :
                new MongoClient(asList(options.getDbServers()));
        log.info("Mapping packages... ");
        //Map DTOS
        Morphia morphia = new Morphia();
        if(options.getModelsPackages() != null){
            for(String pack : options.getModelsPackages()){
                morphia.mapPackage(pack);
            }
        }

        log.info("Creating datastore for database: " + options.getDatabase());
        datastore = morphia.createDatastore(client, options.getDatabase());
        datastore.ensureIndexes();
        setupHandler.onSetup(this);
    }

    public Datastore getDatastore() {
        return datastore;
    }


    @Override
    public MorphiaHandler getDb() {
        return this;
    }

    @Override
    public DaoOptions getDaoOptions() {
        return new DaoOptions();
    }
}
