package sample;

import javafx.fxml.FXML;

public class RootController {

    private Main main;

    public void setMain(Main main) {
        this.main = main;
    }
    @FXML
    public void exportToBMP(){
        main.getController().exportToBMP();
        main.getController().exportToTXT();
    }

    @FXML
    public void exportToTXT(){
        main.getController().exportToTXT();
    }

    @FXML
    public void importFromBMP(){
        main.getController().importFromBMP();
    }

    @FXML
    public void importFromTXT(){
        main.getController().importFromTXT();
    }
}
