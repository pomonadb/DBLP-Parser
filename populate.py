#! /usr/bin/python

try:
     import MySQLdb
except ImportError:
     print \
          "Please install the mysqldb plugin using one of the following commands \n" \
          "\t pip install mysql-python \n " \
          "\t easy_install install mysql-python # deprecated \n " \
          "\t sudo apt-get install python-mysqldb \n" \
          "\t yum install MySQL-python \n" \
          "or follow the link below for Windows instructions: \n" \
          "\t http://stackoverflow.com/questions/21440230/install-mysql-python-windows"
import os
import sys
import getpass
import csv


def main():
     # get the database name and password from the user
     db_name = raw_input("Enter the database: ")
     pw = getpass.getpass()

     # Connect to the database and create the access point (cursor)
     db = MySQLdb.connect(passwd=pw, db=db_name, local_infile=1)
     c = db.cursor()
     c.execute("SET character_set_connection = latin1")

     # Disable autocommit so we can rollback if anything fails
     db.autocommit(False)

     csvDir = raw_input("Path to the directory containing the CSV files: ")

     try:
          create_tables(c)
          print "Tables created"

          for root, dirs, files in os.walk(csvDir):
               files.sort()
               print "just sorted"
               for file_name in files:
                    print "Through the files"
                    if file_name.endswith(".csv"):
                         file_path = os.path.join(root,file_name)
                         fields = get_fields(file_path)
                         table_name, ext = os.path.splitext(file_name)

                         # build and execute the sql command to create the table
                         sql = generate_sql(file_path, db_name, table_name[2:], fields)
                         print sql
                         c.execute(sql)

          # Commit to the Database if everything worked
          db.commit()

          # Close the cursor and open a new one -- create joined tables
          c.close()
          c = db.cursor()
          populate_fullpubs(c)
          populate_coauthor(c)

          # commit, close, and exit
          db.commit()
          db.close()
          sys.exit(0)

     except Exception as err:
          # undo everything if something broke
          print "SOMETHING BROKE"
          print type(err)
          print err.args
          print err
          db.rollback()
          sys.exit(1)

# grab the field names from the csv file
def get_fields(fp):
     with open(fp) as f:
          reader = csv.reader(f)
          return next(reader)

# create the tables necessary to read the data.
def create_tables(conn):
     tables = [
          "CREATE TABLE person (pid INTEGER, name VARCHAR(100), PRIMARY KEY (pid));",

          "CREATE TABLE entity (eid INTEGER, PRIMARY KEY (eid));",

          "CREATE TABLE writes (author_id INTEGER NOT NULL, entity_id INTEGER,"\
               " PRIMARY KEY (author_id,entity_id),"\
               " FOREIGN KEY (author_id) REFERENCES person (pid),"\
               " FOREIGN KEY (entity_id) REFERENCES entity (eid));",

          "CREATE TABLE edits (editor_id INTEGER NOT NULL, entity_id INTEGER,"\
               " PRIMARY KEY (editor_id,entity_id),"\
               " FOREIGN KEY (editor_id) REFERENCES person (pid),"\
               " FOREIGN KEY (entity_id) REFERENCES entity (eid));",

          "CREATE TABLE publications (eid INTEGER, title VARCHAR(1000),"\
               " month VARCHAR(50), year INTEGER,"\
               " PRIMARY KEY (eid));",

          "CREATE TABLE co_author( author_id INTEGER, coauthor_id INTEGER,"\
               " month CHAR(50), year INTEGER, entity_id INTEGER,"\
               " PRIMARY KEY (author_id, coauthor_id, entity_id),"\
               " FOREIGN KEY (author_id) REFERENCES person(pid),"\
               " FOREIGN KEY (coauthor_id) REFERENCES person(pid),"\
               " FOREIGN KEY (entity_id) REFERENCES entity(id));"
     ]

     entity_tuples = [
          "mastersthesis (eid INTEGER,"\
               " `key` CHAR(70),"\
               " title CHAR(150),"\
               " year INTEGER, "\
               " url CHAR(100),"\
               " ee CHAR(100),"\
               " school CHAR(100), ",
          "phdthesis (eid INTEGER,"\
               " `key` CHAR(70),"\
               " title CHAR(350),"\
               " publisher CHAR(50),"\
               " volume CHAR(20), "\
               " number CHAR(20), "\
               "pages CHAR(20), "\
               "month CHAR(10),"\
               "year INTEGER, "\
               "url CHAR(200), "\
               "ee CHAR(200), "\
               "note CHAR(100), "\
               "isbn CHAR(20), "\
               "series CHAR(100), "\
               "school CHAR(100), ",
          "article (eid INTEGER,"\
               "`key` CHAR(70), "\
               "title VARCHAR(1000),"\
               "publisher CHAR(70),"\
               "journal CHAR(150),"\
               "volume CHAR(40),"\
               "number CHAR(30),"\
               "pages CHAR(20),"\
               "month CHAR(30),"\
               "year INTEGER,"\
               "url CHAR(100),"\
               "ee VARCHAR(450),"\
               "crossref CHAR(50),"\
               "cite VARCHAR(1000),"\
               "note CHAR(150),"\
               "booktitle VARCHAR(50),"\
               "cdrom VARCHAR(50), ",

          "proceedings (eid INTEGER, "\
               "`key` CHAR(70), "\
               "title CHAR(550), "\
               "publisher CHAR(170), "\
               "journal CHAR(20), "\
               "volume CHAR(40), "\
               "number CHAR(15), "\
               "pages CHAR(20), "\
               "month CHAR(50), "\
               "year INTEGER, "\
               "url CHAR(100), "\
               "ee CHAR(250), "\
               "crossref CHAR(25),"\
               "cite VARCHAR(1000),"\
               "note CHAR(245),"\
               "booktitle CHAR(150),"\
               "isbn CHAR(60),"\
               "series CHAR(107)),"\
               "address CHAR(15), ",
          "book (eid INTEGER, "\
               "`key` CHAR(70), "\
               "title CHAR(400), "\
               "publisher CHAR(70), "\
               "volume CHAR(20), "\
               "pages CHAR(25), "\
               "year INTEGER, "\
               "url CHAR(150), "\
               "ee CHAR(275), "\
               "crossref CHAR(250),"\
               "cite VARCHAR(1000),"\
               "note CHAR(100),"\
               "booktitle CHAR(150),"\
               "isbn CHAR(60),"\
               "series CHAR(150),"\
               "cdrom CHAR(25),"\
               "school CHAR(50), ",
          "incollection (eid INTEGER,"\
               " `key` CHAR(70),"\
               " title CHAR(250),"\
               " publisher CHAR(50),"\
               " number CHAR(30),"\
               " pages CHAR(100),"\
               " year INTEGER,"\
               " url CHAR(250),"\
               " ee CHAR(450),"\
               " crossref CHAR(250),"\
               " cite VARCHAR(1000),"\
               " note CHAR(250),"\
               "booktitle CHAR(250),"\
               " chapter CHAR(250),"\
               " cdrom CHAR(250), ",

          "inproceedings (eid INTEGER,"\
               " `key` CHAR(70),"\
               " title CHAR(600),"\
               " number CHAR(320),"\
               " pages CHAR(40),"\
               " month CHAR(150),"\
               " year INTEGER,"\
               " url CHAR(100),"\
               " ee CHAR(500),"\
               " crossref CHAR(50),"\
               " cite VARCHAR(1000),"\
               " note CHAR(200),"\
               " booktitle CHAR(150),"\
               " chapter INTEGER,"\
               " address CHAR(100),"\
               " cdrom CHAR(90),",

          "www (eid INTEGER,"\
               " `key` CHAR(70),"\
               " title CHAR(70),"\
               " year INTEGER,"\
               " url CHAR(500),"\
               " ee CHAR(50),"\
               " crossref CHAR(50),"\
               " cite VARCHAR(800),"\
               " note CHAR(450),"\
               " booktitle CHAR(10),"\
               " chapter CHAR(250),"\
               " school CHAR(100),"
     ]

     create = "CREATE TABLE "
     keyinfo = " PRIMARY KEY (eid), FOREIGN KEY (eid) REFERENCES entity (eid));"

     entities = [create + tup + keyinfo for tup in entity_tuples]

     tables.extend(entities)

     for t in tables:
          conn.execute(t)

def populate_fullpubs(c):
    c.execute('''
    INSERT INTO publications
     (SELECT entity.eid, title, month, year FROM entity
         INNER JOIN phdthesis ON entity.eid = phdthesis.eid)
     UNION
     (SELECT entity.eid, title, month, year FROM entity
         INNER JOIN article ON entity.eid = article.eid)
     UNION
     (SELECT entity.eid, title, null, year FROM entity
         INNER JOIN book ON entity.eid = book.eid)
     UNION
     (SELECT entity.eid, title, null, year FROM entity
         INNER JOIN mastersthesis ON entity.eid = mastersthesis.eid)
     UNION
     (SELECT entity.eid, title, null, year FROM entity
         INNER JOIN incollection ON entity.eid = incollection.eid)
     UNION
     (SELECT entity.eid, title, month, year FROM entity
         INNER JOIN inproceedings ON entity.eid = inproceedings.eid)
     UNION
     (SELECT entity.eid, title, month, year FROM entity
         INNER JOIN proceedings ON entity.eid = proceedings.eid)
     UNION
     (SELECT entity.eid, title, null, year FROM entity
         INNER JOIN www ON entity.eid = www.eid);
     ''')

def populate_coauthor():
     c.execute("""
          INSERT INTO co_author(
               SELECT p1.pid as author_id, p2.pid as coauthor_id,
                      P.month as month, P.year as year, P.eid as entity_id
               FROM person p1, person p2, writes w1, writes w2, publications P
               WHERE
                    p1.pid = w1.author_id AND
                    w1.entity_id = w2.entity_id AND
                    w2.author_id = p2.pid AND
                    P.eid = w1.entity_id
                    AND NOT p1.pid = p2.pid
          );
     """)


# Build the command to create the table
def generate_sql(fp, db, table, fields):
     # We know if the table has a key field its in the second place, so check it
     if (len(fields) > 1 and fields[1] == "key"):
          fields[1] = "`" + fields[1] + "`"

     # Return the formatted query
     return "LOAD DATA LOCAL INFILE '{0}' ".format(fp) + \
               "INTO TABLE `{1}` ".format(db,table) + \
               "FIELDS TERMINATED BY ',' " + \
               "IGNORE 1 LINES " +\
               "({0});".format(", ".join(fields))

if __name__ == '__main__' : main()
