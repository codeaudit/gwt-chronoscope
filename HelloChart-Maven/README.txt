
-- Chronoscope's HelloChart Sample application for maven

-- Import your project into Eclipse  --
 . Add the plugin m2eclipse to your eclipse installation.
 . Then import the project 
   File -> Import... -> Maven Projects,
   Browse to the directory containing this file,
   select "HelloChart-Maven",
   Click Finish.
 . You can now browse the project in Eclipse.

-- Running your project in Eclipse
 . Add the plugin google-eclipse
 . Use Google Web Toolkit
   Project -> Properties -> Web Toolkit -> Use Google Web Toolkit
 . Configure as a google web application
   Project -> Properties -> Web Application -> This project has a WAR directory ->
   -> WAR Directory: src/main/webapp
 . You and now run the project in Eclipse.
   Project -> Run as... -> Web Application

-- Run/Compile the application using Maven --

 . Assuming you have 'maven2' installed in your system, 'mvn' is 
   in your path, and you have access to maven repositories, you should be able to run:

   $ mvn clean         # delete temporary stuff
   $ mvn test          # run all the tests (gwt and junit)
   $ mvn gwt:run       # run development mode
   $ mvn gwt:compile   # compile to javascript
   $ mvn package       # generate a .war package ready to deploy

 . For more information about other available goals, read maven and gwt-maven-plugin 
   documentation (http://maven.apache.org, http://mojo.codehaus.org/gwt-maven-plugin)  
