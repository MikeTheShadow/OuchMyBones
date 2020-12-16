package com.miketheshadow.ouchmybones;

import java.io.IOException;
import java.util.*;

import static com.miketheshadow.ouchmybones.ConstantAilments.*;

public class DataStorage {

    public static void save() {

        for(Map.Entry<String,List<InjuryType>> entry : injuredPlayers.entrySet() ) {
            List<String> injuries = new ArrayList<>();
            for(InjuryType s : entry.getValue()) injuries.add(s.name());
            OuchMyBones.dataStorage.set(entry.getKey(),injuries);
        }
        try {
            OuchMyBones.dataStorage.save(OuchMyBones.databaseFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadInjuredPlayers() {

        injuredPlayers.clear();

        Set<String> injuries = OuchMyBones.dataStorage.getKeys(true);
        for (String injury : injuries) {

            List<String> stringInjuries = OuchMyBones.dataStorage.getStringList(injury);
            List<InjuryType> types = new ArrayList<>();
            stringInjuries.forEach(s -> types.add(InjuryType.valueOf(s)));

            injuredPlayers.put(injury,types);
        }
    }

}
