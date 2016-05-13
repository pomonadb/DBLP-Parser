import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The Handler class overwrites the DefaultHandler methods for handling
 * DBLP-specific XML elements and attributes. The class iterates through all
 * elements in the input XML file and writes the publication data to
 * corresponding CSV files (e.g., <article> elements to article.csv).
 *
 * @author Lucas Dos Santos
 * @version 1.0 December 2015
 */
public class Handler extends DefaultHandler {

    /** The types of Entities. */
    private final String ARTICLE = "article";
    private final String INPROCEEDINGS = "inproceedings";
    private final String PROCEEDINGS = "proceedings";
    private final String BOOK = "book";
    private final String INCOLLECTION = "incollection";
    private final String PHDTHESIS = "phdthesis";
    private final String MASTERSTHESIS = "mastersthesis";
    private final String WWW = "www";

    /** The Entity attributes. */
    private final String AUTHOR = "author";
    private final String EDITOR = "editor";
    private final String TITLE = "title";
    private final String BOOKTITLE = "booktitle";
    private final String PAGES = "pages";
    private final String YEAR = "year";
    private final String ADDRESS = "address";
    private final String JOURNAL = "journal";
    private final String VOLUME = "volume";
    private final String NUMBER = "number";
    private final String MONTH = "month";
    private final String URL = "url";
    private final String EE = "ee";
    private final String CDROM = "cdrom";
    private final String CITE = "cite";
    private final String PUBLISHER = "publisher";
    private final String NOTE = "note";
    private final String CROSSREF = "crossref";
    private final String ISBN = "isbn";
    private final String SERIES = "series";
    private final String SCHOOL = "school";
    private final String CHAPTER = "chapter";

    /** The column names for each csv */
    private final String ENTITY_COLS = "eid\n";
    private final String ARTICLE_COLS = "eid,key,title,publisher,journal,volume,number,pages,month,year,url,ee,crossref,cite,note,booktitle,cdrom\n";
    private final String INPROCEDINGS_COLS = "eid,key,title,number,pages,month,year,url,ee,crossref,cite,note,booktitle,chapter,address,cdrom\n";
    private final String PROCEEDINGS_COLS = "eid,key,title,publisher,journal,volume,number,pages,year,url,ee,crossref,cite,note,booktitle,isbn,series,address\n";
    private final String BOOK_COLS = "eid,key,title,publisher,volume,pages,year,url,ee,crossref,cite,note,booktitle,isbn,series,cdrom,school\n";
    private final String INCOLLECTION_COLS = "eid,key,title,publisher,number,pages,year,url,ee,crossref,cite,note,booktitle,chapter,cdrom\n";
    private final String PHDTHESIS_COLS = "eid,key,title,publisher,volume,number,pages,month,year,url,ee,note,isbn,series,school\n";
    private final String MASTERSTHESIS_COLS = "eid,key,title,year,url,ee,school\n";
    private final String WWW_COLS = "eid,key,title,year,url,ee,crossref,cite,note,booktitle,chapter,school\n";
    private final String WRITES_COLS = "author_id,entity_id\n";
    private final String EDITS_COLS = "editor_id,entity_id\n";
    private final String PERSON_COLS = "pid,name\n";


    /**
     * Used to keep track of the current Entity being parsed and its surrounding
     * XML element.
     */
    private String surroundingElement;
    private String currentEntity;

    /** The Entity object corresponding to the current Entity being parsed. **/
    private Entity e;

    /**
     * Used to assign unique IDs to every Entity and person (either author or
     * editor) encountered.
     **/
    private int entityId;
    private int personId;

    /**
     * Used to store the author and editor name of the current Entity being
     * parsed.
     **/
    private String authorName;
    private String editorName;

    /**
     * Used to map person names to a unique ID and to the publications they have
     * written and edited.
     **/
    private HashMap<String, Integer> people;
    private HashMap<String, HashSet<Integer>> editors;
    private HashMap<String, HashSet<Integer>> authors;

    /** The output streams used to write the entities to CSV files. **/
    private FileOutputStream entity_fos;
    private FileOutputStream article_fos;
    private FileOutputStream inproceedings_fos;
    private FileOutputStream proceedings_fos;
    private FileOutputStream book_fos;
    private FileOutputStream incollection_fos;
    private FileOutputStream phdthesis_fos;
    private FileOutputStream mastersthesis_fos;
    private FileOutputStream www_fos;
    private FileOutputStream writes_fos;
    private FileOutputStream edits_fos;
    private FileOutputStream person_fos;

    /**
     * Open all the CSV files and write the column headers
     *
     * @throws SAXException
     */
    public void openAllFos() throws SAXException {
        try {
            new File("output").mkdirs();

            /*
             * Please note that the variable names are misnomers -- the values
             * they refer to are more appropriately refered to by the fileName
             * they generate.
             */
            entity_fos = new FileOutputStream("output/01entity.csv");
            entity_fos.write((ENTITY_COLS).getBytes());

            article_fos = new FileOutputStream("output/02article.csv");
            article_fos.write((ARTICLE_COLS).getBytes());

            inproceedings_fos = new FileOutputStream("output/03inproceedings.csv");
            inproceedings_fos.write((INPROCEDINGS_COLS).getBytes());

            proceedings_fos = new FileOutputStream("output/04proceedings.csv");
            proceedings_fos.write((PROCEEDINGS_COLS).getBytes());

            book_fos = new FileOutputStream("output/05book.csv");
            book_fos.write((BOOK_COLS).getBytes());

            incollection_fos = new FileOutputStream("output/06incollection.csv");
            incollection_fos.write((INCOLLECTION_COLS).getBytes());

            phdthesis_fos = new FileOutputStream("output/07phdthesis.csv");
            phdthesis_fos.write((PHDTHESIS_COLS).getBytes());

            mastersthesis_fos = new FileOutputStream("output/08mastersthesis.csv");
            mastersthesis_fos.write((MASTERSTHESIS_COLS).getBytes());

            www_fos = new FileOutputStream("output/09person.csv");
            www_fos.write((WWW_COLS).getBytes());

            writes_fos = new FileOutputStream("output/10alias_writes.csv");
            writes_fos.write((WRITES_COLS).getBytes());

            edits_fos = new FileOutputStream("output/11alias_edits.csv");
            edits_fos.write((EDITS_COLS).getBytes());

            person_fos = new FileOutputStream("output/00alias.csv");
            person_fos.write((PERSON_COLS).getBytes());

        } catch (Exception e) {
            throw (new SAXException("Error opening output file", e));
        }
    }

    /**
     * Receive notification of the beginning of the document.
     */
    public void startDocument() throws SAXException {
        e = new Entity();
        people = new HashMap<String, Integer>();
        editors = new HashMap<String, HashSet<Integer>>();
        authors = new HashMap<String, HashSet<Integer>>();
    }

    /**
     * Receive notification of the start of an XML element. If it is a DBLP
     * element, reset the Entity object fields, update its type, and set its key
     * and ID.
     *
     * @param namespaceURI
     *            The namespace URI, or the empty string if the element has no
     *            namespace URI or if namespace processing is not being
     *            performed.
     * @param localName
     *            The local name (without prefix), or the empty string if
     *            namespace processing is not being performed.
     * @param qName
     *            The qualified name (with prefix), or the empty string if
     *            qualified names are not available.
     * @param atts
     *            The attributes attached to the element. If there are no
     *            attributes, it shall be an empty Attributes object.
     *
     * @throws SAXException
     */
    public void startElement(String namespaceURI, String localName,
            String qName, Attributes atts) throws SAXException {
        currentEntity = qName;
        if (qName.equals(ARTICLE)) {
            e.resetAllFields();
            e = new Entity(atts.getValue("key"), entityId);
            entityId++;
            e.setType(EntityType.ARTICLE);
            surroundingElement = ARTICLE;
        } else if (qName.equals(INPROCEEDINGS)) {
            e.resetAllFields();
            e = new Entity(atts.getValue("key"), entityId);
            entityId++;
            e.setType(EntityType.INPROCEEDINGS);
            surroundingElement = INPROCEEDINGS;
        } else if (qName.equals(PROCEEDINGS)) {
            e.resetAllFields();
            e = new Entity(atts.getValue("key"), entityId);
            entityId++;
            e.setType(EntityType.PROCEEDINGS);
            surroundingElement = PROCEEDINGS;
        } else if (qName.equals(BOOK)) {
            e.resetAllFields();
            e = new Entity(atts.getValue("key"), entityId);
            entityId++;
            e.setType(EntityType.BOOK);
            surroundingElement = BOOK;
        } else if (qName.equals(INCOLLECTION)) {
            e.resetAllFields();
            e = new Entity(atts.getValue("key"), entityId);
            entityId++;
            e.setType(EntityType.INCOLLECTION);
            surroundingElement = INCOLLECTION;
        } else if (qName.equals(PHDTHESIS)) {
            e.resetAllFields();
            e = new Entity(atts.getValue("key"), entityId);
            entityId++;
            e.setType(EntityType.PHDTHESIS);
            surroundingElement = PHDTHESIS;
        } else if (qName.equals(MASTERSTHESIS)) {
            e.resetAllFields();
            e = new Entity(atts.getValue("key"), entityId);
            entityId++;
            e.setType(EntityType.MASTERSTHESIS);
            surroundingElement = MASTERSTHESIS;
        } else if (qName.equals(WWW)) {
            e.resetAllFields();
            e = new Entity(atts.getValue("key"), entityId);
            entityId++;
            e.setType(EntityType.WWW);
            surroundingElement = WWW;
        } else if (qName.equals(TITLE)) {
            // Required because some title have formatting tags nested within
            // them (e.g.<i>).
            surroundingElement = TITLE;
        }
    }

    /**
     * Receive notification of the end of an XML element. If it is a DBLP
     * entity, write its attributes to the corresponding CSV file. If it is an
     * author or editor, add their names, IDs, and the publication they
     * wrote/edited to the relevant maps.
     *
     * The values written for each entity can be changed to the columns the user
     * wishes to see in the resulting CSV file.
     *
     * @param namespaceURI
     *            The namespace URI, or the empty string if the element has no
     *            namespace URI or if namespace processing is not being
     *            performed.
     * @param localName
     *            The local name (without prefix), or the empty string if
     *            namespace processing is not being performed.
     * @param qName
     *            The qualified name (with prefix), or the empty string if
     *            qualified names are not available.
     * @param atts
     *            The attributes attached to the element. If there are no
     *            attributes, it shall be an empty Attributes object.
     *
     * @throws SAXException
     */
    public void endElement(String namespaceURI, String localName, String qName)
            throws SAXException {
        String result = "";
        try {
            if (qName.equals(ARTICLE)) {
                result = e.getId() + "," + e.getKey() + ","
                        + writeValue(e.getTitle()) + ","
                        + writeValue(e.getPublisher()) + ","
                        + writeValue(e.getJournal()) + ","
                        + writeValue(e.getVolume()) + ","
                        + writeValue(e.getNumber()) + ","
                        + writeValue(e.getPages()) + ","
                        + writeValue(e.getMonth()) + ","
                        + writeValue(e.getYear()) + ","
                        + writeValue(e.getUrl()) + "," + writeValue(e.getEe())
                        + "," + writeValue(e.getCrossref()) + ","
                        + writeValue(e.getCite()) + ","
                        + writeValue(e.getNote()) + ","
                        + writeValue(e.getBooktitle()) + ","
                        + writeValue(e.getCdrom()) + "\n";
                article_fos.write((result).getBytes());
                entity_fos.write((e.getId() + "\n").getBytes());
            } else if (qName.equals(INPROCEEDINGS)) {
                result = e.getId() + "," + e.getKey() + ","
                        + writeValue(e.getTitle()) + ","
                        + writeValue(e.getNumber()) + ","
                        + writeValue(e.getPages()) + ","
                        + writeValue(e.getMonth()) + ","
                        + writeValue(e.getYear()) + ","
                        + writeValue(e.getUrl()) + "," + writeValue(e.getEe())
                        + "," + writeValue(e.getCrossref()) + ","
                        + writeValue(e.getCite()) + ","
                        + writeValue(e.getNote()) + ","
                        + writeValue(e.getBooktitle()) + ","
                        + writeValue(e.getChapter()) + ","
                        + writeValue(e.getAddress()) + ","
                        + writeValue(e.getCdrom()) + "\n";
                inproceedings_fos.write((result).getBytes());
                entity_fos.write((e.getId() + "\n").getBytes());
            } else if (qName.equals(PROCEEDINGS)) {
                result = e.getId() + "," + e.getKey() + ","
                        + writeValue(e.getTitle()) + ","
                        + writeValue(e.getPublisher()) + ","
                        + writeValue(e.getJournal()) + ","
                        + writeValue(e.getVolume()) + ","
                        + writeValue(e.getNumber()) + ","
                        + writeValue(e.getPages()) + ","
                        + writeValue(e.getYear()) + ","
                        + writeValue(e.getUrl()) + "," + writeValue(e.getEe())
                        + "," + writeValue(e.getCrossref()) + ","
                        + writeValue(e.getCite()) + ","
                        + writeValue(e.getNote()) + ","
                        + writeValue(e.getBooktitle()) + ","
                        + writeValue(e.getIsbn()) + ","
                        + writeValue(e.getSeries()) + ","
                        + writeValue(e.getAddress()) + "\n";
                proceedings_fos.write((result).getBytes());
                entity_fos.write((e.getId() + "\n").getBytes());
            } else if (qName.equals(BOOK)) {
                result = e.getId() + "," + e.getKey() + ","
                        + writeValue(e.getTitle()) + ","
                        + writeValue(e.getPublisher()) + ","
                        + writeValue(e.getVolume()) + ","
                        + writeValue(e.getPages()) + ","
                        + writeValue(e.getYear()) + ","
                        + writeValue(e.getUrl()) + "," + writeValue(e.getEe())
                        + "," + writeValue(e.getCrossref()) + ","
                        + writeValue(e.getCite()) + ","
                        + writeValue(e.getNote()) + ","
                        + writeValue(e.getBooktitle()) + ","
                        + writeValue(e.getIsbn()) + ","
                        + writeValue(e.getSeries()) + ","
                        + writeValue(e.getCdrom()) + ","
                        + writeValue(e.getSchool()) + "\n";
                book_fos.write((result).getBytes());
                entity_fos.write((e.getId() + "\n").getBytes());
            } else if (qName.equals(INCOLLECTION)) {
                result = e.getId() + "," + e.getKey() + ","
                        + writeValue(e.getTitle()) + ","
                        + writeValue(e.getPublisher()) + ","
                        + writeValue(e.getNumber()) + ","
                        + writeValue(e.getPages()) + ","
                        + writeValue(e.getYear()) + ","
                        + writeValue(e.getUrl()) + "," + writeValue(e.getEe())
                        + "," + writeValue(e.getCrossref()) + ","
                        + writeValue(e.getCite()) + ","
                        + writeValue(e.getNote()) + ","
                        + writeValue(e.getBooktitle()) + ","
                        + writeValue(e.getChapter()) + ","
                        + writeValue(e.getCdrom()) + "\n";
                incollection_fos.write((result).getBytes());
                entity_fos.write((e.getId() + "\n").getBytes());
            } else if (qName.equals(PHDTHESIS)) {
                result = e.getId() + "," + e.getKey() + ","
                        + writeValue(e.getTitle()) + ","
                        + writeValue(e.getPublisher()) + ","
                        + writeValue(e.getVolume()) + ","
                        + writeValue(e.getNumber()) + ","
                        + writeValue(e.getPages()) + ","
                        + writeValue(e.getMonth()) + ","
                        + writeValue(e.getYear()) + ","
                        + writeValue(e.getUrl()) + "," + writeValue(e.getEe())
                        + "," + writeValue(e.getNote()) + ","
                        + writeValue(e.getIsbn()) + ","
                        + writeValue(e.getSeries()) + ","
                        + writeValue(e.getSchool()) + "\n";
                phdthesis_fos.write((result).getBytes());
                entity_fos.write((e.getId() + "\n").getBytes());
            } else if (qName.equals(MASTERSTHESIS)) {
                result = e.getId() + "," + e.getKey() + ","
                        + writeValue(e.getTitle()) + ","
                        + writeValue(e.getYear()) + ","
                        + writeValue(e.getUrl()) + "," + writeValue(e.getEe())
                        + "," + writeValue(e.getSchool()) + "\n";
                mastersthesis_fos.write((result).getBytes());
                entity_fos.write((e.getId() + "\n").getBytes());
            } else if (qName.equals(WWW)) {
                result = e.getId() + "," + e.getKey() + ","
                        + writeValue(e.getTitle()) + ","
                        + writeValue(e.getYear()) + ","
                        + writeValue(e.getUrl()) + "," + writeValue(e.getEe())
                        + "," + writeValue(e.getCrossref()) + ","
                        + writeValue(e.getCite()) + ","
                        + writeValue(e.getNote()) + ","
                        + writeValue(e.getBooktitle()) + ","
                        + writeValue(e.getChapter()) + ","
                        + writeValue(e.getSchool()) + "\n";
                www_fos.write((result).getBytes());
                entity_fos.write((e.getId() + "\n").getBytes());
            } else if (qName.equals(AUTHOR)) {
                people.put(authorName, personId);
                personId++;
                if (authors.containsKey(authorName)) {
                    authors.get(authorName).add(e.getId());
                } else {
                    HashSet<Integer> publications = new HashSet<Integer>();
                    publications.add(e.getId());
                    authors.put(authorName, publications);
                }
                authorName = "";
            } else if (qName.equals(EDITOR)) {
                people.put(editorName, personId);
                personId++;
                if (editors.containsKey(editorName)) {
                    editors.get(editorName).add(e.getId());
                } else {
                    HashSet<Integer> publications = new HashSet<Integer>();
                    publications.add(e.getId());
                    editors.put(editorName, publications);
                }
                editorName = "";
            }
        } catch (Exception e) {
            throw (new SAXException("Error writing to file.", e));
        }

    }

    /**
     * Receive notification of character data inside of a XML element. If it is
     * a DBLP entity, update the Entity object's corresponding fields and
     * attributes.
     *
     * SAX does not guarantee that all the characters of an XML element will be
     * included, so the method concatenates the Entity object's current value
     * with the new characters. Any commas are replaced with spaces as they
     * would cause issues with reading the CSV file.
     *
     * @param ch
     *            The characters.
     * @param start
     *            The start position in the character array.
     * @param length
     *            The number of characters to use from the character array.
     *
     * @throws SAXException
     */
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        try {
            if (currentEntity.equals(AUTHOR)) {
                authorName += new String(ch, start, length).replace(",", " ");
                return;
            } else if (currentEntity.equals(EDITOR)) {
                editorName += new String(ch, start, length).replace(",", " ");
                return;
            } else if (currentEntity.equals(TITLE)) {
                e.setTitle(e.getTitle()
                        + new String(ch, start, length).replace(",", " "));
                return;
            } else if (currentEntity.equals(BOOKTITLE)) {
                e.setBooktitle(e.getBooktitle()
                        + new String(ch, start, length).replace(",", " "));
                return;
            } else if (currentEntity.equals(PAGES)) {
                e.setPages(e.getPages()
                        + new String(ch, start, length).replace(",", " "));
                return;
            } else if (currentEntity.equals(YEAR)) {
                e.setYear(e.getYear()
                        + new String(ch, start, length).replace(",", " "));
                return;
            } else if (currentEntity.equals(ADDRESS)) {
                e.setAddress(e.getAddress()
                        + new String(ch, start, length).replace(",", " "));
                return;
            } else if (currentEntity.equals(JOURNAL)) {
                e.setJournal(e.getJournal()
                        + new String(ch, start, length).replace(",", " "));
                return;
            } else if (currentEntity.equals(VOLUME)) {
                e.setVolume(e.getVolume()
                        + new String(ch, start, length).replace(",", " "));
                return;
            } else if (currentEntity.equals(NUMBER)) {
                e.setNumber(e.getNumber()
                        + new String(ch, start, length).replace(",", " "));
                return;
            } else if (currentEntity.equals(MONTH)) {
                e.setMonth(e.getMonth()
                        + new String(ch, start, length).replace(",", " "));
                return;
            } else if (currentEntity.equals(URL)) {
                e.setUrl(e.getUrl()
                        + new String(ch, start, length).replace(",", " "));
                return;
            } else if (currentEntity.equals(EE)) {
                e.setEe(e.getEe()
                        + new String(ch, start, length).replace(",", " "));
                return;
            } else if (currentEntity.equals(CDROM)) {
                e.setCdrom(e.getCdrom()
                        + new String(ch, start, length).replace(",", " "));
                return;
            } else if (currentEntity.equals(CITE)) {
                e.setCite(e.getCite()
                        + new String(ch, start, length).replace(",", " "));
                return;
            } else if (currentEntity.equals(PUBLISHER)) {
                e.setPublisher(e.getPublisher()
                        + new String(ch, start, length).replace(",", " "));
                return;
            } else if (currentEntity.equals(NOTE)) {
                // Separate multiple notes by a new line.
                String notes = e.getNote();
                if (!notes.isEmpty()) {
                    notes += "\\n"
                            + new String(ch, start, length).replace(",", " ");
                } else {
                    notes = new String(ch, start, length).replace(",", " ");
                }
                e.setNote(notes);
                return;
            } else if (currentEntity.equals(CROSSREF)) {
                e.setCrossref(e.getCrossref()
                        + new String(ch, start, length).replace(",", " "));
                return;
            } else if (currentEntity.equals(ISBN)) {
                e.setIsbn(e.getIsbn()
                        + new String(ch, start, length).replace(",", " "));
                return;
            } else if (currentEntity.equals(SERIES)) {
                String series = e.getSeries();
                if (!series.isEmpty()) {
                    series += new String(ch, start, length).replace(",", " ");
                } else {
                    series = new String(ch, start, length).replace(",", " ");
                }
                e.setSeries(series);
                return;
            } else if (currentEntity.equals(SCHOOL)) {
                e.setSchool(e.getSchool()
                        + new String(ch, start, length).replace(",", " "));
                return;
            } else if (currentEntity.equals(CHAPTER)) {
                e.setChapter(e.getChapter()
                        + new String(ch, start, length).replace(",", " "));
                return;
            } else if (currentEntity.equals("sub")
                    || currentEntity.equals("sup") || currentEntity.equals("i")
                    || currentEntity.equals("tt")) {
                if (surroundingElement.equals(TITLE)) {
                    // Update the title with text within <sub>, <sup>, <i>, and
                    // <tt> tags
                    e.setTitle(e.getTitle()
                            + new String(ch, start, length).replace(",", " "));
                }
            }
        } catch (Exception e) {
            throw new SAXException("Invalid parser characters "
                    + e.getMessage());
        }
    }

    /**
     * Receive notification of the end of the XML document, write all the
     * person, author, and editor entries and close all the FileOutputStreams.
     *
     * @throws SAXException
     */
    public void endDocument() throws SAXException {
        System.out.println("Writing all the files.");
        try {
            for (String name : people.keySet()) {
                String personEntry = people.get(name) + "," + name + "\n";
                person_fos.write((personEntry).getBytes());
            }
            for (String name : authors.keySet()) {
                int authorId = people.get(name);
                for (Integer id : authors.get(name)) {
                    String writesEntry = authorId + "," + id + "\n";
                    writes_fos.write((writesEntry).getBytes());
                }
            }
            for (String name : editors.keySet()) {
                int editorId = people.get(name);
                for (Integer id : editors.get(name)) {
                    String editsEntry = editorId + "," + id + "\n";
                    edits_fos.write((editsEntry).getBytes());
                }
            }
        } catch (Exception e) {
            throw (new SAXException("Error writing to file.", e));
        }

        try {
            article_fos.close();
            inproceedings_fos.close();
            proceedings_fos.close();
            book_fos.close();
            incollection_fos.close();
            phdthesis_fos.close();
            mastersthesis_fos.close();
            www_fos.close();
            edits_fos.close();
            writes_fos.close();
            person_fos.close();
        } catch (IOException e) {
            throw (new SAXException("Error closing the files.", e));
        }
    }

    /**
     * Return the String if it is not empty, \N if it is empty.
     *
     * @param s
     *            The String to write.
     */
    private String writeValue(String s) {
        return (!s.isEmpty() ? s : "\\N");
    }

    /**
     * Return SAXParseException info in a String.
     *
     * @param spe
     *            The SAXParseException.
     */
    private String getParseExceptionInfo(SAXParseException spe) {
        String systemId = spe.getSystemId();
        if (systemId == null) {
            systemId = "null";
        }
        String info = "URI=" + systemId + " Line=" + spe.getLineNumber() + ": "
                + spe.getMessage();
        return info;
    }

    /**
     * Receive notification of a warning while parsing and print out relevant
     * information.
     *
     * @param e
     *            The warning information encoded as an exception.
     */
    public void warning(SAXParseException e) throws SAXException {
        System.out.println("Warning: " + getParseExceptionInfo(e));
        throw new SAXException("Warning encountered");
    }

    /**
     * Receive notification of an error while parsing and print out relevant
     * information.
     *
     * @param e
     *            The warning information encoded as an exception.
     */
    public void error(SAXParseException e) throws SAXException {
        System.out.println("Error:  " + getParseExceptionInfo(e));
        throw new SAXException("Error encountered");
    }

    /**
     * Receive notification of a fatal error while parsing and print out
     * relevant information.
     *
     * @param e
     *            The warning information encoded as an exception.
     */
    public void fatalError(SAXParseException e) throws SAXException {
        System.out.println("Fatal error : " + getParseExceptionInfo(e));
        throw new SAXException("Fatal error encountered.");
    }
}
