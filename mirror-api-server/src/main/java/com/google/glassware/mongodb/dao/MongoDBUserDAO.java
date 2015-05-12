package com.google.glassware.mongodb.dao;


import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.google.glassware.mongodb.converter.UserConverter;
import com.google.glassware.mongodb.model.User;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

/**
 * Created by JesúsManuel on 14/04/2015.
 */
public class MongoDBUserDAO {

    private DBCollection col;
    private MongoClient mongo;

    public MongoDBUserDAO() {
        try {
            MongoClient mongoClient = new MongoClient("localhost", 27017);
            mongo = mongoClient;
            DB db = mongoClient.getDB("glassware");
            DBCollection collection = db.getCollection("Users");
            this.col = collection;
        }catch (Exception e){

        }

    }

    public MongoClient getClient(){
        return this.mongo;
    }

    public User createUser(User p) {
        DBObject doc = UserConverter.toDBObject(p);
        this.col.insert(doc);
        ObjectId id = (ObjectId) doc.get("_id");
        p.setId(id.toString());
        return p;
    }

    public void updatePerson(User p) {
        DBObject query = BasicDBObjectBuilder.start()
                .append("_id", new ObjectId(p.getId())).get();
        this.col.update(query, UserConverter.toDBObject(p));
    }

    public List<User> readAllPerson() {
        List<User> data = new ArrayList<User>();
        DBCursor cursor = col.find();
        while (cursor.hasNext()) {
            DBObject doc = cursor.next();
            User p = UserConverter.toUser(doc);
            data.add(p);
        }
        return data;
    }

    public void deleteUser(User p) {
        DBObject query = BasicDBObjectBuilder.start()
                .append("_id", new ObjectId(p.getId())).get();
        this.col.remove(query);
    }

    public User readUser(User p) {
        DBObject query = BasicDBObjectBuilder.start()
                .append("_id", new ObjectId(p.getId())).get();
        DBObject data = this.col.findOne(query);
        return UserConverter.toUser(data);
    }

}
