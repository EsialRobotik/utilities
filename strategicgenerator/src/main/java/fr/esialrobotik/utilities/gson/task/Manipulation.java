package fr.esialrobotik.utilities.gson.task;

import fr.esialrobotik.utilities.gson.Tache;

public class Manipulation extends Tache {

    public Manipulation(String desc, int actionId) {
        super(desc, 0, 0, Type.MANIPULATION, null, actionId, Tache.Mirror.MIRRORY);
    }

    public Manipulation(String desc, int actionId, Tache.Mirror mirror) {
        super(desc, 0, 0, Type.MANIPULATION, null, actionId, mirror);
    }
}
