import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

/**
 * Parser creates a new instance of SAXParser and parses the input file with the
 * Handler class.
 * 
 * @author Lucas Dos Santos
 * @version 1.0 December 2015
 */
public class Parser {

    /**
     * Creates a SAXParser instance and parses the file with the provided name.
     * 
     * @param fileName
     *            THe name of the File to parse.
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws Exception
     */
    private Parser(String fileName) throws SAXException, IOException,
            ParserConfigurationException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        Handler handler = new Handler();
        SAXParser parser = spf.newSAXParser();

        spf.setValidating(false);
        handler.openAllFos();
        parser.parse(new File(fileName), handler);
    }

    /**
     * The main routine.
     * 
     * @param args
     *            The command line arguments.
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     * @throws Exception
     */
    public static void main(String[] args) throws SAXException, IOException,
            ParserConfigurationException {
        if (args.length < 1) {
            System.out.println("Please specify the input file as an argument.");
        } else {
            String fileName = args[0];
            System.out.println("Parsing started.");
            new Parser(fileName);
            System.out.println("Parsing finished.");
        }
    }

}
