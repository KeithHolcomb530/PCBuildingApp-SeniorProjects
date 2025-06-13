module keith.pcbuildingappproject {
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires java.desktop;


    opens keith.pcbuildingappproject to javafx.fxml;
    exports keith.pcbuildingappproject;
}