READEME.txt

This is a README file for Carla Bustos and Zixiang Zhou for Assignment 8 CSc 460 Fall 2014.

How to implement the app:
_________

1.Copy the source code into tomcat instance directory
2.set up the database connection in 
SonoranCellular\WEB-INF\classes\SonoranCellular\utils\OracleConnect.java
3.Use the .sql files to create the tables and populate some sample data.
4.Use make under SonoranCellular\WEB-INF\classes\ to compile the .java file.
5.Run your tomcat! 

Configuration:
—————————
No revisions to the Makefile or web.xml file are needed.  The filenames are the same as the template provided.  The only exception is the DataUsage.jsp file which can be placed in JSP folder.  Otherwise, configure as was suggested in the Assignment 8 specifications.
 

Manifest:
—————————
Here are the files that we are submitting via turnin:
LoginServlet.java
AddAccountInformation.java
AddPlan.java
FindBill.java
index.html
SharedAssignment.jsp
DataUsage.jsp
create.sql
populate.sql
destroy.sql


Explanation of Extra Credit opportunity:
——————————————————
Effort was made to make the website look professional and fun.  We came up with a slogan of “The hottest name in Cellular”.  This inspired us to use neon “hot” colors and a background to try and make the colored text not too annoying.  For populating the database, we used character names from “Family Guy”, an animated series about a family. We also stored the extra .css and .js file on the cgi server, and linked those files to our sites.    

Next we decided that the user might be interested in a report that summarizes the user’s data usage.  So we added the option to the main menu and display the result.  Then the user can return to the main menu or logout. 
