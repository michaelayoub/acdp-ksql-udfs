package io.ayoub.acdp.portalDatabase.tables;

import java.io.Serializable;
import java.util.List;

public class XPTable implements Serializable {
    private List<Integer> attributeXPList;
    private List<Integer> vitalXPList;
    private List<Integer> trainedSkillXPList;
    private List<Integer> specializedSkillXPList;
    private List<Long> characterLevelXPList;

    public XPTable() {
    }

    public List<Integer> getAttributeXPList() {
        return attributeXPList;
    }

    public List<Integer> getVitalXPList() {
        return vitalXPList;
    }

    public List<Integer> getTrainedSkillXPList() {
        return trainedSkillXPList;
    }

    public List<Integer> getSpecializedSkillXPList() {
        return specializedSkillXPList;
    }

    public List<Long> getCharacterLevelXPList() {
        return characterLevelXPList;
    }

    public void setAttributeXPList(List<Integer> attributeXPList) {
        this.attributeXPList = attributeXPList;
    }

    public void setVitalXPList(List<Integer> vitalXPList) {
        this.vitalXPList = vitalXPList;
    }

    public void setTrainedSkillXPList(List<Integer> trainedSkillXPList) {
        this.trainedSkillXPList = trainedSkillXPList;
    }

    public void setSpecializedSkillXPList(List<Integer> specializedSkillXPList) {
        this.specializedSkillXPList = specializedSkillXPList;
    }

    public void setCharacterLevelXPList(List<Long> characterLevelXPList) {
        this.characterLevelXPList = characterLevelXPList;
    }
}
