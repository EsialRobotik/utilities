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

    public static void mainStrat(String... arg) throws Exception {
//    public static void main(String... arg) throws Exception {
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
        tachesBoueeNord.add(new Tache("Marquage bouée Nord", tachesBoueeNord.size()+1, 610, 170, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesBoueeNord.add(new Tache("Libération bouée Nord", tachesBoueeNord.size()+1, 610, 670, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO_BACK, -1, Tache.Mirror.MIRRORY));
        tachesBoueeNord.add(new Tache("Placement bouée Nord à virer", tachesBoueeNord.size()+1, 395, 542, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesBoueeNord.add(new Tache("Placement bouée Nord à virer", tachesBoueeNord.size()+1, 180, 415, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesBoueeNord.add(new Tache("Placement bouée Nord à virer", tachesBoueeNord.size()+1, 0, 415, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
        tachesBoueeNord.add(new Tache("Marquage bouée Nord à virer", tachesBoueeNord.size()+1, -250, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
        tachesBoueeNord.add(new Tache("Libération bouée Nord à virer", tachesBoueeNord.size()+1, 150, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
        tachesBoueeNord.add(new Tache("Placement phare", tachesBoueeNord.size()+1, 280, 225, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesBoueeNord.add(new Tache("Alignement phare", tachesBoueeNord.size()+1, 0, 225, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
        tachesBoueeNord.add(new Tache("Preparation phare", tachesBoueeNord.size()+1, 0, Tache.Type.MANIPULATION, null, 11, Tache.Mirror.MIRRORY));
        tachesBoueeNord.add(new Tache("Allumage phare", tachesBoueeNord.size()+1, 140, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY, 1500));
        tachesBoueeNord.add(new Tache("Sortie zone N", tachesBoueeNord.size()+1, -100, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
        tachesBoueeNord.add(new Tache("Sortie zone N", tachesBoueeNord.size()+1, 610, 670, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
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
        tachesBoueeSud.add(new Tache("Marquage bouée Sud", tachesBoueeSud.size()+1, 980, 170, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesBoueeSud.add(new Tache("Libération bouée Sud", tachesBoueeSud.size()+1, 980, 670, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO_BACK, -1, Tache.Mirror.MIRRORY));
        tachesBoueeSud.add(new Tache("Placement bouée Sud à virer", tachesBoueeSud.size()+1, 1500, 670, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesBoueeSud.add(new Tache("Placement bouée Sud à virer", tachesBoueeSud.size()+1, 1500, 300, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesBoueeSud.add(new Tache("Placement bouée Sud à virer", tachesBoueeSud.size()+1, 0, 300, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
        tachesBoueeSud.add(new Tache("Marquage bouée Sud à virer", tachesBoueeSud.size()+1, 250, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
        tachesBoueeSud.add(new Tache("Marquage bouée Sud à virer", tachesBoueeSud.size()+1, 140, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
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
        List<Tache> tachesManches3000 =  new ArrayList<>();
        tachesManches.add(new Tache("Placement manche à air", tachesManches.size()+1, 1780, 210, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesManches.add(new Tache("Alignement manche à air", tachesManches.size()+1, 1780, 3000, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
        tachesManches.add(new Tache("Alignement manche à air", tachesManches.size()+1, -80, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
        int sortieBras = tachesManches.size()+1;
        tachesManches.add(new Tache("Sortie bras droit", sortieBras, 0, Tache.Type.MANIPULATION, null, 1, Tache.Mirror.SPECIFIC));
        tachesManches3000.add(new Tache("Sortie bras gauche", sortieBras, 0, Tache.Type.MANIPULATION, null, 2, Tache.Mirror.SPECIFIC));
        tachesManches.add(new Tache("Réduction de la vitesse", tachesManches.size()+1, 50, Tache.Type.DEPLACEMENT, Tache.SubType.SET_SPEED, -1, Tache.Mirror.MIRRORY));
        tachesManches.add(new Tache("Taper les manches", tachesManches.size()+1, 1780, 700, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesManches.add(new Tache("Vitess normale", tachesManches.size()+1, 100, Tache.Type.DEPLACEMENT, Tache.SubType.SET_SPEED, -1, Tache.Mirror.MIRRORY));
        int rentrerBras = tachesManches.size()+1;
        tachesManches.add(new Tache("Rentrer bras droit", rentrerBras, 0, Tache.Type.MANIPULATION, null, 3, Tache.Mirror.SPECIFIC));
        tachesManches3000.add(new Tache("Rentrer bras gauche", rentrerBras, 0, Tache.Type.MANIPULATION, null, 4, Tache.Mirror.SPECIFIC));
        Objectif objectifManches0 = new Objectif("Manches à air", objectifsCouleur0.size()+1, score, 1, tachesManches);
        Objectif objectifManches3000 = new Objectif("Manches à air", objectifsCouleur3000.size()+1, score, 1, null);
        try {
            objectifManches3000.generateMirror(objectifManches0.taches, tachesManches3000);
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
        tachesRecifS.add(new Tache("Preparer ramassage recif sud", tachesRecifS.size()+1, 0, Tache.Type.MANIPULATION, null, 5, Tache.Mirror.MIRRORY));
        tachesRecifS.add(new Tache("Mise en position rammassage recif sud", tachesRecifS.size()+1, 1600, 100, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesRecifS.add(new Tache("Mise en position rammassage recif sud", tachesRecifS.size()+1, 100, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY, 500));
        tachesRecifS.add(new Tache("Ramassage recif sud", tachesRecifS.size()+1, 0, Tache.Type.MANIPULATION, null, 6, Tache.Mirror.MIRRORY));
        tachesRecifS.add(new Tache("Libération ramassage recif sud", tachesRecifS.size()+1, 0, Tache.Type.MANIPULATION, null, 7, Tache.Mirror.MIRRORY));
        tachesRecifS.add(new Tache("Sortie recif sud", tachesRecifS.size()+1, 1600, 230, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO_BACK, -1, Tache.Mirror.MIRRORY));
        tachesRecifS.add(new Tache("Placement largage sud", tachesRecifS.size()+1, 985, 200, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesRecifS.add(new Tache("Alignement largage sud", tachesRecifS.size()+1, 0, 200, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
        tachesRecifS.add(new Tache("Préparer largage recif sud", tachesRecifS.size()+1, 0, Tache.Type.MANIPULATION, null, 8, Tache.Mirror.MIRRORY));
        tachesRecifS.add(new Tache("Largage impaire recif sud", tachesRecifS.size()+1, 0, Tache.Type.MANIPULATION, null, 9, Tache.Mirror.MIRRORY));
        tachesRecifS.add(new Tache("Sortie largage sud", tachesRecifS.size()+1, -200, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
        tachesRecifS.add(new Tache("Placement recif nord", tachesRecifS.size()+1, 1200, 670, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesRecifS.add(new Tache("Placement recif nord", tachesRecifS.size()+1, 415, 670, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesRecifS.add(new Tache("Placement recif nord", tachesRecifS.size()+1, 415, 200, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesRecifS.add(new Tache("Alignement recif nord", tachesRecifS.size()+1, 2000, 200, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
        tachesRecifS.add(new Tache("Largage impaire recif sud", tachesRecifS.size()+1, 0, Tache.Type.MANIPULATION, null, 10, Tache.Mirror.MIRRORY));
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

        /**
         * On prend en photo la boussole
         * Score = 0
         */
        score = 0;
        List<Tache> tachesBoussole =  new ArrayList<>();
        tachesBoussole.add(new Tache("Placement boussole", tachesBoussole.size()+1, 800, 1000, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesBoussole.add(new Tache("Alignement boussole", tachesBoussole.size()+1, 0, 1500, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
        tachesBoussole.add(new Tache("Photo", tachesBoussole.size()+1, 0, Tache.Type.MANIPULATION, null, 12, Tache.Mirror.MIRRORY));
        Objectif objectifBoussole0 = new Objectif("Boussole", objectifsCouleur0.size()+1, score, 1, tachesBoussole);
        Objectif objectifBoussole3000 = new Objectif("Boussole", objectifsCouleur3000.size()+1, score, 1, null);
        try {
            objectifBoussole3000.generateMirror(objectifBoussole0.taches);
        } catch (Exception e) {
            e.printStackTrace();
        }
        objectifsCouleur0.add(objectifBoussole0);
        objectifsCouleur3000.add(objectifBoussole3000);

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

//    public static void mainBoussole(String... arg) throws Exception {
    public static void main(String... arg) throws Exception {
        System.out.println("Génération de l'action pour arriver à bon port");

        // Liste des objectifs de chaque côté
        // 0 = Bleu, 3000 = Jaune
        List<Objectif> objectifsCouleur0 = new ArrayList<>();
        List<Objectif> objectifsCouleur3000 = new ArrayList<>();

        /**
         * On se positionne entre les deux port et on va dans le bon
         * Score = 10
         */
        int score = 10;
        List<Tache> tachesPortNord =  new ArrayList<>();
        tachesPortNord.add(new Tache("Placement entre les ports", tachesPortNord.size()+1, 800, 670, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesPortNord.add(new Tache("On va au nord", tachesPortNord.size()+1, 300, 670, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesPortNord.add(new Tache("On se gare", tachesPortNord.size()+1, 300, 120, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        Objectif objectifPortN0 = new Objectif("Port Nord", objectifsCouleur0.size()+1, score, 1, tachesPortNord);
        Objectif objectifPortN3000 = new Objectif("Port Nord", objectifsCouleur3000.size()+1, score, 1, null);
        try {
            objectifPortN3000.generateMirror(objectifPortN0.taches);
        } catch (Exception e) {
            e.printStackTrace();
        }
        objectifsCouleur0.add(objectifPortN0);
        objectifsCouleur3000.add(objectifPortN3000);

        /**
         * On se positionne entre les deux port et on va dans le bon
         * Score = 10
         */
        score = 10;
        List<Tache> tachesPortSud =  new ArrayList<>();
        tachesPortSud.add(new Tache("Placement entre les ports", tachesPortSud.size()+1, 800, 670, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesPortSud.add(new Tache("On va au sud", tachesPortSud.size()+1, 1300, 670, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesPortSud.add(new Tache("On se gare", tachesPortSud.size()+1, 1300, 120, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        Objectif objectifPortS0 = new Objectif("Port Sud", objectifsCouleur0.size()+1, score, 1, tachesPortNord);
        Objectif objectifPortS3000 = new Objectif("Port Sud", objectifsCouleur3000.size()+1, score, 1, null);
        try {
            objectifPortS3000.generateMirror(objectifPortS0.taches);
        } catch (Exception e) {
            e.printStackTrace();
        }
        objectifsCouleur0.add(objectifPortS0);
        objectifsCouleur3000.add(objectifPortS3000);

        // Création de la stratégie complète
        Strategie strat = new Strategie();
        strat.couleur0 = objectifsCouleur0;
        strat.couleur3000 = objectifsCouleur3000;

        System.out.println(strat.toString());

        final GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();

        System.out.println("#########################");
        System.out.println(gson.toJson(strat));

        try (PrintWriter jsonFile = new PrintWriter("configCollectionBoussole.json")) {
            jsonFile.println(gson.toJson(strat));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
