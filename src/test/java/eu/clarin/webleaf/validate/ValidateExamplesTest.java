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
 * @author yanapanchenko and Fazleh
 */
public class ValidateExamplesTest {

    private static final String[] INPUT_FILES_FOR_READ_TCF_0_4 = new String[]{
        "/corpus.xml",
        "/corpus_extdata.xml",
        "/tcf04-karin-wl.xml",
        "/lex04-karin-wl.xml"
    };

    private static final String[] INPUT_FILES_FOR_READ_TCF_5 = new String[]{
        "/corpus5.xml",
        "/corpus5_extdata.xml",
        "/tcf5-karin-wl.xml",
        "/lex5-karin-wl.xml"
    };
    public static final String WebLEAF_SCHEMA_LOCATION_TCF_0_4 = "/d-spin-local_0_4.rnc";
    public static final String WebLEAF_SCHEMA_LOCATION_TCF_5 = "/d-spin-local_5.rnc";

    public static final String TCF_0_4 = "TCF0.4";
    public static final String TCF_5 = "TCF5";

    @Test
    public void testRead_WhenTcf_0_4() throws Exception {
        for (String inputFile : INPUT_FILES_FOR_READ_TCF_0_4) {
            testRead(inputFile, TCF_0_4, WebLEAF_SCHEMA_LOCATION_TCF_0_4);
        }
    }

    @Test
    public void testRead_WhenTcf_5() throws Exception {
        for (String inputFile : INPUT_FILES_FOR_READ_TCF_5) {
            testRead(inputFile, TCF_5, WebLEAF_SCHEMA_LOCATION_TCF_5);
        }
    }

    public void testRead(String inputFile, String tcfVersion, String webleafSchemaLocation) throws Exception {

        InputStream is = this.getClass().getResourceAsStream(inputFile);
        System.setProperty(SchemaFactory.class.getName() + ":" + XMLConstants.RELAXNG_NS_URI,
                "com.thaiopensource.relaxng.jaxp.CompactSyntaxSchemaFactory");
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.RELAXNG_NS_URI);
        Validator validator = null;
        try {
            //URL tcf04SchemaLocation = new URL(TCF04_SCHEMA_LOCATION);
            URL schemaLocation = this.getClass().getResource(webleafSchemaLocation);
            Schema schemaTcf = factory.newSchema(schemaLocation);
            validator = schemaTcf.newValidator();
            validator.setErrorHandler(new ErrorLocationErrorHandler());
        } catch (Exception e) {
            // should not happen (schema should be correct and found on server);
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            System.out.println(e.getMessage());
        }

        String message;
        boolean valid = false;
        try {
            validator.validate(new StreamSource(is));
            message = inputFile + " content is valid " + tcfVersion;
            valid = true;
        } catch (SAXException ex) {
            message = inputFile + " content is not valid " + tcfVersion + "because:\n" + ex.getMessage();
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
