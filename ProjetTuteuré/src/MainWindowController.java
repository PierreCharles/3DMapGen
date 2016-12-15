import Maillage.Maillage;
import TraitementImage.Charger;
import TraitementImage.Traitement;
import static TraitementImage.Traitement.traitementNiveauDeGris;
import static TraitementImage.Exporter.ExportToObj;

import java.io.File;
import java.io.IOException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;

public class MainWindowController extends Stage {
    
    @FXML MenuItem ouvrir;
    private String imagePath;
    @FXML private ImageView viewImage;
    @FXML private Label etat;
    @FXML private Label traitementLabel;
    @FXML private Button traitementButton;
    @FXML private MenuItem close;
    @FXML private MenuItem enregistrer;
    
    public  Maillage m;
    
    public void setEtat(String val) {
        etat.setText(val);
    }
    
    private File selectedFile;
    @FXML
    public void ouvrir(ActionEvent event) {
        FileChooser imageChooser = new FileChooser();
        imageChooser.setTitle("ouvrir");
   
        imageChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        selectedFile = imageChooser.showOpenDialog(this);
        if(selectedFile != null) {
            imagePath = selectedFile.toURI().toString();
            viewImage.setImage(new Image(imagePath));
            setEtat("OK\n" + imagePath);
            traitementButton.setDisable(false);       
        }
        else{
            setEtat("Non chargée");
        }
    }
    
    @FXML 
    public void close(ActionEvent event) {
        Platform.exit();
    }
    
    @FXML
    public void onTraitement(ActionEvent envent) {
 
        Charger ch = new Charger(new File(selectedFile.toURI()));
        m = new Maillage();
        ch.ajouterImage();
        traitementNiveauDeGris(ch, m, 50.0, 0);
        
        traitementLabel.setText("Traitement effectué");
        enregistrer.setDisable(false);
    }
    @FXML
    public void enregister(ActionEvent envent) throws IOException{
        DirectoryChooser dir = new DirectoryChooser();
        dir.setTitle("Enregistrer");
        dir.setInitialDirectory(new File("C://"));
        
        File selectedSaveFile = dir.showDialog(this);
        System.out.println(selectedSaveFile.toString());
        if(selectedSaveFile != null){
            ExportToObj(m,selectedSaveFile.toString());
        }
    }
//    public void erreur(){
//        Stage dialogStage = new Stage();
//        dialogStage.initModality(Modality.WINDOW_MODAL);
//
//        VBox vbox = new VBox(new Text("Hi"), new Button("Ok."));
//        vbox.setAlignment(Pos.CENTER);
//
//
//        dialogStage.setScene(new Scene(vbox));
//        dialogStage.show();
//    }
}
