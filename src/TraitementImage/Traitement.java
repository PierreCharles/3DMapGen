package TraitementImage;

import Maillage.Maillage;
import Maillage.Sommet;
import Maillage.Face;
import java.awt.image.BufferedImage;
import java.util.TreeMap;
import Parametres.Parametres;
import static TraitementImage.Decoupage.getHauteurParcelle;
import static TraitementImage.Decoupage.getLargeurParcelle;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class Traitement {

    
     

    
    /**
     * Permet d'obtenir la hauteur d'un pixel donné de l'image chargée en fonction de ses coordonées
     * @param ligne Coordonnée y
     * @param colonne Coordonnée x
     * @param resolution Résolution de la hauteur en fonction des niveau de gris
     * @param image Image chargée dans le logiciel
     * @return La hauteur qu'il faut attribuer au sommet du maillage correspondant au pixel traitée
     */
    public static double getHauteurPixel(double ligne, double colonne, double resolution, BufferedImage image){
        int pixel, rouge, vert, bleu, moyenne;
        double hauteur;
        int x = (int) Math.floor(colonne);
        int y = (int) Math.floor(ligne);
        pixel = image.getRGB(x,y);

        rouge = (pixel >> 16) & 0xff;
        vert = (pixel >> 8) & 0xff;
        bleu = (pixel) & 0xff;
        moyenne = 255-(rouge+vert+bleu)/3;
        hauteur = (resolution*moyenne) + 5;
        
        return hauteur;
    }

    private static boolean isBordHaut(double ligne, double colonne) {
        return (ligne == 0 && colonne!= 0);
    }
    
    private static boolean isBordGauche(double ligne, double colonne) {
        return (colonne == 0 && ligne != 0);
    }
    
    private static boolean isBordDroit(double ligne, double colonne, double largeur, double hauteur) {
        return (colonne == largeur && ligne != 0 && ligne != hauteur);
    }
    
    private static boolean isBordBas(double ligne, double colonne, double largeur, double hauteur) {
        return (ligne == hauteur && colonne != 0 && colonne != largeur);
    }
    
    private static boolean isCentre(double ligne, double colonne, double largeur, double hauteur) {
        return (0 < ligne && ligne < hauteur && 0 < colonne && colonne < largeur);
    }
    
    public static Maillage ParcelleToMaillage(BufferedImage image, double max, Parametres para) {
        
        Maillage m = new Maillage();
        double resolution = max/256;
        double hauteur = image.getHeight()-1;
        double largeur = image.getWidth()-1;
        double epaisseur = 3;
        double debutLargeur = 0.1 * largeur, finLargeur = 0.9 * largeur, debutHauteur = 0.1 * hauteur, finHauteur = 0.9 * hauteur;
        
        for (double ligne = 0; ligne < hauteur; ligne++) {
            for(double colonne = 0; colonne < largeur; colonne++) {
                
                /* On créé le point de la surface en coordonnées ligne;colonne */
                m.ajouterSommet(ligne, colonne, new Sommet(ligne, getHauteurPixel(ligne, colonne, resolution, image), colonne));
            }
        }
        
        for (double ligne = 0; ligne < hauteur; ligne++) {
            for(double colonne = 0; colonne < largeur; colonne++) {
                if(doitEtreRemonte(image, ligne, colonne, debutLargeur, finLargeur, debutHauteur, finHauteur)){
                    m.ajouterSommetSocle(ligne, colonne, new Sommet(ligne, epaisseur, colonne));
                }
                else {
                  /* On créé le point du socle en coordonnées ligne;colonne */
                    m.ajouterSommetSocle(ligne, colonne, new Sommet(ligne, 0, colonne));  
                }
                
            }
        }
        
        for (double ligne = 0; ligne < hauteur; ligne++) {
            for(double colonne = 0; colonne < largeur; colonne++) {
                //System.out.println("ligne : " + ligne + " ; colonne : " + colonne);
                
                if(isBordHaut(ligne, colonne) || isBordBas(ligne, colonne, largeur, hauteur - 1)) {
                    /* Création du coté haut ou bas */
                    m.ajouterFace(new Face(m.getPointSurface(ligne, colonne).getId(), m.getPointSocle(ligne, colonne).getId(), m.getPointSocle(ligne, colonne - 1).getId()));
                    m.ajouterFace(new Face(m.getPointSocle(ligne, colonne - 1).getId(), m.getPointSurface(ligne, colonne - 1).getId(), m.getPointSurface(ligne, colonne).getId()));
                }
                
                if(isBordGauche(ligne, colonne)) {
                    /* Création de la face de la surface collée au bord */
                    m.ajouterFace(new Face(m.getPointSurface(ligne, colonne).getId(), m.getPointSurface(ligne - 1, colonne + 1).getId(), m.getPointSurface(ligne - 1, colonne).getId()));
                    /* Création de la face du socle collée au bord */
                    m.ajouterFace(new Face(m.getPointSocle(ligne, colonne).getId(), m.getPointSocle(ligne - 1, colonne + 1).getId(), m.getPointSocle(ligne - 1, colonne).getId()));
                    /* Création du côté gauche */
                    m.ajouterFace(new Face(m.getPointSurface(ligne, colonne).getId(), m.getPointSurface(ligne - 1, colonne).getId(), m.getPointSocle(ligne, colonne).getId()));
                    m.ajouterFace(new Face(m.getPointSurface(ligne - 1, colonne).getId(), m.getPointSocle(ligne - 1, colonne).getId(), m.getPointSocle(ligne, colonne).getId()));
                }
                
                if(isBordDroit(ligne, colonne, largeur - 1, hauteur)) {
                    /*Création de la face de la surface collée au bord */
                    m.ajouterFace(new Face(m.getPointSurface(ligne, colonne).getId(), m.getPointSurface(ligne - 1, colonne).getId(), m.getPointSurface(ligne, colonne - 1).getId()));
                    /* Création de la face du socle collé au bord */
                    m.ajouterFace(new Face(m.getPointSocle(ligne, colonne).getId(), m.getPointSocle(ligne - 1, colonne).getId(), m.getPointSocle(ligne, colonne - 1).getId()));
                    /* Création du côté droit */
                    m.ajouterFace(new Face(m.getPointSurface(ligne, colonne).getId(), m.getPointSocle(ligne, colonne).getId(), m.getPointSocle(ligne - 1, colonne).getId()));
                    m.ajouterFace(new Face(m.getPointSocle(ligne - 1, colonne).getId(), m.getPointSurface(ligne - 1, colonne).getId(), m.getPointSurface(ligne, colonne).getId()));
                }
                
                if(isCentre(ligne, colonne, largeur - 1, hauteur)) {
                    //System.out.println("ligne : " + ligne + " ; colonne : " + colonne);
                    m.ajouterFace(new Face(m.getPointSurface(ligne, colonne).getId(), m.getPointSurface(ligne - 1, colonne).getId(), m.getPointSurface(ligne, colonne - 1).getId()));
                    m.ajouterFace(new Face(m.getPointSurface(ligne, colonne).getId(), m.getPointSurface(ligne - 1, colonne + 1).getId(), m.getPointSurface(ligne - 1, colonne).getId()));
                    
                    m.ajouterFace(new Face(m.getPointSocle(ligne, colonne).getId(), m.getPointSocle(ligne - 1, colonne).getId(), m.getPointSocle(ligne, colonne - 1).getId()));
                    m.ajouterFace(new Face(m.getPointSocle(ligne, colonne).getId(), m.getPointSocle(ligne - 1, colonne + 1).getId(), m.getPointSocle(ligne - 1, colonne).getId()));
                }
            }
        }
        return m;
    }
    
    public static boolean doitEtreRemonte(BufferedImage image, double ligne, double colonne, double debutLargeur, double finLargeur, double debutHauteur, double finHauteur) {
        double deb = image.getWidth() * 0.1;
        double fin = image.getWidth() * 0.9;
        return (colonne >= debutLargeur && colonne<= finLargeur 
            && ligne >= debutHauteur && ligne <= (image.getHeight()-1) - debutLargeur  //zone du rectangle du socle
                
            || colonne >= ((image.getWidth()-1)-debutLargeur)/2 && colonne <= ((image.getWidth()-1)+debutLargeur)/2
            && ligne <= debutHauteur   //slot haut
                
            || colonne <= debutLargeur
            && ligne >= ((image.getHeight()-1)-debutHauteur)/2 && ligne <= ((image.getHeight()-1)+debutHauteur)/2 //slot gauche
                
            || colonne >= ((image.getWidth()-1)-debutLargeur)/2 && colonne <= ((image.getWidth()-1)+debutLargeur)/2
            && ligne >= (image.getHeight()-1)-debutHauteur     //slot bas
                
            || ligne >= ((image.getHeight()-1)-debutHauteur)/2 && ligne <= ((image.getHeight()-1)+debutHauteur)/2
            && colonne >= finLargeur);       //slot droit
    }
    
    public static Integer getNbAttache(int nbDecoupeL, int nbDecoupeH) {
        return (nbDecoupeL - 1) + (2*nbDecoupeL - 1) * (nbDecoupeH - 1);
    }
    
    /*
    Structure de l'attache:
    3___4           11___12
    |   |5__________7|   |
    |                    |
    |    6__________8    |
    |___|            |___|
    1   2           9    10
    Sommet s0X: sommet au dessus de sX
    */
    public static Maillage genererAttache(BufferedImage parcelle) {
        Maillage attache = new Maillage();
        double deb = parcelle.getWidth() * 0.1;
        Sommet s1 = new Sommet(0, 0, 0);
        attache.getEnsembleSommets().put(s1.getId(), s1);
        Sommet s01 = new Sommet(0, 3, 0);
        attache.getEnsembleSommets().put(s01.getId(), s01);
        Sommet s2 = new Sommet(deb/2, 0, 0);
        attache.getEnsembleSommets().put(s2.getId(), s2);
        Sommet s02 = new Sommet(deb/2, 3, 0);
        attache.getEnsembleSommets().put(s02.getId(), s02);
        Sommet s3 = new Sommet(0, 0, 2*deb);
        attache.getEnsembleSommets().put(s3.getId(), s3);
        Sommet s03 = new Sommet(0, 3, 2*deb);
        attache.getEnsembleSommets().put(s03.getId(), s03);
        Sommet s4 = new Sommet(deb/2, 0, 2*deb);
        attache.getEnsembleSommets().put(s4.getId(), s4);
        Sommet s04 = new Sommet(deb/2, 3, 2*deb);
        attache.getEnsembleSommets().put(s04.getId(), s04);
        Sommet s5 = new Sommet(deb/2, 0, 1.5*deb);
        attache.getEnsembleSommets().put(s5.getId(), s5);
        Sommet s05 = new Sommet(deb/2, 3, 1.5*deb);
        attache.getEnsembleSommets().put(s05.getId(), s05);
        Sommet s6 = new Sommet(deb/2, 0, deb/2);
        attache.getEnsembleSommets().put(s6.getId(), s6);
        Sommet s06 = new Sommet(deb/2, 3, deb/2);
        attache.getEnsembleSommets().put(s06.getId(), s06);
        Sommet s7 = new Sommet(2.5*deb, 0, 1.5*deb);
        attache.getEnsembleSommets().put(s7.getId(), s7);
        Sommet s07 = new Sommet(2.5*deb, 3, 1.5*deb);
        attache.getEnsembleSommets().put(s07.getId(), s07);
        Sommet s8 = new Sommet(2.5*deb, 0, deb/2);
        attache.getEnsembleSommets().put(s8.getId(), s8);
        Sommet s08 = new Sommet(2.5*deb, 3, deb/2);
        attache.getEnsembleSommets().put(s08.getId(), s08);
        Sommet s9 = new Sommet(2.5*deb, 0, 0);
        attache.getEnsembleSommets().put(s9.getId(), s9);
        Sommet s09 = new Sommet(2.5*deb, 3, 0);
        attache.getEnsembleSommets().put(s09.getId(), s09);
        Sommet s10 = new Sommet(3*deb, 0, 0);
        attache.getEnsembleSommets().put(s10.getId(), s10);
        Sommet s010 = new Sommet(3*deb, 3, 0);
        attache.getEnsembleSommets().put(s010.getId(), s010);
        Sommet s11 = new Sommet(2.5*deb, 0, 2*deb);
        attache.getEnsembleSommets().put(s11.getId(), s11);
        Sommet s011 = new Sommet(2.5*deb, 3, 2*deb);
        attache.getEnsembleSommets().put(s011.getId(), s011);
        Sommet s12 = new Sommet(3*deb, 0, 2*deb);
        attache.getEnsembleSommets().put(s12.getId(), s12);
        Sommet s012 = new Sommet(3*deb, 3, 2*deb);
        attache.getEnsembleSommets().put(s012.getId(), s012);
        //faces horizontales
        attache.getEnsembleFaces().add(new Face (s1.getId(), s2.getId(), s3.getId()));
        attache.getEnsembleFaces().add(new Face (s01.getId(), s02.getId(), s03.getId()));
        attache.getEnsembleFaces().add(new Face (s2.getId(), s3.getId(), s4.getId()));
        attache.getEnsembleFaces().add(new Face (s02.getId(), s03.getId(), s04.getId()));
        attache.getEnsembleFaces().add(new Face (s5.getId(), s6.getId(), s7.getId()));
        attache.getEnsembleFaces().add(new Face (s05.getId(), s06.getId(), s07.getId()));
        attache.getEnsembleFaces().add(new Face (s6.getId(), s7.getId(), s8.getId()));
        attache.getEnsembleFaces().add(new Face (s06.getId(), s07.getId(), s08.getId()));
        attache.getEnsembleFaces().add(new Face (s9.getId(), s10.getId(), s11.getId()));
        attache.getEnsembleFaces().add(new Face (s09.getId(), s010.getId(), s011.getId()));
        attache.getEnsembleFaces().add(new Face (s10.getId(), s11.getId(), s12.getId()));
        attache.getEnsembleFaces().add(new Face (s010.getId(), s011.getId(), s012.getId()));
        
        //faces verticales
        
        attache.getEnsembleFaces().add(new Face (s1.getId(), s01.getId(), s3.getId()));
        attache.getEnsembleFaces().add(new Face(s3.getId(), s03.getId(), s01.getId()));
        
        attache.getEnsembleFaces().add(new Face(s1.getId(), s01.getId(), s2.getId()));
        attache.getEnsembleFaces().add(new Face(s01.getId(), s02.getId(), s2.getId()));
        
        attache.getEnsembleFaces().add(new Face(s3.getId(), s03.getId(), s4.getId()));
        attache.getEnsembleFaces().add(new Face(s03.getId(), s04.getId(), s4.getId()));
        
        attache.getEnsembleFaces().add(new Face(s2.getId(), s02.getId(), s6.getId()));
        attache.getEnsembleFaces().add(new Face(s02.getId(), s06.getId(), s6.getId()));
        
        attache.getEnsembleFaces().add(new Face(s4.getId(), s04.getId(), s5.getId()));
        attache.getEnsembleFaces().add(new Face(s04.getId(), s05.getId(), s5.getId()));
        
        attache.getEnsembleFaces().add(new Face(s6.getId(), s06.getId(), s8.getId()));
        attache.getEnsembleFaces().add(new Face(s06.getId(), s08.getId(), s8.getId()));
        
        attache.getEnsembleFaces().add(new Face(s5.getId(), s05.getId(), s7.getId()));
        attache.getEnsembleFaces().add(new Face(s05.getId(), s07.getId(), s7.getId()));
        
        attache.getEnsembleFaces().add(new Face(s8.getId(), s9.getId(), s08.getId()));
        attache.getEnsembleFaces().add(new Face(s08.getId(), s9.getId(), s09.getId()));
        
        attache.getEnsembleFaces().add(new Face(s7.getId(), s07.getId(), s11.getId()));
        attache.getEnsembleFaces().add(new Face(s07.getId(), s11.getId(), s011.getId()));
        
        attache.getEnsembleFaces().add(new Face(s9.getId(), s10.getId(), s09.getId()));
        attache.getEnsembleFaces().add(new Face(s09.getId(), s10.getId(), s010.getId()));
        
        attache.getEnsembleFaces().add(new Face(s11.getId(), s12.getId(), s011.getId()));
        attache.getEnsembleFaces().add(new Face(s011.getId(), s012.getId(), s12.getId()));
        
        attache.getEnsembleFaces().add(new Face(s12.getId(), s10.getId(), s012.getId()));
        attache.getEnsembleFaces().add(new Face(s10.getId(), s012.getId(), s010.getId()));
        

        return attache;
    }
    
    public static void miseAEchelle(Maillage m, BufferedImage img, Parametres para) {
        
        double rapportX = para.getLargeurMaxImpression()/getLargeurParcelle();
        double rapportZ = para.getHauteurMaxImpression()/getHauteurParcelle();
        
        Set<Map.Entry<Double, TreeMap>> setLigne = m.getEnsembleSommets().entrySet();
            Iterator<Map.Entry<Double, TreeMap>> it = setLigne.iterator();
            while(it.hasNext()){
                Map.Entry<Double, TreeMap> e = it.next();
                TreeMap sommetTreeMap = e.getValue();
                
                
                Set<Map.Entry<Double,Sommet>> setColonne = sommetTreeMap.entrySet();
                Iterator<Map.Entry<Double,Sommet>> it2 = setColonne.iterator();
                
                while(it2.hasNext()){
                    Map.Entry<Double, Sommet> sommetEntry = it2.next();
                    sommetEntry.getValue().setX(sommetEntry.getValue().getX()*rapportX);
                    sommetEntry.getValue().setZ(sommetEntry.getValue().getZ()*rapportZ);
                }
                
            }
            
            /**
             * Ecriture de l'ensemble des points du socle
             */
            Set<Map.Entry<Double, TreeMap>> setLigneSocle = m.getEnsembleSommetsSocle().entrySet();
            Iterator<Map.Entry<Double, TreeMap>> it3 = setLigneSocle.iterator();
            while(it3.hasNext()){
                Map.Entry<Double, TreeMap> e2 = it3.next();
                TreeMap sommetTreeMapSocle = e2.getValue();
                
                
                Set<Map.Entry<Double,Sommet>> setColonneSocle = sommetTreeMapSocle.entrySet();
                Iterator<Map.Entry<Double,Sommet>> it4 = setColonneSocle.iterator();
                
                while(it4.hasNext()){
                    Map.Entry<Double, Sommet> sommetEntrySocle = it4.next();
                    sommetEntrySocle.getValue().setX(sommetEntrySocle.getValue().getX()*rapportX);
                    sommetEntrySocle.getValue().setZ(sommetEntrySocle.getValue().getZ()*rapportZ);
                }
                
            }
        
    }
    
}