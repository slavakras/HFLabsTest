package com.example.hflabstest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;

public class ParseUtils {

    private static String nalogUrl = "http://data.nalog.ru/opendata/7707329152-rafp/";
    private static String xsdURL;

    public static String parseFNSPage() {
        String xmlURL;
        Document nalogPage = null;
        try {
            nalogPage = Jsoup.connect(nalogUrl).get();
        } catch (IOException e) {
            System.out.println("Не удалось открыть страницу ФНС, проверьте подключение и правильность ссылки");
            throw new RuntimeException(e);
        }
        xmlURL = nalogPage.getElementsByClass("border_table")
                .select("tr").get(8)
                .select("td").get(2)
                .select("a").attr("href");
        xsdURL = nalogPage.getElementsByClass("border_table")
                .select("tr").get(10)
                .select("td").get(2)
                .select("a").attr("href");
        return xmlURL;
    }

    public static NodeList parseXML (File file) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        File schema = FileUtils.downloadFile(xsdURL,".xsd");
        DocumentBuilder builder;
        org.w3c.dom.Document document;
        try {
            factory.setSchema(schemaFactory.newSchema(schema));
            builder = factory.newDocumentBuilder();
            document = builder.parse(file);
            document.normalize();
        } catch (ParserConfigurationException e) {
            System.out.println("Не удалось иницилизировать Парсер");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("Не удалось открыть файл");
            throw new RuntimeException(e);
        } catch (SAXException e) {
            System.out.println("Ошибка при парсинге документа");
            throw new RuntimeException(e);
        }
        schema.delete();
        return document.getDocumentElement().getChildNodes();
    }
}
