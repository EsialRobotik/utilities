package fr.esialrobotik.utilities.gson.task;

import fr.esialrobotik.utilities.gson.Tache;

public class Go extends Tache {

    public Go(String desc, int dist) {
        super(desc, 0, dist, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY);
    }

    public Go(String desc, int dist, Tache.Mirror mirror) {
        super(desc, 0, dist, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, mirror);
    }

    public Go(String desc, int dist, int timeout) {
        super(desc, 0, dist, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, Tache.Mirror.MIRRORY, timeout);
    }

    public Go(String desc, int dist, Tache.Mirror mirror, int timeout) {
        super(desc, 0, dist, Tache.Type.DEPLACEMENT, Tache.SubType.GO, -1, mirror, timeout);
    }
}
