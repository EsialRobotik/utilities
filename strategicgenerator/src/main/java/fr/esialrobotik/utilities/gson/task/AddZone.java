package fr.esialrobotik.utilities.gson.task;

import fr.esialrobotik.utilities.gson.Tache;

public class AddZone extends Tache {

    public AddZone(String desc, String itemId) {
        super(desc, 0, Type.ELEMENT, SubType.AJOUT, itemId, Mirror.MIRRORY);
    }

    public AddZone(String desc, String itemId, Mirror mirror) {
        super(desc, 0, Type.ELEMENT, SubType.AJOUT, itemId, mirror);
    }
}
