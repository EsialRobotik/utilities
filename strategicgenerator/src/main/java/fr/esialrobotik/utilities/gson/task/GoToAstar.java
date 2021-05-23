package fr.esialrobotik.utilities.gson.task;

import fr.esialrobotik.utilities.gson.Tache;

public class GoToAstar extends Tache {

    public GoToAstar(String desc, int positionX, int positionY) {
        super(desc, 0, positionX, positionY, Tache.Type.DEPLACEMENT, SubType.GOTO_ASTAR, -1, Tache.Mirror.MIRRORY);
    }

    public GoToAstar(String desc, int positionX, int positionY, Tache.Mirror mirror) {
        super(desc, 0, positionX, positionY, Tache.Type.DEPLACEMENT, SubType.GOTO_ASTAR, -1, mirror);
    }
}
