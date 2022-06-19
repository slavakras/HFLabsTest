package com.example.hflabstest;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.HashMap;
import java.util.Map;



@SpringBootApplication
public class HfLabsTestApplication {

    public static void main(String[] args) {
        Map<String, HashMap<String, Integer>> product = new HashMap<>();
        //Собираем данные
        String xmlURL = ParseUtils.parseFNSPage();
        File zippedXML = FileUtils.downloadFile(xmlURL, ".zip");
        File unZippedXML = FileUtils.unZip(zippedXML, ".xml");
        NodeList elements = ParseUtils.parseXML(unZippedXML);
        zippedXML.delete();
        unZippedXML.delete();

        for (int i = 0; i < elements.getLength(); i++) {
           String country = elements.item(i).getChildNodes().item(0)
                   .getChildNodes().item(0).getChildNodes().item(1)
                   .getAttributes().getNamedItem("НаимСтр").getNodeValue();
           if(!product.containsKey(country)) {
               product.put(country, new HashMap<>());
           }

           String akkrStatus = elements.item(i).getChildNodes().item(0)
                   .getChildNodes().item(1).getChildNodes().item(3)
                   .getAttributes().getNamedItem("СостАк").getNodeValue();

           if(!product.get(country).containsKey(akkrStatus)) {
               product.get(country).put(akkrStatus, 1);
           } else {
               int j  = product.get(country).get(akkrStatus) + 1;
               product.get(country).replace(akkrStatus,j);
           }
        }

        FileUtils.writeCSV(product);
    }

}
