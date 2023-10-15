package io.ayoub.acdp.portalDatabase.entities;

import java.io.Serializable;
import java.util.Objects;

public class SkillBase implements Serializable {
    private String name;
    private String description;
    private int iconId;
    private int divisor;
    private int attr1;
    private int attr2;

    public SkillBase(String name, String description, int iconId, int divisor, int attr1, int attr2) {
        this.name = name;
        this.description = description;
        this.iconId = iconId;
        this.divisor = divisor;
        this.attr1 = attr1;
        this.attr2 = attr2;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getIconId() {
        return iconId;
    }

    public int getDivisor() {
        return divisor;
    }

    public int getAttr1() {
        return attr1;
    }

    public int getAttr2() {
        return attr2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkillBase skillBase = (SkillBase) o;
        return iconId == skillBase.iconId && divisor == skillBase.divisor && attr1 == skillBase.attr1 && attr2 == skillBase.attr2 && name.equals(skillBase.name) && description.equals(skillBase.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, iconId, divisor, attr1, attr2);
    }
}
