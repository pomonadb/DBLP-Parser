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
               " PRIMARY KEY (eid));"
     ]

     entity_tuples = [
          "mastersthesis (eid INTEGER, `key` VARCHAR(250), title VARCHAR(1000), year INTEGER, url VARCHAR(250), ee VARCHAR(450), school VARCHAR(100), ",
          "phdthesis (eid INTEGER, `key` VARCHAR(250), title VARCHAR(1000), publisher VARCHAR(500), volume VARCHAR(40),  number VARCHAR(30), pages VARCHAR(100), month VARCHAR(50), year INTEGER, url VARCHAR(250), ee VARCHAR(450), note VARCHAR(250), isbn VARCHAR(100), series VARCHAR(500), school VARCHAR(100), ",
          "article (eid INTEGER,`key` VARCHAR(250),title VARCHAR(1000),publisher VARCHAR(500),journal VARCHAR(100),volume VARCHAR(40),number VARCHAR(30),pages VARCHAR(100),month VARCHAR(50),year INTEGER,url VARCHAR(250),ee VARCHAR(450),crossref VARCHAR(250),cite VARCHAR(1000),note VARCHAR(250),booktitle VARCHAR(250),cdrom VARCHAR(250), ",

          "proceedings (eid INTEGER, `key` VARCHAR(250), title VARCHAR(1000), publisher VARCHAR(500), journal VARCHAR(100), volume VARCHAR(40), number VARCHAR(30), pages VARCHAR(100), month VARCHAR(50), year INTEGER, url VARCHAR(250), ee VARCHAR(450), crossref VARCHAR(250),cite VARCHAR(1000),note VARCHAR(250),booktitle VARCHAR(250),isbn VARCHAR(100),series VARCHAR(500),address VARCHAR(100), ",
          "book (eid INTEGER, `key` VARCHAR(250), title VARCHAR(1000), publisher VARCHAR(500), volume VARCHAR(40), pages VARCHAR(100), year INTEGER, url VARCHAR(250), ee VARCHAR(450), crossref VARCHAR(250),cite VARCHAR(1000),note VARCHAR(250),booktitle VARCHAR(250),isbn VARCHAR(100),series VARCHAR(500),cdrom VARCHAR(250),school VARCHAR(100), ",
          "incollection (eid INTEGER, `key` VARCHAR(250), title VARCHAR(1000), publisher VARCHAR(500), number VARCHAR(30), pages VARCHAR(100), year INTEGER, url VARCHAR(250), ee VARCHAR(450), crossref VARCHAR(250), cite VARCHAR(1000), note VARCHAR(250),booktitle VARCHAR(250), chapter VARCHAR(250), cdrom VARCHAR(250), ",
          "inproceedings (eid INTEGER, `key` VARCHAR(250), title VARCHAR(1000), number VARCHAR(30), pages VARCHAR(100), month VARCHAR(50), year INTEGER, url VARCHAR(250), ee VARCHAR(450), crossref VARCHAR(250), cite VARCHAR(1000), note VARCHAR(250), booktitle VARCHAR(250), chapter INTEGER, address VARCHAR(100), cdrom VARCHAR(250), ",
          "www (eid INTEGER, `key` VARCHAR(250), title VARCHAR(1000), year INTEGER, url VARCHAR(500), ee VARCHAR(450), crossref VARCHAR(250), cite VARCHAR(1000), note VARCHAR(350), booktitle VARCHAR(250), chapter VARCHAR(250), school VARCHAR(100), "
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
