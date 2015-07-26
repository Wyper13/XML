/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml.sax;

import java.util.HashSet;
import java.util.Set;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Thibault
 */
public class ContentHdl extends DefaultHandler
{
    private String str;
    private String element;
    private boolean include;
    
    private Integer random;
    private Set<Integer> ids;
    private Set<String> incl;
    private Set<String> excl;
    
    public ContentHdl()
    {
        str = new String();
        include = true;
        
        random = 0;
        ids = new HashSet<>();
        incl = new HashSet<>();
        excl = new HashSet<>();
    }
    
    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException
    {
        //System.out.println("StartElement - " + qName);
        element = localName;
        switch(element)
        {
            case "include":
                include = true;
                break;
            case "exclude":
                include = false;
                break;
            default:
                break;
        }
    }
    
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException
    {
        if(!str.equals(""))
        {
            //System.out.println(str);
            switch(element)
            {
                case "id":
                    ids.add(Integer.parseInt(str));
                    break;
                case "random":
                    random += Integer.parseInt(str);
                    break;
                case "prop":
                    if(include) // include
                    {
                        if(!str.isEmpty())
                            incl.add(str);
                    }
                    else // exclude
                    {
                        if(!str.isEmpty())
                            excl.add(str);
                    }
                    break;
                default:
                    break;
            }
        }
        //System.out.println("EndElement - " + qName);
        str = new String();
    }
    
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException
    {
        str += new String(ch, start, length);
    }
    
    @Override
    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {}
    
    public Integer getRandom()
    {
        return random;
    }
    
    public Set<Integer> getIds()
    {
        return ids;
    }
    
    public Set<String> getIncl()
    {
        return incl;
    }
    
    public Set<String> getExcl()
    {
        return excl;
    }
    
    
}
