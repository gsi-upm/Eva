package com.google.glassware.mongodb.converter;


import org.bson.types.ObjectId;

import com.google.glassware.mongodb.model.User;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * Created by JesúsManuel on 14/04/2015.
 */
public class UserConverter {

    // convert User Object to MongoDB DBObject
    // take special note of converting id String to ObjectId
    public static DBObject toDBObject(User p) {

        BasicDBObjectBuilder builder = BasicDBObjectBuilder.start()
                .append("name", p.getName()).append("email", p.getEmail()).append("userId", p.getUserId());
        if (p.getId() != null)
            builder = builder.append("_id", new ObjectId(p.getId()));
        return builder.get();
    }

    // convert DBObject Object to User
    // take special note of converting ObjectId to String
    public static User toUser(DBObject doc) {
        User p = new User();
        p.setName((String) doc.get("name"));
        p.setEmail((String) doc.get("email"));
        p.setUserId((String) doc.get("userId"));
        ObjectId id = (ObjectId) doc.get("_id");
        p.setId(id.toString());
        return p;

    }

}
