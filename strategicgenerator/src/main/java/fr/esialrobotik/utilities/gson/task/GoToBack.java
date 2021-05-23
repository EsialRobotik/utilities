package fr.esialrobotik.utilities.gson.task;

import fr.esialrobotik.utilities.gson.Tache;

public class GoToBack extends Tache {

    public GoToBack(String desc, int positionX, int positionY) {
        super(desc, 0, positionX, positionY, Tache.Type.DEPLACEMENT, SubType.GOTO_BACK, -1, Tache.Mirror.MIRRORY);
    }

    public GoToBack(String desc, int positionX, int positionY, Tache.Mirror mirror) {
        super(desc, 0, positionX, positionY, Tache.Type.DEPLACEMENT, SubType.GOTO_BACK, -1, mirror);
    }
}
