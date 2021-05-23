package fr.esialrobotik.utilities.gson.task;

import fr.esialrobotik.utilities.gson.Tache;

public class GoToChain extends Tache {

    public GoToChain(String desc, int positionX, int positionY) {
        super(desc, 0, positionX, positionY, Tache.Type.DEPLACEMENT, SubType.GOTO_CHAIN, -1, Tache.Mirror.MIRRORY);
    }

    public GoToChain(String desc, int positionX, int positionY, Tache.Mirror mirror) {
        super(desc, 0, positionX, positionY, Tache.Type.DEPLACEMENT, SubType.GOTO_CHAIN, -1, mirror);
    }
}
