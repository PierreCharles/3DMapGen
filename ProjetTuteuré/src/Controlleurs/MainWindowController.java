package Controlleurs;

import Maillage.Maillage;
import TraitementImage.Charger;
import static TraitementImage.Decoupage.decouperImage;

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
import static TraitementImage.Exporter.exportToObj;
import static TraitementImage.Exporter.createDirectory;
import static TraitementImage.Exporter.exportAttacheToObj;
import static TraitementImage.Traitement.ParcelleToMaillage;
import static TraitementImage.Traitement.genererAttache;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class MainWindowController extends Stage {
    
   // @FXML MenuItem ouvrir;
    private String imagePath;
    @FXML private ImageView viewImage;
    @FXML private Label etat;
    //@FXML private Label traitementLabel;
    @FXML private Button traitementButton;
    @FXML private MenuItem close;
    @FXML private Button enregistrer;
    @FXML private Button traitementBtn;
    @FXML private Button ouvrirBtn;
    
    
    public List<Maillage> listeParcelles = new ArrayList<>();
    public Maillage attache = new Maillage();
    
    public void initialize() {
        enregistrer.setDisable(true);
        traitementBtn.setDisable(true);
        
    }
    public void setButtonTrue() {
        enregistrer.setDisable(false);
        traitementBtn.setDisable(false);
        ouvrirBtn.setDisable(false);
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
            traitementBtn.setDisable(false);   
            ouvrirBtn.setDisable(true);           
        }

    }
    
    @FXML 
    public void close(ActionEvent event) {
        Platform.exit();
    }
    
    @FXML
    public void onTraitement(ActionEvent envent) {
        
        Charger ch = new Charger(new File(selectedFile.toURI()));
        ch.ajouterImage();
        List<BufferedImage> listeImages = decouperImage(ch, 45, 45, 20);
        for(BufferedImage image : listeImages) {
            listeParcelles.add(ParcelleToMaillage(image, 50.0, 0));
        }
        attache = genererAttache(listeImages.get(0));
        enregistrer.setDisable(false);
        traitementBtn.setDisable(true);
    }
    
    @FXML
    public void enregistrer(ActionEvent envent) throws IOException{
        int i = 1;
        DirectoryChooser dir = new DirectoryChooser();
        dir.setTitle("Enregistrer");
        dir.setInitialDirectory(new File("C://"));
        
        File selectedSaveFile = dir.showDialog(this);
        System.out.println(selectedSaveFile.toString());
        if(selectedSaveFile != null){
            createDirectory(selectedSaveFile.toString(), "Maillage");
            for(Maillage m : listeParcelles) {
                exportToObj(m, selectedSaveFile.toString(), "Maillage", i);
                i++;
            }
            exportAttacheToObj(selectedSaveFile.toString(), "Maillage", attache);
            System.out.println("Exportation terminée");
        }
        this.setButtonTrue();
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
    
    public void ouvrirDialogue() {
        
    }
}
