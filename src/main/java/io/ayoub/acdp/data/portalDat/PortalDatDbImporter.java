package io.ayoub.acdp.data.portalDat;

import io.ayoub.acdp.proto.PortalDatDBOuterClass.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortalDatDbImporter {
    private final List<Integer> attributeXp = new ArrayList<>();
    private final List<Integer> vitalXp = new ArrayList<>();
    private final List<Integer> trainedSkillXp = new ArrayList<>();
    private final List<Integer> specializedSkillXp = new ArrayList<>();
    private final List<Long> characterLevelXp = new ArrayList<>();
    private final List<Contract> contracts = new ArrayList<>();
    private final Map<Integer, Skill> skillMap = new HashMap<>();

    public PortalDatDbImporter() {
        try {
            final PortalDatDB portalDatDB = PortalDatDB
                    .parseFrom(getClass().getClassLoader().getResourceAsStream("portal_dat.binpb"));

            loadXpLists(portalDatDB);
            loadContracts(portalDatDB);
            loadSkills(portalDatDB);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadSkills(PortalDatDB portalDatDB) {
        portalDatDB.getSkillsList().forEach(skill -> {
            skillMap.put(skill.getId(), skill);
        });
    }

    private void loadContracts(PortalDatDB portalDatDB) {
        contracts.addAll(portalDatDB.getContractsList());
    }

    private void loadXpLists(PortalDatDB portalDatDB) {
        portalDatDB.getAttributeXpList().forEach(xpListElementInt -> {
            attributeXp.add(xpListElementInt.getCost());
        });

        portalDatDB.getVitalXpList().forEach(xpListElementInt -> {
            vitalXp.add(xpListElementInt.getCost());
        });

        portalDatDB.getTrainedSkillXpList().forEach(xpListElementInt -> {
            trainedSkillXp.add(xpListElementInt.getCost());
        });

        portalDatDB.getSpecializedSkillXpList().forEach(xpListElementInt -> {
            specializedSkillXp.add(xpListElementInt.getCost());
        });

        portalDatDB.getCharacterLevelXpList().forEach(xpListElementLong -> {
            characterLevelXp.add(xpListElementLong.getCost());
        });
    }

    public List<Integer> getAttributeXp() {
        return attributeXp;
    }

    public List<Integer> getVitalXp() {
        return vitalXp;
    }

    public List<Integer> getTrainedSkillXp() {
        return trainedSkillXp;
    }

    public List<Integer> getSpecializedSkillXp() {
        return specializedSkillXp;
    }

    public List<Long> getCharacterLevelXp() {
        return characterLevelXp;
    }

    public List<Contract> getContracts() {
        return contracts;
    }

    public Map<Integer, Skill> getSkillMap() {
        return skillMap;
    }
}
