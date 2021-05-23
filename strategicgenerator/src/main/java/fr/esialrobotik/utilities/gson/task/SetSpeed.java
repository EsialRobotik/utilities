package fr.esialrobotik.utilities.gson.task;

import fr.esialrobotik.utilities.gson.Tache;

public class SetSpeed extends Tache {

    public SetSpeed(String desc, int speed) {
        super(desc, 0, speed, Tache.Type.DEPLACEMENT, SubType.SET_SPEED, -1, Tache.Mirror.MIRRORY);
    }

    public SetSpeed(String desc, int speed, Tache.Mirror mirror) {
        super(desc, 0, speed, Tache.Type.DEPLACEMENT, Tache.SubType.SET_SPEED, -1, mirror);
    }
}
