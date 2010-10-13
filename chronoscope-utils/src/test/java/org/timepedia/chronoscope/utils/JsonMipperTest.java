package org.timepedia.chronoscope.utils;

import java.io.IOException;
import java.net.URL;

import org.timepedia.chronoscope.utils.JsonMipper;

import junit.framework.TestCase;

public class JsonMipperTest extends TestCase {
  JsonMipper mipper = new JsonMipper(); 
  
  public void testMipperJsonString() throws IOException {
    String s = mipper.mipJsonString("{id: 'id', domainscale: 1000, domain: [1,2], range: [3,4], label: 'label', axis: '$'}");
    assertEquals("{id:'id',label:'label',axis:'$',mipped:true,domain:[[1000.0,2000.0],[1000.0]],range:[[3.0,4.0],[3.5]],rangeTop:2000.0,rangeBottom:1000.0}", s.replaceAll("\\s+", ""));
    
    s = mipper.mipJsonString("data = {domainscale: 1000, domain: [1,2], range: [3,4], label: 'label', axis: '$'}");
    assertEquals("data={label:'label',axis:'$',mipped:true,domain:[[1000.0,2000.0],[1000.0]],range:[[3.0,4.0],[3.5]],rangeTop:2000.0,rangeBottom:1000.0};", s.replaceAll("\\s+", ""));

    s = mipper.mipJsonString("[{id: 'id', domainscale: 1000, domain: [1,2], range: [3,4], label: 'label', axis: '$'}," +
    		"{id: 'id2', domainscale: 1000, domain: [1,2], range: [3,4], label: 'label', axis: '$'}]");
    assertEquals("[{id:'id',label:'label',axis:'$',mipped:true,domain:[[1000.0,2000.0],[1000.0]],range:[[3.0,4.0],[3.5]],rangeTop:2000.0,rangeBottom:1000.0}," +
    		"{id:'id2',label:'label',axis:'$',mipped:true,domain:[[1000.0,2000.0],[1000.0]],range:[[3.0,4.0],[3.5]],rangeTop:2000.0,rangeBottom:1000.0}]", s.replaceAll("\\s+", ""));
    
    s = mipper.mipJsonString("[{id: 'id', domainscale: 1000, domain: [1,2], range: [3,4], label: 'label', axis: '$'}," +
        "{id: 'id2', domainscale: 1000, domain: [1,2], range: [3,4], label: 'label', axis: '$'}]");
    assertEquals("[{id:'id',label:'label',axis:'$',mipped:true,domain:[[1000.0,2000.0],[1000.0]],range:[[3.0,4.0],[3.5]],rangeTop:2000.0,rangeBottom:1000.0}," +
    		"{id:'id2',label:'label',axis:'$',mipped:true,domain:[[1000.0,2000.0],[1000.0]],range:[[3.0,4.0],[3.5]],rangeTop:2000.0,rangeBottom:1000.0}]", s.replaceAll("\\s+", ""));
  }

  
  public void testMipperReadFile() throws IOException {
    URL url = Thread.currentThread().getContextClassLoader().getResource("dash.js");
    String json = mipper.mipJsonFile(url.getFile());
    assertTrue(json.contains("1.28170656E12,1.2817068E12"));

    url = Thread.currentThread().getContextClassLoader().getResource("dffdatamip.js");
    json = mipper.mipJsonFile(url.getFile());
    assertTrue(json.contains("1.14480"));
  }

}
