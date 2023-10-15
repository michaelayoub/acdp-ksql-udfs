package io.ayoub.acdp.portalDatabase.tables;

import io.ayoub.acdp.portalDatabase.entities.SkillBase;

import java.io.Serializable;
import java.util.Map;

public class SkillTable implements Serializable {
    private Map<Integer, SkillBase> skillBaseMap;

    public SkillTable(Map<Integer, SkillBase> skillBaseMap) {
        this.skillBaseMap = skillBaseMap;
    }

    public SkillTable() { }

    public Map<Integer, SkillBase> getSkillBaseMap() {
        return skillBaseMap;
    }

    public void setSkillBaseMap(Map<Integer, SkillBase> skillBaseMap) {
        this.skillBaseMap = skillBaseMap;
    }
}
