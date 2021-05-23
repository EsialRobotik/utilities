package fr.esialrobotik.utilities.gson.task;

import fr.esialrobotik.utilities.gson.Tache;

public class GoTo extends Tache {

    public GoTo(String desc, int positionX, int positionY) {
        super(desc, 0, positionX, positionY, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, Tache.Mirror.MIRRORY);
    }

    public GoTo(String desc, int positionX, int positionY, Tache.Mirror mirror) {
        super(desc, 0, positionX, positionY, Tache.Type.DEPLACEMENT, Tache.SubType.GOTO, -1, mirror);
    }
}
