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
        System.out.println("Hello world");

        Tache tache1 = new Tache("go position 1", 1, 320, 930, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY);
        Tache tache2 = new Tache("alignement", 2, 600, 1000, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY);
        Tache tache3 = new Tache("specific", 3, 420, 1000, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.SPECIFIC);
        Tache tache4 = new Tache("no mirror", 4, 1000, 1000, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.NONE);
        List<Tache> tachesO1C0 = new ArrayList<>();
        tachesO1C0.add(tache1);
        tachesO1C0.add(tache2);
        tachesO1C0.add(tache3);
        tachesO1C0.add(tache4);

        Objectif objectifCouleur0 = new Objectif("Objectif 1", 1, 42, 42, tachesO1C0);

        List<Objectif> objectifsC0 = new ArrayList<>();
        objectifsC0.add(objectifCouleur0);

        Objectif objectifCouleur3000 = new Objectif("Objectif 1", 1, 42, 42, null);
        Tache tache3Couleur3000 = new Tache("specific", 3, 420, 500, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.SPECIFIC);
        List<Tache> tachesSpecific = new ArrayList<>();
        tachesSpecific.add(tache3Couleur3000);

        try {
            objectifCouleur3000.generateMirror(objectifCouleur0.taches, tachesSpecific);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Objectif> objectifsC3000 = new ArrayList<>();
        objectifsC3000.add(objectifCouleur3000);

        Strategie strat = new Strategie();
        strat.couleur0 = objectifsC0;
        strat.couleur3000 = objectifsC3000;

        System.out.println(strat.toString());

        final GsonBuilder builder = new GsonBuilder();
        final Gson gson = builder.create();

        System.out.println("#########################");
        System.out.println(gson.toJson(strat));

        try (PrintWriter jsonFile = new PrintWriter("test.json")) {
            jsonFile.println(gson.toJson(strat));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}
