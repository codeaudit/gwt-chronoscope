package org.timepedia.chronoscope.server;

import org.timepedia.chronoscope.client.gss.GssPropertyManager;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 *Print Gss Docs
 */
public class GssDocGenerator {
    public static void main(String[] args) {
        for(Field f : GssPropertyManager.class.getFields()) {
            f.setAccessible(true);
            if(Modifier.isStatic(f.getModifiers())) {
                if(f.getName().startsWith("GSS_") && f.getName().endsWith("_TYPE")) {
                    try {
                        GssPropertyManager.GssElementType t = (GssPropertyManager.GssElementType) f.get(GssPropertyManager.class);
                        System.out.println("Element "+t.getName());

                        System.out.println("Docstring "+t.getDocString());
                        for(GssPropertyManager.GssPropertyType p : t.getProperties())  {
                            System.out.println("\t\tProperty "+p.getName()+" \t\t: "+p.getDocString());
                        }

                        System.out.println("classes: ");
                        for(String gssClass: t.getClasses()) {
                            System.out.println(gssClass);
                        }

                        System.out.println("Example: ");
                        for(String xmp: t.getExamples()) {
                            System.out.println(xmp);
                        }

                        System.out.println("\nChild elements:");
                        for(GssPropertyManager.GssElementType c : t.getChildTypes()) {
                            System.out.println("\t"+c.getName()+", ");
                        }
                        System.out.println("");
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            }
        }
    }
}
