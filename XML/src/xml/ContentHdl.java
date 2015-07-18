/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Thibault
 */
public class ContentHdl extends DefaultHandler
{
    private final StringBuilder acc;
    private final PrintStream out;

    public ContentHdl(PrintStream out) {
        acc = new StringBuilder();
        this.out = out;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException {
        out.printf("startElement. qName:%s\n", qName);
        for (int i = 0; i < atts.getLength(); i++) {
            out.printf("    uri:%s, localName:%s, qName:%s = '%s'\n",
                    atts.getURI(i), atts.getLocalName(i),
                    atts.getQName(i), atts.getValue(i));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        out.printf("endElement. qName:%s\n", qName);
        //out.printf("Characters accumulated: %s\n", acc.toString());
        acc.setLength(0);
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        acc.append(ch, start, length);
        out.printf("characters. [%s]\n", new String(ch, start, length));
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length)
            throws SAXException {
        String s = new String(ch, start, length);
        List<String> codePoints = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            int codePoint = s.codePointAt(i);
            codePoints.add(String.format("0x%X", codePoint));
        }
        //out.printf("ignorableWhitespace. [%s]\n", codePoints.stream().collect(Collectors.joining(", ")));
    }
}
