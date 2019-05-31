package fr.esialrobotik.utilities.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by franc on 27/04/2018.
 */
public class Tache {

    public enum Type {
        @SerializedName("deplacement")
        DEPLACEMENT("deplacement"),
        @SerializedName("manipulation")
        MANIPULATION("manipulation")
        ;

        private final String text;

        /**
         * @param text
         */
        Type(final String text) {
            this.text = text;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }
    }

    public enum SubType {
        @SerializedName("go")
        GO("go"),
        @SerializedName("goto")
        GOTO("goto"),
        @SerializedName("face")
        FACE("face"),
        @SerializedName("goto_back")
        GOTO_BACK("goto_back"),
        @SerializedName("goto_astar")
        GOTO_ASTAR("goto_astar"),
        ;

        private final String text;

        /**
         * @param text
         */
        SubType(final String text) {
            this.text = text;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }
    }

    public enum Mirror {
        NONE,
        MIRRORY,
        SPECIFIC
    }

    public String desc;
    public int id;
    public int positionX;
    public int positionY;
    public int dist;
    public Type type;
    public SubType subtype;
    public int actionId;
    public Mirror mirror;
    public int timeout = -1;

    public Tache(String desc, int id, int positionX, int positionY, Type type, SubType subtype, int actionId, Mirror mirror) {
        this.desc = desc;
        this.id = id;
        this.positionX = positionX;
        this.positionY = positionY;
        this.type = type;
        this.subtype = subtype;
        this.actionId = actionId;
        this.mirror = mirror;
    }

    public Tache(String desc, int id, int dist, Type type, SubType subtype, int actionId, Mirror mirror) {
        this.desc = desc;
        this.id = id;
        this.dist = dist;
        this.type = type;
        this.subtype = subtype;
        this.actionId = actionId;
        this.mirror = mirror;
    }

    public Tache(String desc, int id, int dist, Type type, SubType subtype, int actionId, Mirror mirror, int timeout) {
        this.desc = desc;
        this.id = id;
        this.dist = dist;
        this.type = type;
        this.subtype = subtype;
        this.actionId = actionId;
        this.mirror = mirror;
        this.timeout = timeout;
    }

    public Tache() {
    }

    public Tache(Tache t) {
        this.desc = t.desc;
        this.id = t.id;
        this.positionX = t.positionX;
        this.positionY = t.positionY;
        this.dist = t.dist;
        this.type = t.type;
        this.subtype = t.subtype;
        this.actionId = t.actionId;
        this.mirror = t.mirror;
        this.timeout = t.timeout;
    }

    @Override
    public String toString() {
        return "\n\t\t\t\tTache{" +
                "desc='" + desc + '\'' +
                ", id=" + id +
                ", positionX=" + positionX +
                ", positionY=" + positionY +
                ", dist=" + dist +
                ", type=" + type +
                ", subtype=" + subtype +
                ", actionId=" + actionId +
                '}';
    }
}
