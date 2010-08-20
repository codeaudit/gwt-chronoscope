
This is the public beta release of Chronoscope. See http://timepedia.org/chronoscope for details.


NOTE:

- This version of chronoscope can be used with Gwt either 1.6.x and 2.0.x.
- It is the last chronoscope version supporting Gwt-1.6.x. and it is here 
  in order to be used in legacy applications which depend on old Gwt versions.
- We encourage people to use newer versions of Chronoscope and Gwt.
- Applications depending on gwt-2.0.x should include gwtexporter-2.0.10
- Applications depending on gwt-1.6.x should include gwtexporter-2.06


To compile the library with gwt-2.0.4 just type:
$ mvn clean install package

To compile with gwt-1.6.4 type:
$ mvn -P gwt-1.6 clean install package

