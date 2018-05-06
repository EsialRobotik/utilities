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

        Tache tache1 = new Tache("go position cube départ", 1, 1000, 850, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY);
        Tache tache2 = new Tache("déplacement bloc zone de départ", 2, 300, 850, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY);
//        Tache tache3 = new Tache("specific", 3, 420, 1000, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.SPECIFIC);
//        Tache tache4 = new Tache("no mirror", 4, 1000, 1000, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.NONE);
        List<Tache> tachesBlocsDepart = new ArrayList<>();
        tachesBlocsDepart.add(tache1);
        tachesBlocsDepart.add(tache2);
//        tachesO1C0.add(tache3);
//        tachesO1C0.add(tache4);

        Objectif objectifBlocsDepart0 = new Objectif("Bloc zone de départ", 1, 5, 1, tachesBlocsDepart);

        List<Objectif> objectifsCouleur0 = new ArrayList<>();
        objectifsCouleur0.add(objectifBlocsDepart0);

        Objectif objectifBlocsDepart3000 = new Objectif("Bloc zone de départ", 1, 5, 1, null);
//        Tache tache3Couleur3000 = new Tache("specific", 3, 420, 500, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.SPECIFIC);
//        List<Tache> tachesSpecific = new ArrayList<>();
//        tachesSpecific.add(tache3Couleur3000);

        try {
//            objectifBlocsDepart3000.generateMirror(objectifBlocsDepart0.taches, tachesSpecific);
            objectifBlocsDepart3000.generateMirror(objectifBlocsDepart0.taches);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Objectif> objectifsCouleur3000 = new ArrayList<>();
        objectifsCouleur3000.add(objectifBlocsDepart3000);

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
