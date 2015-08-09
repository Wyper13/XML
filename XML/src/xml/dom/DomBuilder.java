 /*
  * To change this license header, choose License Headers in Project Properties.
  * To change this template file, choose Tools | Templates
  * and open the template in the editor.
  */
package xml.dom;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

/**
 *
 * @author Thibault
 */
public class DomBuilder
{
    private int lvl;
    private Document doc;
    
    public DomBuilder(int lvl)
    {
        this.lvl = lvl;
        if(lvl == 2)
        {
            try
            {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                doc = db.newDocument();
            }
            catch (ParserConfigurationException ex) {
                Logger.getLogger(DomBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        else if(lvl == 3)
        {
            try
            {
                DOMImplementationRegistry dir = DOMImplementationRegistry.newInstance();
                DOMImplementation di = dir.getDOMImplementation("XML 3.0 LS 3.0");
                File dtd = new File("movies.dtd");
                DocumentType dt = di.createDocumentType("movies", null, dtd.getAbsolutePath());
                doc = di.createDocument("http://moviesRT", "movies", dt);
            }
            catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException ex)
            {
                Logger.getLogger(DomBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void generateDoc(DBCursor cursor)
    {
        Element root = null;
        if(lvl == 2)
        {
            root = doc.createElementNS("http://moviesRT", "movies");
            doc.appendChild(root);
        }
        else if(lvl == 3)
        {
            root = doc.getDocumentElement();
        }
        
        for (DBObject DBmovie : cursor)
        {
            Node movie = generateMovieNode(DBmovie);
            root.appendChild(movie);
        }
    }
    
    // <editor-fold defaultstate="collapsed" desc="Main">
    private Node generateMovieNode(DBObject DBmovie)
    {
        Element movie = doc.createElementNS("http://moviesRT", "movie");
        
        if(DBmovie.containsField("_id"))
            textChild(DBmovie, movie, "_id");
        if(DBmovie.containsField("title"))
            textChild(DBmovie, movie, "title");
        if(DBmovie.containsField("genres"))
            genreChild(DBmovie, movie);
        if(DBmovie.containsField("overview"))
            textChild(DBmovie, movie, "overview");
        if(DBmovie.containsField("vote_average"))
            numberChild(DBmovie, movie, "vote_average");
        if(DBmovie.containsField("vote_count"))
            numberChild(DBmovie, movie, "vote_count");
        if(DBmovie.containsField("release_date"))
            textChild(DBmovie, movie, "release_date");
        if(DBmovie.containsField("runtime"))
            numberChild(DBmovie, movie, "runtime");
        if(DBmovie.containsField("spoken_languages"))
            languagesChild(DBmovie, movie);
        if(DBmovie.containsField("status"))
            textChild(DBmovie, movie, "status");
        if(DBmovie.containsField("tagline"))
            textChild(DBmovie, movie, "tagline");
        if(DBmovie.containsField("actors"))
            actorsChild(DBmovie, movie);
        if(DBmovie.containsField("directors"))
            directorsChild(DBmovie, movie);
        if(DBmovie.containsField("production_companies"))
            companiesChild(DBmovie, movie);
        if(DBmovie.containsField("original_title"))
            textChild(DBmovie, movie, "original_title");
        if(DBmovie.containsField("budget"))
            numberChild(DBmovie, movie, "budget");
        if(DBmovie.containsField("revenue"))
            numberChild(DBmovie, movie, "revenue");
        if(DBmovie.containsField("production_countries"))
            countriesChild(DBmovie, movie);
        if(DBmovie.containsField("homepage"))
            textChild(DBmovie, movie, "homepage");
        if(DBmovie.containsField("poster_path"))
            textChild(DBmovie, movie, "poster_path");
        
        return movie;
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Child">
    private void textChild(DBObject DBmovie, Element movie, String field)
    {
        if(DBmovie.get(field) != null)
            if(!DBmovie.get(field).equals(""))
            {
                Element elem = doc.createElementNS("http://moviesRT", field);
                elem.appendChild(doc.createTextNode(DBmovie.get(field).toString()));
                movie.appendChild(elem);
            }
    }
    /* http://stackoverflow.com/questions/7528544/error-validating-cdata-elements-with-dtd-xml
     * private void cdataChild(DBObject DBmovie, Element movie, String field)
     * {
     * if(DBmovie.get(field) != null)
     * if(!DBmovie.get(field).equals(""))
     * {
     * Element elem = doc.createElementNS("http://moviesRT", field);
     * elem.appendChild(doc.createCDATASection(DBmovie.get(field).toString()));
     * movie.appendChild(elem);
     * }
     * }
     */
    private void numberChild(DBObject DBmovie, Element movie, String field)
    {
        if(DBmovie.get(field) != null)
        {
            double number = Double.parseDouble(DBmovie.get(field).toString());
            if(number > 0)
            {
                Element elem = doc.createElementNS("http://moviesRT", field);
                elem.appendChild(doc.createTextNode(DBmovie.get(field).toString()));
                movie.appendChild(elem);
            }
        }
    }
    
    private void genreChild(DBObject DBmovie, Element movie)
    {
        Element subRoot = doc.createElementNS("http://moviesRT", "genres");
        BasicDBList genres = (BasicDBList) DBmovie.get("genres");
        if(!genres.isEmpty())
        {
            for (Object genre : genres)
            {
                Element g = doc.createElementNS("http://moviesRT", "genre");
                g.setAttribute("id", ((Map)genre).get("id").toString());
                g.setTextContent(((Map)genre).get("name").toString());
                
                subRoot.appendChild(g);
            }
            movie.appendChild(subRoot);
        }
    }
    
    private void languagesChild(DBObject DBmovie, Element movie)
    {
        Element subRoot = doc.createElementNS("http://moviesRT", "spoken_languages");
        BasicDBList langs = (BasicDBList) DBmovie.get("spoken_languages");
        if(!langs.isEmpty())
        {
            for (Object lang : langs)
            {
                Element g = doc.createElementNS("http://moviesRT", "spoken_language");
                g.setAttribute("iso", ((Map)lang).get("iso_639_1").toString());
                g.setTextContent(((Map)lang).get("name").toString());
                
                subRoot.appendChild(g);
            }
            movie.appendChild(subRoot);
        }
    }
    
    private void actorsChild(DBObject DBmovie, Element movie)
    {
        Element subRoot = doc.createElementNS("http://moviesRT", "actors");
        BasicDBList actors = (BasicDBList) DBmovie.get("actors");
        if(!actors.isEmpty())
        {
            for (Object act : actors)
            {
                Element g = doc.createElementNS("http://moviesRT", "actor");
                g.setAttribute("id", ((Map)act).get("id").toString());
                g.setAttribute("name", ((Map)act).get("name").toString());
                if(((Map)act).get("profile_path") != null)
                    g.setAttribute("profile_path", ((Map)act).get("profile_path").toString());
                
                Element actor = doc.createElementNS("http://moviesRT", "character");
                actor.setAttribute("cast_id", ((Map)act).get("cast_id").toString());
                actor.setTextContent(((Map)act).get("character").toString());
                
                g.appendChild(actor);
                
                subRoot.appendChild(g);
            }
            movie.appendChild(subRoot);
        }
    }
    
    private void directorsChild(DBObject DBmovie, Element movie)
    {
        Element subRoot = doc.createElementNS("http://moviesRT", "directors");
        BasicDBList directors = (BasicDBList) DBmovie.get("directors");
        if(!directors.isEmpty())
        {
            for (Object director : directors)
            {
                Element g = doc.createElementNS("http://moviesRT", "director");
                g.setAttribute("id", ((Map)director).get("id").toString());
                if(((Map)director).get("profile_path") != null)
                    g.setAttribute("profile_path", ((Map)director).get("profile_path").toString());
                g.setTextContent(((Map)director).get("name").toString());
                
                subRoot.appendChild(g);
            }
            movie.appendChild(subRoot);
        }
    }
    
    private void companiesChild(DBObject DBmovie, Element movie)
    {
        Element subRoot = doc.createElementNS("http://moviesRT", "production_companies");
        BasicDBList companies = (BasicDBList) DBmovie.get("production_companies");
        if(!companies.isEmpty())
        {
            for (Object company : companies)
            {
                Element g = doc.createElementNS("http://moviesRT", "production_company");
                g.setAttribute("id", ((Map)company).get("id").toString());
                g.setTextContent(((Map)company).get("name").toString());
                
                subRoot.appendChild(g);
            }
            movie.appendChild(subRoot);
        }
    }
    
    private void countriesChild(DBObject DBmovie, Element movie)
    {
        Element subRoot = doc.createElementNS("http://moviesRT", "production_countries");
        BasicDBList countries = (BasicDBList) DBmovie.get("production_countries");
        if(!countries.isEmpty())
        {
            for (Object country : countries)
            {
                Element g = doc.createElementNS("http://moviesRT", "production_country");
                g.setAttribute("iso", ((Map)country).get("iso_3166_1").toString());
                g.setTextContent(((Map)country).get("name").toString());
                
                subRoot.appendChild(g);
            }
            movie.appendChild(subRoot);
        }
    }
    //</editor-fold>
    
    public void domSerialize(String file)
    {
        if(lvl == 2)
        {
            try
            {
                File dtd = new File("movies.dtd");
                Transformer tf = TransformerFactory.newInstance().newTransformer();
                tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tf.setOutputProperty(OutputKeys.INDENT, "YES");
                tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
                tf.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dtd.getAbsolutePath());
                FileOutputStream fos = new FileOutputStream(new File(file));
                DOMSource src = new DOMSource(doc);
                StreamResult out = new StreamResult(fos);
                tf.transform(src, out);
            }
            catch (TransformerException | FileNotFoundException ex)
            {
                Logger.getLogger(DomBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(lvl == 3)
        {
            
            try {
                DOMImplementationRegistry dir = DOMImplementationRegistry.newInstance();
                DOMImplementationLS diLS = (DOMImplementationLS)dir.getDOMImplementation("XML 3.0 LS 3.0");
                
                LSSerializer LSser = diLS.createLSSerializer();
                DOMConfiguration conf = LSser.getDomConfig();
                conf.setParameter("format-pretty-print", true);
                
                LSOutput out = diLS.createLSOutput();
                out.setEncoding("UTF-8");
                out.setByteStream(new FileOutputStream(new File(file)));
                LSser.write(doc, out);
            }
            catch (ClassNotFoundException | InstantiationException | IllegalAccessException | ClassCastException | FileNotFoundException ex) {
                Logger.getLogger(DomBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
    public Document getDoc()
    {
        return doc;
    }
}
