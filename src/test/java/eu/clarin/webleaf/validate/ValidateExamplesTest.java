/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.clarin.webleaf.validate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author yanapanchenko
 */
public class ValidateExamplesTest {

    private static final String[] INPUT_FILES_FOR_READ = new String[]{
        "/corpus.xml",
        "/corpus_extdata.xml",
        "/tcf04-karin-wl.xml",
        "/lex04-karin-wl.xml"
    };
    public static final String WebLEAF_SCHEMA_LOCATION = "/d-spin-local_0_4.rnc";
    //public static final String TCF04_SCHEMA_LOCATION =
    //        "http://clarin-d.de/images/weblicht-tutorials/resources/tcf-04/schemas/latest/d-spin_0_4.rnc";
    static {
        System.setProperty(SchemaFactory.class.getName() + ":" + XMLConstants.RELAXNG_NS_URI,
                "com.thaiopensource.relaxng.jaxp.CompactSyntaxSchemaFactory");
    }
    
    @Test
    public void testRead() throws Exception {
        for (String inputFile : INPUT_FILES_FOR_READ) {
            testRead(inputFile);
        }
    }
    
    
    public void testRead(String inputFile) throws Exception {
        
        InputStream is = this.getClass().getResourceAsStream(inputFile);
        System.setProperty(SchemaFactory.class.getName() + ":" + XMLConstants.RELAXNG_NS_URI,
                "com.thaiopensource.relaxng.jaxp.CompactSyntaxSchemaFactory");
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.RELAXNG_NS_URI);
        Validator validator = null;
        try {
            //URL tcf04SchemaLocation = new URL(TCF04_SCHEMA_LOCATION);
            URL tcf04SchemaLocation = this.getClass().getResource(WebLEAF_SCHEMA_LOCATION);
            Schema schemaTcf04 = factory.newSchema(tcf04SchemaLocation);
            validator = schemaTcf04.newValidator();
            validator.setErrorHandler(new ErrorLocationErrorHandler());
        } catch (Exception e) {
            // should not happen (schema should be correct and found on server);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            System.out.println(e.getMessage());
        }

        // Validate the file and outout the validation result message
        String message;
        boolean valid = false;
        try {
            //validator.validate(new StreamSource(new ByteArrayInputStream("blah".getBytes())));
            validator.validate(new StreamSource(is));
            message = inputFile + " content is valid TCF0.4";
            valid = true;
        } catch (SAXException ex) {
           //Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            message = inputFile + " content is not valid TCF0.4 because:\n" + ex.getMessage();
        } catch (IOException e) {
            // should not happen
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            message = e.getMessage();
        } finally {
            if (is != null) {
                is.close();
            }
        }
        System.out.println(message);
        Assert.assertTrue(valid);
    }


        private static class ErrorLocationErrorHandler implements ErrorHandler {

        public ErrorLocationErrorHandler() {
        }

        @Override
        public void warning(SAXParseException exception) throws SAXException {
            //throw new SAXException(exception.getMessage() + " line " + exception.getLineNumber() + " column " + exception.getColumnNumber());
        }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            throw new SAXException(" line " + exception.getLineNumber() + " column " + exception.getColumnNumber() + " - " + exception.getMessage());
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            throw new SAXException(" line " + exception.getLineNumber() + " column " + exception.getColumnNumber() + " - " + exception.getMessage());
        }

    }
}

