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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import xml.dom.DomBuilder;
import xml.dom.xPath;
import xml.utils.BaseXUtils;
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
        long begin, end;
        
        // <editor-fold defaultstate="collapsed" desc="args">
        ArgumentParser AP = ArgumentParsers.newArgumentParser("moviesRT");
        AP.addArgument("-c", "--config").setDefault("./config.xml").help("Config XML");
        AP.addArgument("-d", "--dom").setDefault("2").help("DOM lvl");
        AP.addArgument("-o", "--output").setDefault("./movies.xml").help("Output XML file");
        AP.addArgument("-v", "--validation").setDefault("dtd").help("Validation dtd or xsd");
        AP.addArgument("-xs", "--xslt").setDefault("./movies_xslt.xml").help("Output XSLT file");
        AP.addArgument("-xh", "--xhtml").setDefault("./movies.html").help("Output XHTML file");
        AP.addArgument("-b", "--basex").setDefault("false").help("Insert into BaseX");
        
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
            
            System.out.printf("SAX Parsing config : ");
            begin = System.nanoTime();
            parser.parse(xml);
            end = System.nanoTime() - begin;
            System.out.println(TimeUnit.NANOSECONDS.toMillis(end) + "ms");
//            System.out.println("Random : " + contentH.getRandom());
//            System.out.println("nIds : " + contentH.getIds().size());
//            System.out.println("nIncl : " + contentH.getIncl().size());
//            System.out.println("nExcl : " + contentH.getExcl().size());
        }
        catch (ParserConfigurationException | IOException ex)
        {
            Logger.getLogger(XML.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SAXException ex)
        {
            System.out.println(ex.getMessage());
        }
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="4. dom">
        int domlvl = Integer.parseInt(ARGS.getString("dom"));
        Mongo mongo = new Mongo();
        DBCursor cursor =  mongo.getMovies(contentH);
        
        // <editor-fold defaultstate="collapsed" desc="BaseX">
        String bx = ARGS.getString("basex");
        BaseXUtils basex;
        if(bx.equalsIgnoreCase("true"))        
            basex = new BaseXUtils();            
        else
            basex = null;
               
        // </editor-fold>
        
        DomBuilder domBuilder = new DomBuilder(domlvl, basex);
        
        System.out.printf("DOM build document : ");
        begin = System.nanoTime();
        domBuilder.generateDoc(cursor);
        end = System.nanoTime() - begin;
        System.out.println(TimeUnit.NANOSECONDS.toMillis(end) + "ms");
        
        String outFile = ARGS.getString("output");
        
        System.out.printf("DOM serialisation : ");
        begin = System.nanoTime();
        domBuilder.domSerialize(outFile);
        end = System.nanoTime() - begin;
        System.out.println(TimeUnit.NANOSECONDS.toMillis(end) + "ms");
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
                
                System.out.printf("DTD validation : ");
                begin = System.nanoTime();
                parser.parse(outFile);
                end = System.nanoTime() - begin;
                System.out.println(TimeUnit.NANOSECONDS.toMillis(end) + "ms");
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
                
                System.out.printf("XSD validation : ");
                begin = System.nanoTime();
                v.validate(new DOMSource(domBuilder.getDoc()));
                end = System.nanoTime() - begin;
                System.out.println(TimeUnit.NANOSECONDS.toMillis(end) + "ms");
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
        
        // <editor-fold defaultstate="collapsed" desc="7. xPath">
        xPath xPathValid = new xPath(domBuilder.getDoc());
        
        System.out.printf("XPATH validation : ");
        begin = System.nanoTime();
        xPathValid.validation();
        end = System.nanoTime() - begin;
        System.out.println(TimeUnit.NANOSECONDS.toMillis(end) + "ms");
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="8. xslt">
        String XMLout = ARGS.getString("xslt");
        TransformerFactory tf = TransformerFactory.newInstance();
        File xslt = new File("./transformation1.xslt");
        Transformer transformer = null;
        File xsltOUT = new File(XMLout);
        try
        {
            transformer = tf.newTransformer(new StreamSource(xslt));
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            
            System.out.printf("XSLT : ");
            begin = System.nanoTime();
            transformer.transform(new DOMSource(domBuilder.getDoc()), new StreamResult(xsltOUT));
            end = System.nanoTime() - begin;
            System.out.println(TimeUnit.NANOSECONDS.toMillis(end) + "ms");
        }
        catch (TransformerException ex)
        {
            Logger.getLogger(XML.class.getName()).log(Level.SEVERE, null, ex);
        }
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="10. xhtml">
        String XHTMLout = ARGS.getString("xhtml");
        File xhtml = new File("./transformation2.xslt");
        transformer = null;
        File xhtmlOUT = new File(XHTMLout);
        try
        {
            transformer = tf.newTransformer(new StreamSource(xhtml));
            
            System.out.printf("XHTML generation : ");
            begin = System.nanoTime();
            transformer.transform(new DOMSource(domBuilder.getDoc()), new StreamResult(xhtmlOUT));
            end = System.nanoTime() - begin;
            System.out.println(TimeUnit.NANOSECONDS.toMillis(end) + "ms");
        }
        catch (TransformerException ex)
        {
            Logger.getLogger(XML.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Validation
        try
        {
            parser = saxPF.newSAXParser().getXMLReader();
            parser.setContentHandler(new DefaultHandler());
            parser.setErrorHandler(errorH);
            
            System.out.printf("XHTML validation : ");
            begin = System.nanoTime();
            parser.parse(XHTMLout);
            end = System.nanoTime() - begin;
            System.out.println(TimeUnit.NANOSECONDS.toMillis(end) + "ms");
        }
        catch (ParserConfigurationException | IOException ex)
        {
            Logger.getLogger(XML.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SAXException ex)
        {
            System.out.println(ex.getMessage());
            end = System.nanoTime() - begin;
            System.out.println("XHTML validation : " + TimeUnit.NANOSECONDS.toMillis(end) + "ms");
        }
        // </editor-fold>
    }
    
}
