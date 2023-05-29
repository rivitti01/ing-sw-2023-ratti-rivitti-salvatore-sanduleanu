package it.polimi.ingsw.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.net.URL;

public class LoaderFXML {
    private Pane view;

    public Pane getPage (String fileName){
        try {
            URL fileUrl = FXGraphicalUI.class.getResource("");
            if(fileUrl==null){
                throw new java.io.FileNotFoundException("FXML file cannot be found");
            }
            view = new FXMLLoader().load(fileUrl);
        } catch (Exception e){
            System.out.println("No page " + fileName + " please check FxmlLoader.");
        }
        return view;
    }
}
