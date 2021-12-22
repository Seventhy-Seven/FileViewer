package com.example.fileviewer;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Controller implements Initializable {

    @FXML
    TableView <FileInfo>filesTable;  //имя таблицы

    @FXML
    ComboBox<String> diskBox;   //переменная для выбора локального диска

    @FXML
    TextField pathField;   //рамка, указывающая путь файла

    @FXML
    Button buttonUp;    //имя кнопки вверх

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //создание столбца
        TableColumn<FileInfo,String> fileTypeColumn = new TableColumn<>(); //здесь хранится тип файла
        fileTypeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getType().getName())); //имя файла (D или F)
        fileTypeColumn.setPrefWidth(24);  //размер столбца

        TableColumn<FileInfo,String> fileNameColumn = new TableColumn<>("Имя"); //название столбца Имя
        fileNameColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName())); //запрос имени файла
        fileNameColumn.setPrefWidth(200);

        TableColumn<FileInfo,Long> fileSizeColumn = new TableColumn<>("Размер");
        fileSizeColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getSize())); //запрос на размер файла
        fileSizeColumn.setPrefWidth(100);
        fileSizeColumn.setCellFactory(column -> {
            //блок, отвечающий за внешнив вид ячейки в столбце
            return new TableCell<FileInfo, Long>(){
                @Override
                protected void updateItem(Long item, boolean empty){            //информация о самой ячейке, и заполнена ли ячейка
                    super.updateItem(item,empty);
                    if(item == null || empty){           //если Item или ячейка пустая
                        setText(null);                   //в ячейке ничего не пишем
                        setStyle("");
                    }else{                               //иначе прописываем все что есть в item + добавляем bytes
                        String text = String.format("%,d bytes ", item);
                        if(item == -1){                  //если тип файла директория
                            text = "[DIR]";              //меняем на DIR
                        }
                        setText(text);
                    }
                }
            };
        });

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  //форматтер даты и времени последнего изменения файла

        TableColumn<FileInfo,String> fileDateColumn = new TableColumn<>("Дата изменения");
        fileDateColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getLastModified().format(dateTimeFormatter)));
        fileDateColumn.setPrefWidth(150);

        filesTable.getColumns().addAll(fileTypeColumn,fileNameColumn,fileSizeColumn,fileDateColumn);  //добавили столбцы в таблицу
        filesTable.getSortOrder().add(fileTypeColumn); //в качестве старотовой сортировки использовать столбец с типами файлов

        diskBox.getItems().clear();  //перед выбором локльного диска производим очистку diskBox

        for (Path p: FileSystems.getDefault().getRootDirectories()) {   //запрос информации о файловой системе и получаем список локальных директорий
            diskBox.getItems().add(p.toString());                       //добавляем ссылку на каждый из дисков
        }
        diskBox.getSelectionModel().select(0);                        //по умолчанию выбираем первый из них

        filesTable.setOnMouseClicked(new EventHandler<MouseEvent>() {    //двойной клик мышкой
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getClickCount() == 2){
                    Path path = Paths.get(pathField.getText()).resolve(filesTable.getSelectionModel().getSelectedItem().getName());  //добавили путь и имя файла в пути
                    if(Files.isDirectory(path)){  //если файл директория и в ней лежит директория
                        updateList(path);         //переходим в новую директорию
                    }
                }
            }
        });

        updateList(Paths.get(".")); //указываем путь к корню текущего проекта
    }

    public void updateList(Path path){   //метод, собирает файлы по заданному пути

        try {
            filesTable.getItems().clear();  //очистка таблицы, перед добавлением файлов
            pathField.setText(path.normalize().toAbsolutePath().toString());  //вывод путя к корню
            filesTable.getItems().addAll(Files.list(path).map(FileInfo::new).collect(Collectors.toList())); //добавляем файлы через поток путей Files.list(path)
            //преобразование потока в файлы типа FileInfo (map(FileInfo::new)), затем собираем в лист и лист отдаём в таблицу
            filesTable.sort();  //сортировка данных в таблице
        } catch (IOException e) {
            //всплывающее окно предупреждения
            Alert alert = new Alert(Alert.AlertType.WARNING, "По какой-то причине не удалось обновить список файлов", ButtonType.OK);
            alert.showAndWait();
        }
    }

    public void exitAction() {      //метод закрытия программы
        Platform.exit();
    }

    public void buttonPathUpAction(){     //метод управления кнопкой "вверх"
        Path upperPath = Paths.get(pathField.getText()).getParent();   //берём строку, преобразуем в путь, хзаправшиваем родителя (getParent)
        if(upperPath != null){   //если над ссылкой есть еще переход
            updateList(upperPath);  //осуществляем переход выше
        }
    }

    public void selectDickAction(ActionEvent actionEvent) {   //метод выбора локальной директориии в выпадающем comboBox
        ComboBox<String> element = (ComboBox<String>)actionEvent.getSource();  //запрашиваем источник события
        updateList(Paths.get(element.getSelectionModel().getSelectedItem()));  //переход на выбранную строку
    }
}