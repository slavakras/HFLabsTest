package com.example.hflabstest;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class FileUtils {
    private static String filename = "result.csv";

    public static File downloadFile(String url, String extension) {
        File file;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            file = File.createTempFile(UUID.randomUUID().toString(), extension);
            inputStream = new URL(url).openStream();
            outputStream = new FileOutputStream(file);
        } catch (MalformedURLException e) {
            System.out.println("Некорректная ссылка для скачивания");
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            System.out.println("Не удалось создать временный файл");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("Не удалось скачать файл");
            throw new RuntimeException(e);
        }
        try {
            IOUtils.copy(inputStream,outputStream);
        } catch (IOException e) {
            System.out.println("Временный файл не создан");
            throw new RuntimeException(e);
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                System.out.println("Потоки остались висеть");
                throw new RuntimeException(e);
            } finally {
                return file;
            }
        }

    }

    public static File unZip (File file, String extension) {
        ZipFile zip = new ZipFile(file);
        List<FileHeader> fileHeaders;
        FileHeader header;
        File unzipped;
        try {
            fileHeaders = zip.getFileHeaders();
            header = fileHeaders.get(0);
            Pattern pattern = Pattern.compile(".*\\.XML$");
            if(!pattern.matcher(header.getFileName()).matches()) {
                throw new RuntimeException("В архиве не xml файла");
            }
            unzipped = File.createTempFile(UUID.randomUUID().toString(), extension);
        } catch (ZipException e) {
            System.out.println("Проблема с файлом");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("Не удалось создать временный файл");
            throw new RuntimeException(e);
        }
        ZipInputStream zis = null;
        FileOutputStream out = null;
        try {
            zis = zip.getInputStream(header);
            out = new FileOutputStream(unzipped);
            IOUtils.copy(zis,out);
        } catch (IOException e) {
            System.out.println("Не удалось распаковать файл");
            throw new RuntimeException(e);
        } finally {
            try {
                zis.close();
                out.close();
            } catch (IOException e) {
                System.out.println("Потоки остались висеть");
                throw new RuntimeException(e);
            } finally {
                return unzipped;
            }
        }

    }

    public static void writeCSV(Map<String, HashMap<String, Integer>> product) {
        try(CSVPrinter printer = new CSVPrinter(new FileWriter(filename), CSVFormat.DEFAULT)) {
            printer.printRecord("Страна", "Статус", "Количество");
            for(Map.Entry<String, HashMap<String, Integer>> entryParent : product.entrySet()) {
                int i = 0;
                for(Map.Entry<String, Integer> entryChild : entryParent.getValue().entrySet()) {
                    if(i == 0) {
                        printer.printRecord(entryParent.getKey(), entryChild.getKey(), entryChild.getValue());
                    } else {
                        printer.printRecord("", entryChild.getKey(), entryChild.getValue());
                    }
                    i++;
                }
            }
        } catch (IOException e) {
            System.out.println("Не удалось создать файл");
            throw new RuntimeException(e);
        }
    }
}
