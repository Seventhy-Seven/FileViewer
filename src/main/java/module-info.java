module com.example.filemanager {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.example.fileviewer to javafx.fxml;
    exports com.example.fileviewer;
}