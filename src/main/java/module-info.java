module csi.travail_pratique_3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;

    opens csi.travail_pratique_3 to javafx.fxml;
    exports csi.travail_pratique_3;
}