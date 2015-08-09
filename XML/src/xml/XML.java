 /*
  * To change this license header, choose License Headers in Project Properties.
  * To change this template file, choose Tools | Templates
  * and open the template in the editor.
  */
package xml;

// <editor-fold defaultstate="collapsed" desc="imports">
import com.mongodb.DBCursor;
import java.io.File;
import xml.sax.ErrorHdl;
import xml.sax.ContentHdl;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import xml.dom.DomBuilder;
import xml.utils.Mongo;
// </editor-fold>

/**
 *
 * @author Thibault
 */
public class XML
{
    
    public static void main(String[] args) throws UnsupportedEncodingException
    {
        // <editor-fold defaultstate="collapsed" desc="args">
        ArgumentParser AP = ArgumentParsers.newArgumentParser("moviesRT");
        AP.addArgument("-c", "--config").setDefault("./config.xml").help("XML");
        AP.addArgument("-d", "--dom").setDefault("2").help("DOM lvl");
        AP.addArgument("-o", "--output").setDefault("./movies.xml").help("Output XML file");
        AP.addArgument("-v", "--validation").setConst("dtd").help("Validation dtd or xsd");
        
        Namespace ARGS = null;
        try
        {
            ARGS = AP.parseArgs(args);
        }
        catch (ArgumentParserException ex)
        {
            Logger.getLogger(XML.class.getName()).log(Level.SEVERE, null, ex);
        }
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="3. sax">
        String xml = ARGS.getString("config");
        SAXParserFactory saxPF = SAXParserFactory.newInstance();
        saxPF.setValidating(true);
        saxPF.setNamespaceAware(true);
        
        XMLReader parser = null;
        ContentHdl contentH = new ContentHdl();
        ErrorHdl errorH = new ErrorHdl();
        
        try
        {
            parser = saxPF.newSAXParser().getXMLReader();
            parser.setContentHandler(contentH);
            parser.setErrorHandler(errorH);
            parser.parse(xml);
            System.out.println("OK");
//            System.out.println("Random : " + contentH.getRandom());
//            System.out.println("nIds : " + contentH.getIds().size());
//            System.out.println("nIncl : " + contentH.getIncl().size());
//            System.out.println("nExcl : " + contentH.getExcl().size());
        }
        catch (ParserConfigurationException ex)
        {
            Logger.getLogger(XML.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SAXException ex)
        {
            System.out.println(ex.getMessage());
        }
        catch (IOException ex)
        {
            Logger.getLogger(XML.class.getName()).log(Level.SEVERE, null, ex);
        }
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="4. dom">
        int domlvl = Integer.parseInt(ARGS.getString("dom"));
        Mongo mongo = new Mongo();
        DBCursor cursor =  mongo.getMovies(contentH);
        
        DomBuilder domBuilder = new DomBuilder(domlvl);
        domBuilder.generateDoc(cursor);
        
        String outFile = ARGS.getString("output");
        
        domBuilder.domSerialize(outFile);
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="6. validation">
        String validation = ARGS.getString("validation");
        if(validation.equals("dtd"))
        {
            try
            {
                parser = saxPF.newSAXParser().getXMLReader();
                parser.setContentHandler(contentH);
                parser.setErrorHandler(errorH);
                parser.parse(outFile);
                System.out.println("DTD OK");
            }
            catch (ParserConfigurationException ex)
            {
                Logger.getLogger(XML.class.getName()).log(Level.SEVERE, null, ex);
            }
            catch (SAXException ex)
            {
                System.out.println(ex.getMessage());
            }
            catch (IOException ex)
            {
                Logger.getLogger(XML.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if(validation.equals("xsd"))
        {
            try
            {
                SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Schema shema = sf.newSchema(new File("./movies.xsd"));
                Validator v = shema.newValidator();
                v.setErrorHandler(errorH);
                
                v.validate(new DOMSource(domBuilder.getDoc()));
                
                System.out.println("XSD OK");
            }
            catch (SAXException ex)
            {
                System.out.println(ex.getMessage());
            }
            catch (IOException ex)
            {
                Logger.getLogger(XML.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // </editor-fold>
    }
    
}
