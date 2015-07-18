 /*
  * To change this license header, choose License Headers in Project Properties.
  * To change this template file, choose Tools | Templates
  * and open the template in the editor.
  */
package xml;

// <editor-fold defaultstate="collapsed" desc="imports">
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
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
            System.out.println("Random : " + contentH.getRandom());
            System.out.println("nIds : " + contentH.getIds().size());
            System.out.println("nIncl : " + contentH.getIncl().size());
            System.out.println("nExcl : " + contentH.getExcl().size());
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
    }
    
}
