 /*
  * To change this license header, choose License Headers in Project Properties.
  * To change this template file, choose Tools | Templates
  * and open the template in the editor.
  */
package xml.utils;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Thibault
 */
public class BaseXUtils
{
    private BaseXClient session = null;
    
    public BaseXUtils()
    {
        try
        {
            session = new BaseXClient("localhost", 1984, "admin", "admin");
            session.execute("open movies");
        }
        catch (IOException ex)
        {
            Logger.getLogger(BaseXUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void insert(Node movie, String id)
    {
        try
        {
            Document doc = new DocumentImpl();
            doc.appendChild(doc.importNode(movie, true));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Source src = new DOMSource(doc);
            Result res = new StreamResult(baos);
            TransformerFactory.newInstance().newTransformer().transform(src, res);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            
            session.replace(id, bais);
        }
        catch (TransformerException | IOException ex)
        {
            Logger.getLogger(BaseXUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public BaseXClient getSession()
    {
        return session;
    }        
}
