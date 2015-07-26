/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xml.sax;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author Thibault
 */
public class ErrorHdl implements ErrorHandler {

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        System.err.println("### warning ###");
        throw exception;
    }

    @Override
    public void error(SAXParseException exception) throws SAXException {
        System.err.println("### error ###");        
        throw exception;
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        System.err.println("### fatalError ###");        
        throw exception;
    }
}
