/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlleurs;

import Parametres.Parametres;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author petit
 */
public class ParametresController implements Initializable {

    /**
     * @param hauteurP the hauteurP to set
     */
    public void setHauteurP(StringProperty hauteurP) {
        this.hauteurP = hauteurP;
    }
    @FXML private Button annuler, valider;
    @FXML private TextField hauteurField, largeurField,hauteurMaillageField, largeurMaxImpressionField, hauteurMaxImpressionField;
    @FXML private Label labelError, adjLabel;
    private double hauteurMaillage, largeurMaxImpression, hauteurMaxImpression, hauteur, largeur;
    private Image image;
    private Stage thisStage;
    static Parametres para = new Parametres();
    private StringProperty hauteurP = new SimpleStringProperty();
    private StringProperty largeurP = new SimpleStringProperty();
    private Double ratioH, ratioL;
    
    public StringProperty hauteurProperty () {
        return hauteurP;
    }
    public String getHauteur () {
        return hauteurProperty().get();
    }
    
    ParametresController(Image image, Parametres para) {
        this.image = image; 
        this.para = para;

    }
    public void setStage(Stage thisStage) {
        this.thisStage = thisStage;    
    }   
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ratioL = image.getWidth() / image.getHeight();
        ratioH = image.getHeight() / image.getWidth();
    }   


    @FXML 
    public void Annuler() {
        thisStage.close();
    }
    @FXML
    public void HauteurBtn () {
        largeur = Double.parseDouble(largeurField.getText());
        hauteur = largeur * ratioH;
        hauteurField.setText(String.valueOf(hauteur));
    }
    @FXML 
    public void LargeurBtn () {
        hauteur = Double.parseDouble(largeurField.getText());
        largeur = hauteur * ratioL;
        largeurField.setText(String.valueOf(largeur));
    }
    @FXML 
    public void Valider() {
        if(hauteurField.getText().isEmpty() || largeurField.getText().isEmpty() || largeurMaxImpressionField.getText().isEmpty()
                || hauteurMaxImpressionField.getText().isEmpty() ) {
            labelError.setVisible(true);
        }
        else {
            hauteur = Double.parseDouble(hauteurField.getText());
            largeur = Double.parseDouble(largeurField.getText());
            hauteurMaillage = Double.parseDouble(hauteurMaillageField.getText());
            largeurMaxImpression = Double.parseDouble(largeurMaxImpressionField.getText());
            hauteurMaxImpression = Double.parseDouble(hauteurMaxImpressionField.getText()); 
            if(hauteur/largeur != ratioH) {
                adjLabel.setVisible(true);
            }
            else{
                para.setHauteurImage(hauteur);
                para.setHauteurMaillage(hauteurMaillage);
                para.setLargeurImage(largeur);
                para.setHauteurMaxImpression(hauteurMaxImpression);
                para.setLargeurMaxImpression(largeurMaxImpression);
                thisStage.close();
            }
        }
    }
}
