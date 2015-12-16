/**
 * The Entity class represents a DBLP element as defined by dblp.dtd.
 * 
 * @author Lucas Dos Santos
 * @version 1.0 December 2015 
 */
public class Entity {

    /** The possible entities within an element. **/
    private String address;
    private String booktitle;
    private String cdrom;
    private String chapter;
    private String cite;
    private String crossref;
    private String ee;
    private Integer id;
    private String isbn;
    private String journal;
    private String month;
    private String note;
    private String number;
    private String pages;
    private String publisher;
    private String school;
    private String series;
    private String title;
    private String url;
    private String volume;
    private String year;

    /** The possible attributes for an element. **/
    private String key;
    private String mdate;
    private String publtype;
    private String rating;
    private String reviewId;
    
    private EntityType type;


    /**
     * Create an empty Entity object.
     */
    public Entity() {
        resetAllFields();
    }

    /**
     * Create an Entity object with a key and ID.
     * @param key the key attribute of the DBLP entry
     * @param id a uniquely assigned number used 
     */
    public Entity(String key, int id) {
        resetAllFields();
        this.key = key;
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public String getBooktitle() {
        return booktitle;
    }

    public String getCdrom() {
        return cdrom;
    }

    public String getChapter() {
        return chapter;
    }

    public String getCite() {
        return cite;
    }

    public String getCrossref() {
        return crossref;
    }

    public String getEe() {
        return ee;
    }

    public Integer getId() {
        return id;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getJournal() {
        return journal;
    }

    public String getKey() {
        return key;
    }

    public String getMdate() {
        return mdate;
    }

    public String getMonth() {
        return month;
    }

    public String getNote() {
        return note;
    }

    public String getNumber() {
        return number;
    }

    public String getPages() {
        return pages;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPubltype() {
        return publtype;
    }

    public String getRating() {
        return rating;
    }

    public String getReviewId() {
        return reviewId;
    }

    public String getSchool() {
        return school;
    }

    public String getSeries() {
        return series;
    }

    public String getTitle() {
        return title;
    }

    public EntityType getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public String getVolume() {
        return volume;
    }

    public String getYear() {
        return year;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBooktitle(String booktitle) {
        this.booktitle = booktitle;
    }

    public void setCdrom(String cdrom) {
        this.cdrom = cdrom;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public void setCite(String cite) {
        this.cite = cite;
    }

    public void setCrossref(String crossref) {
        this.crossref = crossref;
    }

    public void setEe(String ee) {
        this.ee = ee;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setMdate(String mdate) {
        this.mdate = mdate;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setPubltype(String publtype) {
        this.publtype = publtype;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(EntityType type) {
        this.type = type;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void resetAllFields() {
        this.key = "";
        this.id = 0;
        this.title = "";
        this.booktitle = "";
        this.pages = "";
        this.year = "";
        this.address = "";
        this.journal = "";
        this.volume = "";
        this.number = "";
        this.month = "";
        this.url = "";
        this.ee = "";
        this.cdrom = "";
        this.cite = "";
        this.publisher = "";
        this.note = "";
        this.crossref = "";
        this.isbn = "";
        this.series = "";
        this.school = "";
        this.chapter = "";
        this.mdate = "";
        this.publtype = "";
        this.reviewId = "";
        this.rating = "";
    }
}
