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
public class Main {

    public static void main(String... arg) {
        System.out.println("Génération de la stratégie");

        // Liste des objectifs de chaque côté
        // 0 = Jaune, 3000 = Violet
        List<Objectif> objectifsCouleur0 = new ArrayList<>();
        List<Objectif> objectifsCouleur3000 = new ArrayList<>();

        /**
         * Si on sort de la zone de départ, on marque les points de l'expérience complète (démarrage à distance au SRF08)
         * Score = pose du l'expérience (5) + allumage (15) + électron (20) = 40
         */
        List<Tache> tachesSortieZoneDepart =  new ArrayList<>();
        tachesSortieZoneDepart.add(new Tache("sortie de la zone de départ vers petit distributeur", tachesSortieZoneDepart.size()+1, 1880, 225, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesSortieZoneDepart.add(new Tache("alignement petit distributeur", tachesSortieZoneDepart.size()+1, 2000, 225, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
        // todo action de récupération + manoeuvres
        Objectif objectifSortieZoneDepart0 = new Objectif("Récupération petit distributeur", objectifsCouleur0.size()+1, 40, 1, tachesSortieZoneDepart);
        Objectif objectifSortieZoneDepart3000 = new Objectif("Récupération petit distributeur", objectifsCouleur3000.size()+1, 40, 1, null);
        try {
            objectifSortieZoneDepart3000.generateMirror(objectifSortieZoneDepart0.taches);
        } catch (Exception e) {
            e.printStackTrace();
        }
        objectifsCouleur0.add(objectifSortieZoneDepart0);
        objectifsCouleur3000.add(objectifSortieZoneDepart3000);

        /**
         * On évacue la zone de chaos
         * 5 palets dont 1 de la bonne couleur = 1 * 5 + 5 = 10
         */
//        List<Tache> tachesChaos0 = new ArrayList<>();
//        List<Tache> tachesChaos3000 = new ArrayList<>();
//        tachesChaos0.add(new Tache("Go position séquence chaos", tachesChaos0.size()+1, 300, 1400, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        tachesChaos0.add(new Tache("Go position séquence chaos 2", tachesChaos0.size()+1, 1050, 1400, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        tachesChaos0.add(new Tache("Alignement avec le chaos", tachesChaos0.size()+1, 1050, 0, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
//        tachesChaos0.add(new Tache("On pousse la chaos", tachesChaos0.size()+1, 1050, 400, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        Objectif objectifChaos0 = new Objectif("Zone de chaos", objectifsCouleur0.size()+1, 10, 1, tachesChaos0);
//        Objectif objectifChaos3000 = new Objectif("Zone de chaos", objectifsCouleur3000.size()+1, 10, 1, null);
//        try {
//            objectifChaos3000.generateMirror(objectifChaos0.taches, tachesChaos3000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        objectifsCouleur0.add(objectifChaos0);
//        objectifsCouleur3000.add(objectifChaos3000);

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

    public static void main2018(String... arg) {
        System.out.println("Génération de la stratégie");

        // Liste des objectifs de chaque côté
        // 0 = Vert, 3000 = Orange
        List<Objectif> objectifsCouleur0 = new ArrayList<>();
        List<Objectif> objectifsCouleur3000 = new ArrayList<>();

        /**
         * Si on sort de la zone de départ, on marque les points du panneau et de l'abeille
         * Score = pose de l'abeille (5) + pose du panneau (5) = 10
         */
        List<Tache> tachesSortieZoneDepart =  new ArrayList<>();
        tachesSortieZoneDepart.add(new Tache("sortie de la zone de départ vers objetif 1", tachesSortieZoneDepart.size()+1, 1000, 870, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        Objectif objectifSortieZoneDepart0 = new Objectif("Sortie de la zone de départ", objectifsCouleur0.size()+1, 10, 1, tachesSortieZoneDepart);
        Objectif objectifSortieZoneDepart3000 = new Objectif("Sortie de la zone de départ", objectifsCouleur3000.size()+1, 10, 1, null);
        try {
            objectifSortieZoneDepart3000.generateMirror(objectifSortieZoneDepart0.taches);
        } catch (Exception e) {
            e.printStackTrace();
        }
        objectifsCouleur0.add(objectifSortieZoneDepart0);
        objectifsCouleur3000.add(objectifSortieZoneDepart3000);

        /**
         * On se met en position pour pousser les blocs proches de la zone de départ dans la zone de construction
         * Score = 5
         */
        List<Tache> tachesBlocsDepart0 = new ArrayList<>();
        tachesBlocsDepart0.add(new Tache("go position cube départ", tachesBlocsDepart0.size()+1, 1000, 850, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesBlocsDepart0.add(new Tache("déplacement bloc zone de départ", tachesBlocsDepart0.size()+1, 300, 850, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesBlocsDepart0.add(new Tache("On recule pour se dégager", tachesBlocsDepart0.size()+1, -200, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
        Objectif objectifBlocsDepart0 = new Objectif("Bloc zone de départ", objectifsCouleur0.size()+1, 5, 1, tachesBlocsDepart0);
        Objectif objectifBlocsDepart3000 = new Objectif("Bloc zone de départ", objectifsCouleur3000.size()+1, 5, 1, null);
        try {
            objectifBlocsDepart3000.generateMirror(objectifBlocsDepart0.taches);
        } catch (Exception e) {
            e.printStackTrace();
        }
        objectifsCouleur0.add(objectifBlocsDepart0);
        objectifsCouleur3000.add(objectifBlocsDepart3000);

        /**
         * On va allumer le panneau domotique
         * Score = 25
         */
        List<Tache> tachesPanneauDomotik0 = new ArrayList<>();
        tachesPanneauDomotik0.add(new Tache("Go position panneau domotique", tachesPanneauDomotik0.size()+1, 500, 1130, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesPanneauDomotik0.add(new Tache("Alignement panneau domotique", tachesPanneauDomotik0.size()+1, 0, 1130, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
        tachesPanneauDomotik0.add(new Tache("go position panneau domotique pour action", tachesPanneauDomotik0.size()+1, 200, 1130, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesPanneauDomotik0.add(new Tache("Préparation allumage interrupteur", tachesPanneauDomotik0.size()+1, 0, Tache.Type.MANIPULATION, null, 12, Tache.Mirror.MIRRORY));
        tachesPanneauDomotik0.add(new Tache("Allumage interrupteur", tachesPanneauDomotik0.size()+1, 0, Tache.Type.MANIPULATION, null, 13, Tache.Mirror.MIRRORY));
        tachesPanneauDomotik0.add(new Tache("On recule pour se dégager", tachesPanneauDomotik0.size()+1, -200, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
        Objectif objectifPanneauDomotik0 = new Objectif("Panneau domotik", objectifsCouleur0.size()+1, 25, 1, tachesPanneauDomotik0);
        Objectif objectifPanneauDomotik3000 = new Objectif("Panneau domotik", objectifsCouleur3000.size()+1, 25, 1, null);
        try {
            objectifPanneauDomotik3000.generateMirror(objectifPanneauDomotik0.taches);
        } catch (Exception e) {
            e.printStackTrace();
        }
        objectifsCouleur0.add(objectifPanneauDomotik0);
        objectifsCouleur3000.add(objectifPanneauDomotik3000);

        /**
         * On va pousser l'abeille
         * Score = 50
         */
        List<Tache> tachesAbeille0 = new ArrayList<>();
        List<Tache> tachesAbeille3000 = new ArrayList<>();
        tachesAbeille0.add(new Tache("Go position séquence abeille", tachesAbeille0.size()+1, 1800, 219, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesAbeille0.add(new Tache("Alignement pour lancement abeille", tachesAbeille0.size()+1, 1800, 3000, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
        tachesAbeille0.add(new Tache("Mise en position lancement abeille", tachesAbeille0.size()+1, 1800, 124, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO_BACK, -1, Tache.Mirror.MIRRORY));
        int idAbaisseBras = tachesAbeille0.size()+1;
        tachesAbeille0.add(new Tache("Abaissement bras", idAbaisseBras, 0, Tache.Type.MANIPULATION, null, 1, Tache.Mirror.SPECIFIC));
        tachesAbeille3000.add(new Tache("Abaissement bras", idAbaisseBras, 0, Tache.Type.MANIPULATION, null, 3, Tache.Mirror.SPECIFIC));
        tachesAbeille0.add(new Tache("Lancement abeille", tachesAbeille0.size()+1, 1800, 334, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        int idRentrerBras = tachesAbeille0.size()+1;
        tachesAbeille0.add(new Tache("Rentrer bras", idRentrerBras, 0, Tache.Type.MANIPULATION, null, 0, Tache.Mirror.SPECIFIC));
        tachesAbeille3000.add(new Tache("Rentrer bras", idRentrerBras, 0, Tache.Type.MANIPULATION, null, 2, Tache.Mirror.SPECIFIC));
        Objectif objectifAbeille0 = new Objectif("Abeille", objectifsCouleur0.size()+1, 50, 1, tachesAbeille0);
        Objectif objectifAbeille3000 = new Objectif("Abeille", objectifsCouleur3000.size()+1, 50, 1, null);
        try {
            objectifAbeille3000.generateMirror(objectifAbeille0.taches, tachesAbeille3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        objectifsCouleur0.add(objectifAbeille0);
        objectifsCouleur3000.add(objectifAbeille3000);

        /**
         * On ramène les cubes proches du réservoir d'eau dans la zone de construction
         * Score = 5 ==> trop chiant
         */
//        List<Tache> tachesCubesReservoir0 = new ArrayList<>();
//        tachesCubesReservoir0.add(new Tache("Go position cubes réservoir", tachesCubesReservoir0.size()+1, 1450, 180, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        tachesCubesReservoir0.add(new Tache("Go sur les cubes réservoir", tachesCubesReservoir0.size()+1, 600, 600, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        tachesCubesReservoir0.add(new Tache("Alignement pour déposer les cubes réservoir", tachesCubesReservoir0.size()+1, 0, 600, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
//        tachesCubesReservoir0.add(new Tache("Déposer des cubes réservoir", tachesCubesReservoir0.size()+1, 270, 600, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        tachesCubesReservoir0.add(new Tache("On se dégage", tachesCubesReservoir0.size()+1, -200, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
//        Objectif objectifCubesReservoir0 = new Objectif("Cubes réservoir", objectifsCouleur0.size()+1, 5, 1, tachesCubesReservoir0);
//        Objectif objectifCubesReservoir3000 = new Objectif("Cubes réservoir", objectifsCouleur0.size()+1, 5, 1, null);
//        try {
//            objectifCubesReservoir3000.generateMirror(objectifCubesReservoir0.taches);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        objectifsCouleur0.add(objectifCubesReservoir0);
//        objectifsCouleur3000.add(objectifCubesReservoir3000);

        /**
         * On va récupérer l'eau propre et l'éjecter
         * Score = récupérer une balle (10) + 8 * une balle dans le chateau d'eau (8*5) = 50 - On paris 7
         */
        List<Tache> tachesEauPropre0 = new ArrayList<>();
        List<Tache> tachesEauPropre3000 = new ArrayList<>();
        int eauX = 850;
        tachesEauPropre0.add(new Tache("Esquive cube de merde", tachesEauPropre0.size()+1, 1100, 630, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesEauPropre0.add(new Tache("Go position Eau propre", tachesEauPropre0.size()+1, eauX, 400, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesEauPropre0.add(new Tache("Alignement Eau propre", tachesEauPropre0.size()+1, eauX, 0, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
        tachesEauPropre0.add(new Tache("Préparation remplissage", tachesEauPropre0.size()+1, 0, Tache.Type.MANIPULATION, null, 10, Tache.Mirror.MIRRORY));

        int id = tachesEauPropre0.size()+1;
        tachesEauPropre0.add(new Tache("Mise en position pour récupération Eau propre", id, eauX, 225, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.SPECIFIC));
        tachesEauPropre3000.add(new Tache("Mise en position pour récupération Eau propre", id, eauX, 3000 - 215, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.SPECIFIC));

        tachesEauPropre0.add(new Tache("Remplissage", tachesEauPropre0.size()+1, 0, Tache.Type.MANIPULATION, null, 9, Tache.Mirror.MIRRORY));
        tachesEauPropre0.add(new Tache("On se dégage", tachesEauPropre0.size()+1, -200, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY));
        tachesEauPropre0.add(new Tache("Remplissage rangement", tachesEauPropre0.size()+1, 0, Tache.Type.MANIPULATION, null, 11, Tache.Mirror.MIRRORY));
        tachesEauPropre0.add(new Tache("Go pré-position lancement Eau propre", tachesEauPropre0.size()+1, 200, 1130, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tachesEauPropre0.add(new Tache("Go pré-position lancement Eau propre", tachesEauPropre0.size()+1, 200, 750, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));

        id = tachesEauPropre0.size()+1;
        tachesEauPropre0.add(new Tache("Go position lancement Eau propre", id, 220, 930, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO_BACK, -1, Tache.Mirror.MIRRORY));
        id = tachesEauPropre0.size()+1;
        tachesEauPropre0.add(new Tache("Alignement lancement Eau propre", id, 720, 1793, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.SPECIFIC));
        tachesEauPropre3000.add(new Tache("Alignement lancement Eau propre", id, 2000, 130, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.SPECIFIC));

        tachesEauPropre0.add(new Tache("Lancement de l'eau propre", tachesEauPropre0.size()+1, 0, Tache.Type.MANIPULATION, null, 4, Tache.Mirror.MIRRORY));

        Objectif objectifEauPropre0 = new Objectif("Eau propre", objectifsCouleur0.size()+1, 45, 1, tachesEauPropre0);
        Objectif objectifEauPropre3000 = new Objectif("Eau propre", objectifsCouleur0.size()+1, 45, 1, null);
        try {
            objectifEauPropre3000.generateMirror(objectifEauPropre0.taches, tachesEauPropre3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        objectifsCouleur0.add(objectifEauPropre0);
        objectifsCouleur3000.add(objectifEauPropre3000);

        /**
         * On va récupérer et larguer l'eau random
         * Score = récupérer une balle (10) + 4 * une balle adverse dans la station d'épuration (4*10) = 50
         */
//        List<Tache> tachesEauRandom0 = new ArrayList<>();
//        tachesEauRandom0.add(new Tache("Go position Eau random", tachesEauRandom0.size()+1, 1700, 610, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        tachesEauRandom0.add(new Tache("Alignement Eau random", tachesEauRandom0.size()+1, 2000, 610, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
//        tachesEauRandom0.add(new Tache("Mise en position pour récupération Eau random", tachesEauRandom0.size()+1, 1760, 610, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        // todo action de récupération
//        tachesEauRandom0.add(new Tache("Go position station d'épuration", tachesEauRandom0.size()+1, 1550, 900, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
//        tachesEauRandom0.add(new Tache("Alignement station d'épuration", tachesEauRandom0.size()+1, 1550, 3000, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
//        // todo largage station d'épuration


//        Tache tache1 = new Tache("go position cube départ", 1, 1000, 850, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY);
//        Tache tache2 = new Tache("déplacement bloc zone de départ", 2, 300, 850, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY);
//        Tache tache3 = new Tache("specific", 3, 420, 1000, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.SPECIFIC);
//        Tache tache4 = new Tache("no mirror", 4, 1000, 1000, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.NONE);
//        List<Tache> tachesBlocsDepart = new ArrayList<>();
//        tachesBlocsDepart.add(tache1);
//        tachesBlocsDepart.add(tache2);
//        tachesO1C0.add(tache3);
//        tachesO1C0.add(tache4);
//        Objectif objectifBlocsDepart0 = new Objectif("Bloc zone de départ", 1, 5, 1, tachesBlocsDepart);
//        objectifsCouleur0.add(objectifBlocsDepart0);

//        Objectif objectifBlocsDepart3000 = new Objectif("Bloc zone de départ", 1, 5, 1, null);
//        Tache tache3Couleur3000 = new Tache("specific", 3, 420, 500, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.SPECIFIC);
//        List<Tache> tachesSpecific = new ArrayList<>();
//        tachesSpecific.add(tache3Couleur3000);

//        try {
////            objectifBlocsDepart3000.generateMirror(objectifBlocsDepart0.taches, tachesSpecific);
//            objectifBlocsDepart3000.generateMirror(objectifBlocsDepart0.taches);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        objectifsCouleur3000.add(objectifBlocsDepart3000);

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
