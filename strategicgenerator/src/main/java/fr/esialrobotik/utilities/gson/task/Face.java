package fr.esialrobotik.utilities.gson.task;

import fr.esialrobotik.utilities.gson.Tache;

public class Face extends Tache {

    public Face(String desc, int positionX, int positionY) {
        super(desc, 0, positionX, positionY, Tache.Type.DEPLACEMENT, SubType.FACE, -1, Tache.Mirror.MIRRORY);
    }

    public Face(String desc, int positionX, int positionY, Tache.Mirror mirror) {
        super(desc, 0, positionX, positionY, Tache.Type.DEPLACEMENT, SubType.FACE, -1, mirror);
    }
}
