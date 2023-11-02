package io.ayoub.acdp.data.pois;

import io.ayoub.acdp.proto.POIsDBOuterClass.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PoisDbImporter {
    private final List<POI> pois = new ArrayList<>();

    public PoisDbImporter() {
        try {
            final POIsDB poisDB = POIsDB.parseFrom(getClass().getClassLoader().getResourceAsStream("pois_db.binpb"));

            loadPois(poisDB);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadPois(POIsDB poisDB) {
        pois.addAll(poisDB.getPoisList());
    }

    public List<POI> getPois() { return pois; }
}
