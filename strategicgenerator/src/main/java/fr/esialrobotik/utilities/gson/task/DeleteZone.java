package fr.esialrobotik.utilities.gson.task;

import fr.esialrobotik.utilities.gson.Tache;

public class DeleteZone extends Tache {

    public DeleteZone(String desc, String itemId) {
        super(desc, 0, Tache.Type.ELEMENT, Tache.SubType.SUPPRESSION, itemId, Tache.Mirror.MIRRORY);
    }

    public DeleteZone(String desc, String itemId, Tache.Mirror mirror) {
        super(desc, 0, Tache.Type.ELEMENT, Tache.SubType.SUPPRESSION, itemId, mirror);
    }
}
