This program performs simple SQL operations on the Students table, which is structured using the following fields:
- student_id: Integer, Primary Key, Auto-increment
- first_name: Text, Not Null
- last_name: Text, Not Null
- email: Text, Not Null, Unique
- enrollment_date: Date

This program uses JDBC to connect to a table in a PostgreSQL database. The connection credentials to this are hard-coded in the main method and must be changed by the user if 
configurations vary. The program uses a Maven structure to include the PostgreSQL driver as a dependency in the pom.xml file.

This program supports operations to retrieve all the data in the table, add a student to the table, remove a student from the table, and update a particular student's email.
In the GUI, each of these operations has a corresponding button. In scenarios where the user is required to enter information to perform the desired query, an input dialogue will
appear to do so. Then, clicking 'OK' will perform the query. The GUI also contains a text area for viewing the data.

To execute this program, use an IDE that supports Java (such as IntelliJ).
Alternatively, it could be run from the command line using the following steps:
- Navigate to the location of the project, then within the project to src > main > java
- compile with 'javac StudentManager.java'
- run with 'java StudentManager'

See demo video for this program here: https://vimeo.com/1135402864?fl=ip&fe=ec

IMPORTANT NOTES:
1) This program does not have error checking implemented, which means all data inputted for queries must conform to field requirements (i.e. not null and/or unique if applicable), 
be the correct data type, and correct format (in the case of dates, which must be 'yyyy-mm-dd').

2) It is assumed that the Students table has already been created and its initial values have been set. For users who wish to do this, see Student_SQL_initial.txt for the SQL
   statements to create and initialize the table.
