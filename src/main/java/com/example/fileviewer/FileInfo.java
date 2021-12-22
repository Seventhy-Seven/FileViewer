package com.example.fileviewer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;



public class FileInfo {     //класс описания хранмых файлов в таблице

    public enum FileType{       //перечисление о типах файлов
        FILE("F"), DIRECTORY("D");

        private final String name;   //переменная возвращающая в зависимости от типа файла либо F(если файл), либо D(если директория)

        FileType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private final String filename;  //имя файла
    private final FileType type;    //тип файла
    private long size;              //размер файла
    private final LocalDateTime lastModified;   //время последней модификации

    public String getName() {
        return filename;
    }

    public FileType getType() {
        return type;
    }

    public long getSize() {
        return size;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public FileInfo(Path path){   //собираем объект FileInfo по заданному пути Path path
        try{
            this.filename = path.getFileName().toString();  //из пути выдёргиваем имя файла, преобразуем в строку
            this.size = Files.size(path);   //по заданному пути path возвращает размер файла в байтах
            this.type = Files.isDirectory(path) ? FileType.DIRECTORY : FileType.FILE;  //если смотрим на директорию, тип файла будет директория, иначе файл

            if(this.type == FileType.DIRECTORY){
                this.size = -1L;   //если тип файла директория - размер равен -1 (необходимо для сортировки файлов в таблице)
            }
            this.lastModified = LocalDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneOffset.ofHours(0));
            //получаем LocalDateTime из объекта типа Instant, длаее запрашиваем время последней модификации файла по указанному пути,
            // затем преобразуем в Instant, затем в LocalDateTime


        } catch (IOException e) {
            throw new RuntimeException("Невозможно создать информацию о файле");  //новое исключение останавливающее создание объекта
        }
    }
}
