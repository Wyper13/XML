 /*
  * To change this license header, choose License Headers in Project Properties.
  * To change this template file, choose Tools | Templates
  * and open the template in the editor.
  */
package xml.utils;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import xml.dom.RandomID;
import xml.sax.ContentHdl;

/**
 *
 * @author Thibault
 */
public class Mongo
{
    private MongoClient mongoClient;
    private DB db;
    private DBCollection coll;
    private DBCursor cursor;
    
    private static List<String> projBase = new ArrayList<>();
    
    static
    {
        projBase.add("_id");
        projBase.add("title");
        projBase.add("genres");
        projBase.add("overview");
        projBase.add("vote_average");
        projBase.add("vote_count");
        projBase.add("release_date");
        projBase.add("runtime");
        projBase.add("spoken_languages");
        projBase.add("status");
        projBase.add("tagline");
        projBase.add("actors");
        projBase.add("directors");
        projBase.add("production_companies");
        projBase.add("original_title");
        projBase.add("budget");
        projBase.add("revenue");
        projBase.add("production_countries");
        projBase.add("homepage");
        projBase.add("poster_path");
    }
    
    public Mongo()
    {
        try
        {
            mongoClient = new MongoClient("localhost");
            db = mongoClient.getDB("movies");
            coll = db.getCollection("movies");
        }
        catch (UnknownHostException ex)
        {
            Logger.getLogger(Mongo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public DBCursor getMovies(ContentHdl content)
    {
        RandomID random = new RandomID();
        // Remplissage avec les ids de la base
        DBObject proj = new BasicDBObject("_id", 1);
        DBCursor curs = coll.find(new BasicDBObject(), proj);
        for (DBObject id : curs)
        {
            random.insert(Integer.parseInt(id.get("_id").toString()));
        }
        
        Set<Integer> ids = content.getIds();
        // On enlève les ids demandés explicitement des aléatoires
        for (Integer id : ids)
        {
            random.remove(id);
        }
        // Ajout des ids aléatoires à ceux demandés explicitement
        for(int i = 0 ; i < content.getRandom() ; i++)
        {
            int id = random.getRandomElement();
            random.remove(id);
            ids.add(id);
        }
        // ids /s-forme de DBObject
        BasicDBObject idsQuery = new BasicDBObject();
        List<Integer> idsList = new ArrayList<Integer>(ids);
        idsQuery.put("_id", new BasicDBObject("$in", idsList));
        
        
        
        // Projections
        List<String> projections = new ArrayList<>();
        if(content.getExcl().isEmpty() && content.getIncl().isEmpty()) // 0 excl ET 0 incl
        {
            projections = projBase;
        }
        else if(content.getIncl().isEmpty() && !content.getExcl().isEmpty()) // 0 incl ET n excl
        {
            projections = projBase;
            projections.removeAll(content.getExcl());
        }
        else // n incl ET n excl
        {
            // Incl - excl
            Set<String> projDiff = content.getIncl();
            projDiff.removeAll(content.getExcl());
            
            // Sélection des éléments demandés, en éliminant les champs rejetés
            for(int i = 0 ; i < projBase.size() ; i++)
                if(projDiff.contains(projBase.get(i)))
                    projections.add(projBase.get(i));
        }
        // Requète mongo
        proj = new BasicDBObject();
        for (String p : projections)
            proj.put(p, "1");
        
        cursor = coll.find(idsQuery, proj);
        
//            System.out.println("elements in cursor : " + cursor.size());
//            while (cursor.hasNext())
//            {
//                System.out.println(cursor.next());
//            }
        
        return cursor;
    }
}
