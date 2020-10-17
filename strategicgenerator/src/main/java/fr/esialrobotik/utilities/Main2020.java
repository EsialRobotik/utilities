package fr.esialrobotik.utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.esialrobotik.utilities.gson.Objectif;
import fr.esialrobotik.utilities.gson.Strategie;
import fr.esialrobotik.utilities.gson.Tache;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by franc on 27/04/2018.
 */
public class Main2020 {

    public static void main(String... arg) throws Exception {
        System.out.println("Génération de la stratégie");

        // Liste des objectifs de chaque côté
        // 0 = Bleu, 3000 = Jaune
        List<Objectif> objectifsCouleur0 = new ArrayList<>();
        List<Objectif> objectifsCouleur3000 = new ArrayList<>();



        /**
         * On va placer la bouée N dans le chenal et on fait de la place avec celle de l'autre couleur
         * Et on allume le phare
         * Score = Bouée placée (1) + Chenal (1) + Bouée placée (1) = 3
         * Score = Poser le phare (2) + Allumer le phare (3) + Phare qui fonctionne (10) = 15
         * Total = 18
         */
        int score = 18;
        List<Tache> tachesBoueeNord =  new ArrayList<>();
        tachesBoueeNord.add(new Tache("haaaaack", tachesBoueeNord.size()+1, 1, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
        tachesBoueeNord.add(new Tache("Sortie de la zone de départ pour bouée Nord", tachesBoueeNord.size()+1, 800, 670, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesBoueeNord.add(new Tache("Placement bouée Nord", tachesBoueeNord.size()+1, 610, 670, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesBoueeNord.add(new Tache("Alignement bouée Nord", tachesBoueeNord.size()+1, 610, 0, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
        tachesBoueeNord.add(new Tache("Marquage bouée Nord", tachesBoueeNord.size()+1, 610, 170, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
        tachesBoueeNord.add(new Tache("Libération bouée Nord", tachesBoueeNord.size()+1, 610, 670, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO_BACK, -1, Tache.Mirror.MIRRORY));
        tachesBoueeNord.add(new Tache("Placement bouée Nord à virer", tachesBoueeNord.size()+1, 200, 300, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesBoueeNord.add(new Tache("Marquage bouée Nord à virer", tachesBoueeNord.size()+1, 400, 300, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesBoueeNord.add(new Tache("Libération bouée Nord à virer", tachesBoueeNord.size()+1, 200, 300, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO_BACK, -1, Tache.Mirror.MIRRORY));
        tachesBoueeNord.add(new Tache("Alignement phare", tachesBoueeNord.size()+1, 0, 300, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
        // TODO action phare
        tachesBoueeNord.add(new Tache("Allumage expérience", tachesBoueeNord.size()+1, 110, 300, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        // TODO action pousser les bouées
        tachesBoueeNord.add(new Tache("Sortie zone N", tachesBoueeNord.size()+1, 200, 300, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO_BACK, -1, Tache.Mirror.MIRRORY));
        Objectif objectifBoueeN0 = new Objectif("Bouée Nord + Phare", objectifsCouleur0.size()+1, score, 1, tachesBoueeNord);
        Objectif objectifBoueeN3000 = new Objectif("Bouée Nord + Phare", objectifsCouleur3000.size()+1, score, 1, null);
        try {
            objectifBoueeN3000.generateMirror(objectifBoueeN0.taches);
        } catch (Exception e) {
            e.printStackTrace();
        }
        objectifsCouleur0.add(objectifBoueeN0);
        objectifsCouleur3000.add(objectifBoueeN3000);

        /**
         * On va placer la bouée S dans le chenal et on fait de la place avec celle de l'autre couleur
         * Score = Bouée placée (1) + Chenal (1) + Paire(2) + Bouée placée (1) = 5
         */
        score = 5;
        List<Tache> tachesBoueeSud =  new ArrayList<>();
        tachesBoueeSud.add(new Tache("Placement bouée Sud", tachesBoueeSud.size()+1, 980, 670, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesBoueeSud.add(new Tache("Alignement bouée Sud", tachesBoueeSud.size()+1, 980, 0, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
        tachesBoueeSud.add(new Tache("Marquage bouée Sud", tachesBoueeSud.size()+1, 980, 170, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
        tachesBoueeSud.add(new Tache("Libération bouée Sud", tachesBoueeSud.size()+1, 980, 670, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO_BACK, -1, Tache.Mirror.MIRRORY));
        tachesBoueeSud.add(new Tache("Placement bouée Sud à virer", tachesBoueeSud.size()+1, 1400, 300, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesBoueeSud.add(new Tache("Marquage bouée Sud à virer", tachesBoueeSud.size()+1, 1000, 300, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesBoueeSud.add(new Tache("Libération bouée Sud à virer", tachesBoueeSud.size()+1, 1400, 300, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO_BACK, -1, Tache.Mirror.MIRRORY));
        Objectif objectifBoueeS0 = new Objectif("Bouée Sud", objectifsCouleur0.size()+1, score, 1, tachesBoueeSud);
        Objectif objectifBoueeS3000 = new Objectif("Bouée Sud", objectifsCouleur3000.size()+1, score, 1, null);
        try {
            objectifBoueeS3000.generateMirror(objectifBoueeS0.taches);
        } catch (Exception e) {
            e.printStackTrace();
        }
        objectifsCouleur0.add(objectifBoueeS0);
        objectifsCouleur3000.add(objectifBoueeS3000);

        /**
         * On va taper les manches à air
         * Score = 15 pour les 2
         */
        score = 15;
        List<Tache> tachesManches =  new ArrayList<>();
        tachesManches.add(new Tache("Placement manche à air", tachesManches.size()+1, 1780, 210, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesManches.add(new Tache("Alignement manche à air", tachesManches.size()+1, 1780, 3000, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
        // Todo action bras
        tachesManches.add(new Tache("Taper les manches", tachesManches.size()+1, 1780, 700, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        Objectif objectifManches0 = new Objectif("Manches à air", objectifsCouleur0.size()+1, score, 1, tachesManches);
        Objectif objectifManches3000 = new Objectif("Manches à air", objectifsCouleur3000.size()+1, score, 1, null);
        try {
            objectifManches3000.generateMirror(objectifManches0.taches);
        } catch (Exception e) {
            e.printStackTrace();
        }
        objectifsCouleur0.add(objectifManches0);
        objectifsCouleur3000.add(objectifManches3000);


        /**
         * On vide le récif de notre couleur
         * Score = 5 bouées (5) + 5 chenal (5) + 2 paires (4) = 14
         */
        score = 14;
        List<Tache> tachesRecifS =  new ArrayList<>();
        tachesRecifS.add(new Tache("Placement recif sud", tachesRecifS.size()+1, 1600, 230, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesRecifS.add(new Tache("Alignement recif sud", tachesRecifS.size()+1, 1600, 0, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
        tachesRecifS.add(new Tache("Mise en position rammassage recif sud", tachesRecifS.size()+1, 1600, 100, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesRecifS.add(new Tache("Mise en position rammassage recif sud", tachesRecifS.size()+1, 100, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY, 500));
        // TODO actions rammassage
        tachesRecifS.add(new Tache("Sortie recif sud", tachesRecifS.size()+1, 1600, 230, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO_BACK, -1, Tache.Mirror.MIRRORY));
        tachesRecifS.add(new Tache("Placement largage sud", tachesRecifS.size()+1, 985, 200, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesRecifS.add(new Tache("Alignement largage sud", tachesRecifS.size()+1, 0, 200, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
        // Todo actions largage
        tachesRecifS.add(new Tache("Sortie largage sud", tachesRecifS.size()+1, -200, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
        tachesRecifS.add(new Tache("Placement recif nord", tachesRecifS.size()+1, 1200, 670, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesRecifS.add(new Tache("Placement recif nord", tachesRecifS.size()+1, 415, 670, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesRecifS.add(new Tache("Placement recif nord", tachesRecifS.size()+1, 415, 200, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesRecifS.add(new Tache("Alignement recif nord", tachesRecifS.size()+1, 2000, 200, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
        // Todo largage nord
        tachesRecifS.add(new Tache("Sortie largage nord", tachesRecifS.size()+1, -200, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
        tachesRecifS.add(new Tache("Sortie recif nord", tachesRecifS.size()+1, 415, 670, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        Objectif objectifRecifS0 = new Objectif("Récif sud", objectifsCouleur0.size()+1, score, 1, tachesRecifS);
        Objectif objectifRecifS3000 = new Objectif("Récif sud", objectifsCouleur3000.size()+1, score, 1, null);
        try {
            objectifRecifS3000.generateMirror(objectifRecifS0.taches);
        } catch (Exception e) {
            e.printStackTrace();
        }
        objectifsCouleur0.add(objectifRecifS0);
        objectifsCouleur3000.add(objectifRecifS3000);
        
        //
//        /**
//         * On récupère le côté pente du grand distributeur
//         */
//        List<Tache> tachesGrandDistribRouges = new ArrayList<>();
//        tachesGrandDistribRouges.add(new Tache("direction la grand distributeur rouges", tachesGrandDistribRouges.size()+1, 1320, 600, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        tachesGrandDistribRouges.add(new Tache("alignement grand distributeur rouges", tachesGrandDistribRouges.size()+1, 2000, 600, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
//        tachesGrandDistribRouges.add(new Tache("Preparation vidage grand distributeut rouges", tachesGrandDistribRouges.size()+1, 0, Tache.Type.MANIPULATION, null, 8, Tache.Mirror.MIRRORY));
//        tachesGrandDistribRouges.add(new Tache("vidage grand distributeur rouges", tachesGrandDistribRouges.size()+1, 150, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY, 1000));
//        tachesGrandDistribRouges.add(new Tache("liberation grand distributeur rouges", tachesGrandDistribRouges.size()+1, -50, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
//        tachesGrandDistribRouges.add(new Tache("recuperation grand distributeur rouges", tachesGrandDistribRouges.size()+1, 0, Tache.Type.MANIPULATION, null, 9, Tache.Mirror.MIRRORY));
//        tachesGrandDistribRouges.add(new Tache("rangement palets grand distributeur rouges", tachesGrandDistribRouges.size()+1, 150, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY, 1000));
//        tachesGrandDistribRouges.add(new Tache("remise en place pour la suite", tachesGrandDistribRouges.size()+1, -50, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
//        tachesGrandDistribRouges.add(new Tache("remise en place pour la suite", tachesGrandDistribRouges.size()+1, 1320, 600, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO_BACK, -1, Tache.Mirror.MIRRORY));
//        tachesGrandDistribRouges.add(new Tache("rangement des bras", tachesGrandDistribRouges.size()+1, 0, Tache.Type.MANIPULATION, null, 6, Tache.Mirror.MIRRORY));
//        Objectif objectifGrandDistribRouges0 = new Objectif("Récupération grand distributeur cote pente", objectifsCouleur0.size()+1, 0, 1, tachesGrandDistribRouges);
//        Objectif objectifGrandDistribRouges3000 = new Objectif("Récupération grand distributeur cote pente", objectifsCouleur3000.size()+1, 0, 1, null);
//        try {
//            objectifGrandDistribRouges3000.generateMirror(objectifGrandDistribRouges0.taches);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        objectifsCouleur0.add(objectifGrandDistribRouges0);
//        objectifsCouleur3000.add(objectifGrandDistribRouges3000);
//
//        /**
//         * On récupère le côté balance du grand distributeur
//         */
//        List<Tache> tachesGrandDistribBleu = new ArrayList<>();
//        tachesGrandDistribBleu.add(new Tache("direction la grand distributeur avec l'atome bleu", tachesGrandDistribBleu.size()+1, 1320, 900, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        tachesGrandDistribBleu.add(new Tache("alignement grand distributeur avec l'atome bleu", tachesGrandDistribBleu.size()+1, 2000, 900, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
//        tachesGrandDistribBleu.add(new Tache("Preparation vidage grand distributeut bleu", tachesGrandDistribBleu.size()+1, 0, Tache.Type.MANIPULATION, null, 3, Tache.Mirror.MIRRORY));
//        tachesGrandDistribBleu.add(new Tache("vidage grand distributeur avec atome bleu", tachesGrandDistribBleu.size()+1, 150, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY, 1000));
//        tachesGrandDistribBleu.add(new Tache("liberation grand distributeur avec atome bleu", tachesGrandDistribBleu.size()+1, -50, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
//        tachesGrandDistribBleu.add(new Tache("recuperation grand distributeur avec atome bleu", tachesGrandDistribBleu.size()+1, 0, Tache.Type.MANIPULATION, null, 4, Tache.Mirror.MIRRORY));
//        Objectif objectifGrandDistribBleu0 = new Objectif("Récupération grand distributeur avec atome bleu", objectifsCouleur0.size()+1, 0, 1, tachesGrandDistribBleu);
//        Objectif objectifGrandDistribBleu3000 = new Objectif("Récupération grand distributeur avec atome bleu", objectifsCouleur3000.size()+1, 0, 1, null);
//        try {
//            objectifGrandDistribBleu3000.generateMirror(objectifGrandDistribBleu0.taches);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        objectifsCouleur0.add(objectifGrandDistribBleu0);
//        objectifsCouleur3000.add(objectifGrandDistribBleu3000);
//
//        /**
//         * On évacue la zone de chaos
//         * 5 palets dont 1 de la bonne couleur = 1 * 5 + 5 = 10
//         * CHECKPOINT : TROP LONG, PAS RENTABLE, ON ZAPPE : 0 POINT
//         */
//        List<Tache> tachesChaos = new ArrayList<>();
//        tachesChaos.add(new Tache("Go position séquence chaos", tachesChaos.size()+1, 1320, 1280, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        tachesChaos.add(new Tache("Go position séquence chaos 2", tachesChaos.size()+1, 1050, 1280, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        // todo à virer
////        tachesChaos.add(new Tache("Alignement avec le chaos", tachesChaos.size()+1, 1050, 0, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
////        tachesChaos.add(new Tache("On pousse la chaos", tachesChaos.size()+1, 1050, 400, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
////        tachesChaos.add(new Tache("On pousse la chaos", tachesChaos.size()+1, 1050, 700, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO_BACK, -1, Tache.Mirror.MIRRORY));
////        tachesChaos.add(new Tache("Go position séquence chaos 2", tachesChaos.size()+1, 1050, 1280, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        // todo à virer
//        Objectif objectifChaos0 = new Objectif("Zone de chaos", objectifsCouleur0.size()+1, 0, 1, tachesChaos);
//        Objectif objectifChaos3000 = new Objectif("Zone de chaos", objectifsCouleur3000.size()+1, 0, 1, null);
//        try {
//            objectifChaos3000.generateMirror(objectifChaos0.taches);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        objectifsCouleur0.add(objectifChaos0);
//        objectifsCouleur3000.add(objectifChaos3000);
//
//        /**
//         * Largage accelerateur
//         * 3 palets * 10 points + 10 points de libération du goldenium = 40
//         */
//        List<Tache> tachesAccelerateur = new ArrayList<>();
//        List<Tache> tachesAccelerateur3000 = new ArrayList<>();
//        tachesAccelerateur.add(new Tache("Go position accelerateur", tachesAccelerateur.size()+1, 400, 1900, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        tachesAccelerateur.add(new Tache("Alignement position accelerateur", tachesAccelerateur.size()+1, 0, 1900, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
//        tachesAccelerateur.add(new Tache("On lève les palets", tachesAccelerateur.size()+1, 0, Tache.Type.MANIPULATION, null, 10, Tache.Mirror.MIRRORY));
//        tachesAccelerateur.add(new Tache("On s'approche", tachesAccelerateur.size()+1, 290, 1900, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        tachesAccelerateur.add(new Tache("On s'aligne", tachesAccelerateur.size()+1, 0, 1900, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
//        tachesAccelerateur.add(new Tache("On se colle parce que fuck", tachesAccelerateur.size()+1, 220, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY, 1000));
//        tachesAccelerateur.add(new Tache("On recule", tachesAccelerateur.size()+1, -95, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
//        int accelerateur = tachesAccelerateur.size()+1;
//        tachesAccelerateur.add(new Tache("On pose les palets", accelerateur, 0, Tache.Type.MANIPULATION, null, 11, Tache.Mirror.SPECIFIC));
//        tachesAccelerateur3000.add(new Tache("On pose les palets", accelerateur, 0, Tache.Type.MANIPULATION, null, 18, Tache.Mirror.SPECIFIC));
//        tachesAccelerateur.add(new Tache("On recule un poil", tachesAccelerateur.size()+1, -10, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
//        tachesAccelerateur.add(new Tache("On recule un poil", tachesAccelerateur.size()+1, -10, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
//        tachesAccelerateur.add(new Tache("On recule un poil", tachesAccelerateur.size()+1, 10, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
//        tachesAccelerateur.add(new Tache("On lache tout", tachesAccelerateur.size()+1, 0, Tache.Type.MANIPULATION, null, 12, Tache.Mirror.MIRRORY));
//        tachesAccelerateur.add(new Tache("petite impulsion magique", tachesAccelerateur.size()+1, 10, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
//        tachesAccelerateur.add(new Tache("Go position accelerateur", tachesAccelerateur.size()+1, 400, 1900, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO_BACK, -1, Tache.Mirror.MIRRORY));
//        tachesAccelerateur.add(new Tache("Alignement position accelerateur", tachesAccelerateur.size()+1, 0, 1900, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
//        Objectif objectifAccelerateur0 = new Objectif("Accelerateur", objectifsCouleur0.size()+1, 40, 1, tachesAccelerateur);
//        Objectif objectifAccelerateur3000 = new Objectif("Accelerateur", objectifsCouleur3000.size()+1, 40, 1, null);
//        try {
//            objectifAccelerateur3000.generateMirror(objectifAccelerateur0.taches, tachesAccelerateur3000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        objectifsCouleur0.add(objectifAccelerateur0);
//        objectifsCouleur3000.add(objectifAccelerateur3000);
//
//        /**
//         * Largage accelerateur 2
//         * 3 palets * 10 points = 30
//         */
//        List<Tache> tachesAccelerateur2 = new ArrayList<>();
//        List<Tache> tachesAccelerateur23000 = new ArrayList<>();
//        tachesAccelerateur2.add(new Tache("On lève les palets", tachesAccelerateur2.size()+1, 0, Tache.Type.MANIPULATION, null, 13, Tache.Mirror.MIRRORY));
//        tachesAccelerateur2.add(new Tache("On s'approche", tachesAccelerateur2.size()+1, 290, 1900, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        tachesAccelerateur2.add(new Tache("On s'aligne", tachesAccelerateur2.size()+1, 0, 1900, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
//        tachesAccelerateur2.add(new Tache("On recule un poil", tachesAccelerateur2.size()+1, 220, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY, 1000));
//        tachesAccelerateur2.add(new Tache("On recule un poil", tachesAccelerateur2.size()+1, -95, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
//        accelerateur = tachesAccelerateur2.size()+1;
//        tachesAccelerateur2.add(new Tache("On pose les palets", accelerateur, 0, Tache.Type.MANIPULATION, null, 11, Tache.Mirror.SPECIFIC));
//        tachesAccelerateur23000.add(new Tache("On pose les palets", accelerateur, 0, Tache.Type.MANIPULATION, null, 18, Tache.Mirror.SPECIFIC));
//        tachesAccelerateur2.add(new Tache("On recule un poil", tachesAccelerateur2.size()+1, -10, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
//        tachesAccelerateur2.add(new Tache("On recule un poil", tachesAccelerateur2.size()+1, -10, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
//        tachesAccelerateur2.add(new Tache("On recule un poil", tachesAccelerateur2.size()+1, 10, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
//        tachesAccelerateur2.add(new Tache("On lache tout", tachesAccelerateur2.size()+1, 0, Tache.Type.MANIPULATION, null, 12, Tache.Mirror.MIRRORY));
//        tachesAccelerateur2.add(new Tache("petite impulsion magique", tachesAccelerateur2.size()+1, 10, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
//        tachesAccelerateur2.add(new Tache("Go position accelerateur", tachesAccelerateur2.size()+1, 400, 1900, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO_BACK, -1, Tache.Mirror.MIRRORY));
//        tachesAccelerateur2.add(new Tache("Alignement position accelerateur", tachesAccelerateur2.size()+1, 0, 1900, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
//        Objectif objectifAccelerateur20 = new Objectif("Accelerateur", objectifsCouleur0.size()+1, 30, 1, tachesAccelerateur2);
//        Objectif objectifAccelerateur23000 = new Objectif("Accelerateur", objectifsCouleur3000.size()+1, 30, 1, null);
//        try {
//            objectifAccelerateur23000.generateMirror(objectifAccelerateur20.taches, tachesAccelerateur23000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        objectifsCouleur0.add(objectifAccelerateur20);
//        objectifsCouleur3000.add(objectifAccelerateur23000);
//
//        /**
//         * Largage accelerateur 3
//         * 3 palets * 10 points = 30
//         */
//        List<Tache> tachesAccelerateur3 = new ArrayList<>();
//        List<Tache> tachesAccelerateur33000 = new ArrayList<>();
//        tachesAccelerateur3.add(new Tache("On lève les palets", tachesAccelerateur3.size()+1, 0, Tache.Type.MANIPULATION, null, 14, Tache.Mirror.MIRRORY));
//        tachesAccelerateur3.add(new Tache("On s'approche", tachesAccelerateur3.size()+1, 290, 1900, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        tachesAccelerateur3.add(new Tache("On s'aligne", tachesAccelerateur3.size()+1, 0, 1900, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
//        tachesAccelerateur3.add(new Tache("On recule un poil", tachesAccelerateur3.size()+1, 220, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY, 1000));
//        tachesAccelerateur3.add(new Tache("On recule un poil", tachesAccelerateur3.size()+1, -95, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
//        accelerateur = tachesAccelerateur3.size()+1;
//        tachesAccelerateur3.add(new Tache("On pose les palets", accelerateur, 0, Tache.Type.MANIPULATION, null, 11, Tache.Mirror.SPECIFIC));
//        tachesAccelerateur33000.add(new Tache("On pose les palets", accelerateur, 0, Tache.Type.MANIPULATION, null, 18, Tache.Mirror.SPECIFIC));
//        tachesAccelerateur3.add(new Tache("On recule un poil", tachesAccelerateur3.size()+1, -10, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
//        tachesAccelerateur3.add(new Tache("On recule un poil", tachesAccelerateur3.size()+1, -10, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
//        tachesAccelerateur3.add(new Tache("On recule un poil", tachesAccelerateur3.size()+1, 10, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
//        tachesAccelerateur3.add(new Tache("On lache tout", tachesAccelerateur3.size()+1, 0, Tache.Type.MANIPULATION, null, 12, Tache.Mirror.MIRRORY));
//        tachesAccelerateur3.add(new Tache("petite impulsion magique", tachesAccelerateur3.size()+1, 10, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
//        tachesAccelerateur3.add(new Tache("Go position accelerateur", tachesAccelerateur3.size()+1, 400, 1900, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO_BACK, -1, Tache.Mirror.MIRRORY));
//        tachesAccelerateur3.add(new Tache("Alignement position accelerateur", tachesAccelerateur3.size()+1, 0, 1900, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
//        Objectif objectifAccelerateur30 = new Objectif("Accelerateur", objectifsCouleur0.size()+1, 30, 1, tachesAccelerateur3);
//        Objectif objectifAccelerateur33000 = new Objectif("Accelerateur", objectifsCouleur3000.size()+1, 30, 1, null);
//        try {
//            objectifAccelerateur33000.generateMirror(objectifAccelerateur30.taches, tachesAccelerateur33000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        objectifsCouleur0.add(objectifAccelerateur30);
//        objectifsCouleur3000.add(objectifAccelerateur33000);
//
//        /**
//         * Récupération du goldenium = 20 points
//         */
//        List<Tache> tacheRecupGoldenium = new ArrayList<>();
//        List<Tache> tacheRecupGoldenium3000 = new ArrayList<>();
//        tacheRecupGoldenium.add(new Tache("Go position goldenium", tacheRecupGoldenium.size()+1, 410, 2325, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        tacheRecupGoldenium.add(new Tache("Alignement goldenium", tacheRecupGoldenium.size()+1, 0, 2325, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
//        int recupGold = tacheRecupGoldenium.size()+1;
//        tacheRecupGoldenium.add(new Tache("Preparation récupération goldenium", recupGold, 0, Tache.Type.MANIPULATION, null, 15, Tache.Mirror.SPECIFIC));
//        tacheRecupGoldenium3000.add(new Tache("Preparation récupération goldenium", recupGold, 0, Tache.Type.MANIPULATION, null, 19, Tache.Mirror.SPECIFIC));
//        tacheRecupGoldenium.add(new Tache("On avance", tacheRecupGoldenium.size()+1, 220, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY, 1000));
//        tacheRecupGoldenium.add(new Tache("On recule", tacheRecupGoldenium.size()+1, -100, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
//        Objectif objectifRecupGoldenium0 = new Objectif("Recuperation goldenium", objectifsCouleur0.size()+1, 20, 1, tacheRecupGoldenium);
//        Objectif objectifRecupGoldenium3000 = new Objectif("Recuperation goldenium", objectifsCouleur3000.size()+1, 20, 1, null);
//        objectifRecupGoldenium3000.generateMirror(objectifRecupGoldenium0.taches, tacheRecupGoldenium3000);
//        objectifsCouleur0.add(objectifRecupGoldenium0);
//        objectifsCouleur3000.add(objectifRecupGoldenium3000);
//
//        /**
//         * Largage du goldenium = 24 points
//         */
//        List<Tache> tacheLargageGoldenium = new ArrayList<>();
//        int positionGold = tacheLargageGoldenium.size()+1;
//        tacheLargageGoldenium.add(new Tache("Go position", positionGold, 1050, 1280, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        tacheLargageGoldenium.add(new Tache("On s'aligne", tacheLargageGoldenium.size()+1, 2000, 1280, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
//        tacheLargageGoldenium.add(new Tache("On se met en place", tacheLargageGoldenium.size()+1, 1300, 1280, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        tacheLargageGoldenium.add(new Tache("On se met en place", tacheLargageGoldenium.size()+1, 100, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY, 1000));
//        tacheLargageGoldenium.add(new Tache("On largue", tacheLargageGoldenium.size()+1, 0, Tache.Type.MANIPULATION, null, 17, Tache.Mirror.MIRRORY));
//        Objectif objectifLargageGoldenium0 = new Objectif("Largage goldenium", objectifsCouleur0.size()+1, 24, 1, tacheLargageGoldenium);
//        Objectif objectifLargageGoldenium3000 = new Objectif("Largage goldenium", objectifsCouleur3000.size()+1, 24, 1, null);
//        objectifLargageGoldenium3000.generateMirror(objectifLargageGoldenium0.taches);
//        objectifsCouleur0.add(objectifLargageGoldenium0);
//        objectifsCouleur3000.add(objectifLargageGoldenium3000);
//
//        // Création de la stratégie complète
//        Strategie strat = new Strategie();
//        strat.couleur0 = objectifsCouleur0;
//        strat.couleur3000 = objectifsCouleur3000;
//
//        System.out.println(strat.toString());
//
//        final GsonBuilder builder = new GsonBuilder();
//        final Gson gson = builder.create();
//
//        System.out.println("#########################");
//        System.out.println(gson.toJson(strat));
//
//        System.out.println("#########################");
//        try (PrintWriter jsonFile = new PrintWriter("configCollection.json")) {
//            jsonFile.println(gson.toJson(strat));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void main2018(String... arg) {
//        System.out.println("Génération de la stratégie");
//
//        // Liste des objectifs de chaque côté
//        // 0 = Vert, 3000 = Orange
//        List<Objectif> objectifsCouleur0 = new ArrayList<>();
//        List<Objectif> objectifsCouleur3000 = new ArrayList<>();
//
//        /**
//         * Si on sort de la zone de départ, on marque les points du panneau et de l'abeille
//         * Score = pose de l'abeille (5) + pose du panneau (5) = 10
//         */
//        List<Tache> tachesSortieZoneDepart =  new ArrayList<>();
//        tachesSortieZoneDepart.add(new Tache("sortie de la zone de départ vers objetif 1", tachesSortieZoneDepart.size()+1, 1000, 870, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        Objectif objectifSortieZoneDepart0 = new Objectif("Sortie de la zone de départ", objectifsCouleur0.size()+1, 10, 1, tachesSortieZoneDepart);
//        Objectif objectifSortieZoneDepart3000 = new Objectif("Sortie de la zone de départ", objectifsCouleur3000.size()+1, 10, 1, null);
//        try {
//            objectifSortieZoneDepart3000.generateMirror(objectifSortieZoneDepart0.taches);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        objectifsCouleur0.add(objectifSortieZoneDepart0);
//        objectifsCouleur3000.add(objectifSortieZoneDepart3000);
//
//        /**
//         * On se met en position pour pousser les blocs proches de la zone de départ dans la zone de construction
//         * Score = 5
//         */
//        List<Tache> tachesBlocsDepart0 = new ArrayList<>();
//        tachesBlocsDepart0.add(new Tache("go position cube départ", tachesBlocsDepart0.size()+1, 1000, 850, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        tachesBlocsDepart0.add(new Tache("déplacement bloc zone de départ", tachesBlocsDepart0.size()+1, 300, 850, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        tachesBlocsDepart0.add(new Tache("On recule pour se dégager", tachesBlocsDepart0.size()+1, -200, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
//        Objectif objectifBlocsDepart0 = new Objectif("Bloc zone de départ", objectifsCouleur0.size()+1, 5, 1, tachesBlocsDepart0);
//        Objectif objectifBlocsDepart3000 = new Objectif("Bloc zone de départ", objectifsCouleur3000.size()+1, 5, 1, null);
//        try {
//            objectifBlocsDepart3000.generateMirror(objectifBlocsDepart0.taches);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        objectifsCouleur0.add(objectifBlocsDepart0);
//        objectifsCouleur3000.add(objectifBlocsDepart3000);
//
//        /**
//         * On va allumer le panneau domotique
//         * Score = 25
//         */
//        List<Tache> tachesPanneauDomotik0 = new ArrayList<>();
//        tachesPanneauDomotik0.add(new Tache("Go position panneau domotique", tachesPanneauDomotik0.size()+1, 500, 1130, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        tachesPanneauDomotik0.add(new Tache("Alignement panneau domotique", tachesPanneauDomotik0.size()+1, 0, 1130, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
//        tachesPanneauDomotik0.add(new Tache("go position panneau domotique pour action", tachesPanneauDomotik0.size()+1, 200, 1130, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        tachesPanneauDomotik0.add(new Tache("Préparation allumage interrupteur", tachesPanneauDomotik0.size()+1, 0, Tache.Type.MANIPULATION, null, 12, Tache.Mirror.MIRRORY));
//        tachesPanneauDomotik0.add(new Tache("Allumage interrupteur", tachesPanneauDomotik0.size()+1, 0, Tache.Type.MANIPULATION, null, 13, Tache.Mirror.MIRRORY));
//        tachesPanneauDomotik0.add(new Tache("On recule pour se dégager", tachesPanneauDomotik0.size()+1, -200, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
//        Objectif objectifPanneauDomotik0 = new Objectif("Panneau domotik", objectifsCouleur0.size()+1, 25, 1, tachesPanneauDomotik0);
//        Objectif objectifPanneauDomotik3000 = new Objectif("Panneau domotik", objectifsCouleur3000.size()+1, 25, 1, null);
//        try {
//            objectifPanneauDomotik3000.generateMirror(objectifPanneauDomotik0.taches);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        objectifsCouleur0.add(objectifPanneauDomotik0);
//        objectifsCouleur3000.add(objectifPanneauDomotik3000);
//
//        /**
//         * On va pousser l'abeille
//         * Score = 50
//         */
//        List<Tache> tachesAbeille0 = new ArrayList<>();
//        List<Tache> tachesAbeille3000 = new ArrayList<>();
//        tachesAbeille0.add(new Tache("Go position séquence abeille", tachesAbeille0.size()+1, 1800, 219, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        tachesAbeille0.add(new Tache("Alignement pour lancement abeille", tachesAbeille0.size()+1, 1800, 3000, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
//        tachesAbeille0.add(new Tache("Mise en position lancement abeille", tachesAbeille0.size()+1, 1800, 124, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO_BACK, -1, Tache.Mirror.MIRRORY));
//        int idAbaisseBras = tachesAbeille0.size()+1;
//        tachesAbeille0.add(new Tache("Abaissement bras", idAbaisseBras, 0, Tache.Type.MANIPULATION, null, 1, Tache.Mirror.SPECIFIC));
//        tachesAbeille3000.add(new Tache("Abaissement bras", idAbaisseBras, 0, Tache.Type.MANIPULATION, null, 3, Tache.Mirror.SPECIFIC));
//        tachesAbeille0.add(new Tache("Lancement abeille", tachesAbeille0.size()+1, 1800, 334, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        int idRentrerBras = tachesAbeille0.size()+1;
//        tachesAbeille0.add(new Tache("Rentrer bras", idRentrerBras, 0, Tache.Type.MANIPULATION, null, 0, Tache.Mirror.SPECIFIC));
//        tachesAbeille3000.add(new Tache("Rentrer bras", idRentrerBras, 0, Tache.Type.MANIPULATION, null, 2, Tache.Mirror.SPECIFIC));
//        Objectif objectifAbeille0 = new Objectif("Abeille", objectifsCouleur0.size()+1, 50, 1, tachesAbeille0);
//        Objectif objectifAbeille3000 = new Objectif("Abeille", objectifsCouleur3000.size()+1, 50, 1, null);
//        try {
//            objectifAbeille3000.generateMirror(objectifAbeille0.taches, tachesAbeille3000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        objectifsCouleur0.add(objectifAbeille0);
//        objectifsCouleur3000.add(objectifAbeille3000);
//
//        /**
//         * On ramène les cubes proches du réservoir d'eau dans la zone de construction
//         * Score = 5 ==> trop chiant
//         */
////        List<Tache> tachesCubesReservoir0 = new ArrayList<>();
////        tachesCubesReservoir0.add(new Tache("Go position cubes réservoir", tachesCubesReservoir0.size()+1, 1450, 180, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
////        tachesCubesReservoir0.add(new Tache("Go sur les cubes réservoir", tachesCubesReservoir0.size()+1, 600, 600, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
////        tachesCubesReservoir0.add(new Tache("Alignement pour déposer les cubes réservoir", tachesCubesReservoir0.size()+1, 0, 600, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
////        tachesCubesReservoir0.add(new Tache("Déposer des cubes réservoir", tachesCubesReservoir0.size()+1, 270, 600, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
////        tachesCubesReservoir0.add(new Tache("On se dégage", tachesCubesReservoir0.size()+1, -200, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
////        Objectif objectifCubesReservoir0 = new Objectif("Cubes réservoir", objectifsCouleur0.size()+1, 5, 1, tachesCubesReservoir0);
////        Objectif objectifCubesReservoir3000 = new Objectif("Cubes réservoir", objectifsCouleur0.size()+1, 5, 1, null);
////        try {
////            objectifCubesReservoir3000.generateMirror(objectifCubesReservoir0.taches);
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////        objectifsCouleur0.add(objectifCubesReservoir0);
////        objectifsCouleur3000.add(objectifCubesReservoir3000);
//
//        /**
//         * On va récupérer l'eau propre et l'éjecter
//         * Score = récupérer une balle (10) + 8 * une balle dans le chateau d'eau (8*5) = 50 - On paris 7
//         */
//        List<Tache> tachesEauPropre0 = new ArrayList<>();
//        List<Tache> tachesEauPropre3000 = new ArrayList<>();
//        int eauX = 850;
//        tachesEauPropre0.add(new Tache("Esquive cube de merde", tachesEauPropre0.size()+1, 1100, 630, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        tachesEauPropre0.add(new Tache("Go position Eau propre", tachesEauPropre0.size()+1, eauX, 400, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        tachesEauPropre0.add(new Tache("Alignement Eau propre", tachesEauPropre0.size()+1, eauX, 0, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
//        tachesEauPropre0.add(new Tache("Préparation remplissage", tachesEauPropre0.size()+1, 0, Tache.Type.MANIPULATION, null, 10, Tache.Mirror.MIRRORY));
//
//        int id = tachesEauPropre0.size()+1;
//        tachesEauPropre0.add(new Tache("Mise en position pour récupération Eau propre", id, eauX, 225, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.SPECIFIC));
//        tachesEauPropre3000.add(new Tache("Mise en position pour récupération Eau propre", id, eauX, 3000 - 215, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.SPECIFIC));
//
//        tachesEauPropre0.add(new Tache("Remplissage", tachesEauPropre0.size()+1, 0, Tache.Type.MANIPULATION, null, 9, Tache.Mirror.MIRRORY));
//        tachesEauPropre0.add(new Tache("On se dégage", tachesEauPropre0.size()+1, -200, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
//        tachesEauPropre0.add(new Tache("Remplissage rangement", tachesEauPropre0.size()+1, 0, Tache.Type.MANIPULATION, null, 11, Tache.Mirror.MIRRORY));
//        tachesEauPropre0.add(new Tache("Go pré-position lancement Eau propre", tachesEauPropre0.size()+1, 200, 1130, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        tachesEauPropre0.add(new Tache("Go pré-position lancement Eau propre", tachesEauPropre0.size()+1, 200, 750, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//
//        id = tachesEauPropre0.size()+1;
//        tachesEauPropre0.add(new Tache("Go position lancement Eau propre", id, 220, 930, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO_BACK, -1, Tache.Mirror.MIRRORY));
//        id = tachesEauPropre0.size()+1;
//        tachesEauPropre0.add(new Tache("Alignement lancement Eau propre", id, 720, 1793, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.SPECIFIC));
//        tachesEauPropre3000.add(new Tache("Alignement lancement Eau propre", id, 2000, 130, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.SPECIFIC));
//
//        tachesEauPropre0.add(new Tache("Lancement de l'eau propre", tachesEauPropre0.size()+1, 0, Tache.Type.MANIPULATION, null, 4, Tache.Mirror.MIRRORY));
//
//        Objectif objectifEauPropre0 = new Objectif("Eau propre", objectifsCouleur0.size()+1, 45, 1, tachesEauPropre0);
//        Objectif objectifEauPropre3000 = new Objectif("Eau propre", objectifsCouleur0.size()+1, 45, 1, null);
//        try {
//            objectifEauPropre3000.generateMirror(objectifEauPropre0.taches, tachesEauPropre3000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        objectifsCouleur0.add(objectifEauPropre0);
//        objectifsCouleur3000.add(objectifEauPropre3000);

        // Création de la stratégie complète
        Strategie strat = new Strategie();
        strat.couleur0 = objectifsCouleur0;
        strat.couleur3000 = objectifsCouleur3000;

        System.out.println(strat.toString());

        final GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();

        System.out.println("#########################");
        System.out.println(gson.toJson(strat));

        try (PrintWriter jsonFile = new PrintWriter("configCollection.json")) {
            jsonFile.println(gson.toJson(strat));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}
