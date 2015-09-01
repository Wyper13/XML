/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml.dom;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 *
 * @author Thibault
 */
public class xPath
{
    private XPath xpath;
    private Document doc;
    
    private XPathExpression expression;
    
    public xPath(Document doc)
    {
        XPathFactory xpf = XPathFactory.newInstance();
        xpath = xpf.newXPath();
        this.doc = doc;
        
        // tous les films qui n'ont pas un vote_count OU un vote_average (XOR)
        String EXPRmovie = "//mv:movie[//mv:vote_count and not(//mv:vote_average) or //mv:vote_average and not(//mv:vote_count)]//mv:_id";
        xpath.setNamespaceContext(new NamespaceContext() {
            
            @Override
            public String getNamespaceURI(String prefix) {
                return prefix.equals("mv") ? "http://moviesRT" : null;
            }
            
            @Override
            public String getPrefix(String namespaceURI) {
                return null;
            }
            
            @Override
            public Iterator getPrefixes(String namespaceURI) {
                return null;
            }
        });
        try
        {
            expression = (XPathExpression) xpath.compile(EXPRmovie);
        }
        catch (XPathExpressionException ex)
        {
            Logger.getLogger(xPath.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void validation()
    {
        try
        {
            NodeList idNodes = (NodeList) expression.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < idNodes.getLength(); i++)
                System.out.println("Erreur : élément manquant pour le film " + idNodes.item(i).getTextContent());
        }
        catch (XPathExpressionException ex)
        {
            Logger.getLogger(xPath.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
