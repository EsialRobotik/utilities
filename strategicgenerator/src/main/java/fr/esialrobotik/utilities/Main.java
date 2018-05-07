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
        // 0 = Vert, 3000 = Orange
        List<Objectif> objectifsCouleur0 = new ArrayList<>();
        List<Objectif> objectifsCouleur3000 = new ArrayList<>();

        /**
         * Si on sort de la zone de départ, on marque les points du panneau et de l'abeille
         * Score = pose de l'abeille (5) + pose du panneau (5) = 10
         */
        List<Tache> tachesSortieZoneDepart =  new ArrayList<>();
        tachesSortieZoneDepart.add(new Tache("sortie de la zone de départ vers objetif 1", tachesSortieZoneDepart.size()+1, 1000, 850, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
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
        List<Tache> tacheAbeille0 = new ArrayList<>();
        tacheAbeille0.add(new Tache("Go position séquence abeille", tacheAbeille0.size()+1, 1788, 219, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        tacheAbeille0.add(new Tache("Alignement pour lancement abeille", tacheAbeille0.size()+1, 1788, 3000, Tache.Type.DEPLACEMENT, Tache.SubType.FACE, -1, Tache.Mirror.MIRRORY));
        tacheAbeille0.add(new Tache("Mise en position lancement abeille", tacheAbeille0.size()+1, 1788, 134, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO_BACK, -1, Tache.Mirror.MIRRORY));
        // todo sortir le bras
        tacheAbeille0.add(new Tache("Lancement abeille", tacheAbeille0.size()+1, 1788, 334, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY));
        // todo rentrer le bras
        Objectif objectifAbeille0 = new Objectif("Abeille", objectifsCouleur0.size()+1, 50, 1, tacheAbeille0);
        Objectif objectifAbeille3000 = new Objectif("Abeille", objectifsCouleur3000.size()+1, 50, 1, null);
        try {
            objectifAbeille3000.generateMirror(objectifAbeille0.taches);
        } catch (Exception e) {
            e.printStackTrace();
        }
        objectifsCouleur0.add(objectifAbeille0);
        objectifsCouleur3000.add(objectifAbeille3000);

        /**
         * On ramène les cubes proches du réservoir d'eau dans la zone de construction
         * Score = 5
         */

        /**
         * On va récupérer l'eau propre et l'éjecter
         * Score = récupérer une balle (10) + 8 * une balle dans le chateau d'eau (8*5) = 50
         */

        /**
         * On va récupérer et larguer l'eau random
         * Score = récupérer une balle (10) + 4 * une balle adverse dans la station d'épuration (4*10) = 50
         */


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
