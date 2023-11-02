package io.ayoub.acdp.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Ace3DDistanceTest {
    private static Ace3DDistance ace3DDistance;
    private static final double DELTA = 1e-15;

    @BeforeAll
    static void setAce3DDistance() {
        ace3DDistance = new Ace3DDistance();
    }

    @Test
    void distanceShoushiToHebianTo() {
        AcePositionWithName shoushi = new AcePositionWithName("Shoushi", 3663003677L, 84.8, 99.0, 20.0);
        AcePositionWithName hebianTo = new AcePositionWithName("Hebian-to", 3863871535L, 138.304, 161.905, 20.04);

        final double distance = ace3DDistance.distance(shoushi, hebianTo);

        assertEquals(2683.100726890625, distance, DELTA);
    }

    @Test
    void distanceZaikhalToAlJalima() {
        AcePositionWithName zaikhal = new AcePositionWithName("Zaikhal", 2156920851L, 64.863, 55.687, 124.005);
        AcePositionWithName alJalima = new AcePositionWithName("Al-Jalima", 2240282668L, 120.359, 95.47, 90.049);

        final double distance = ace3DDistance.distance(zaikhal, alJalima);

        assertEquals(1808.6045579509635, distance, DELTA);
    }

    @Test
    void distanceZaikhalToTufa() {
        AcePositionWithName zaikhal = new AcePositionWithName("Zaikhal", 2156920851L, 64.863, 55.687, 124.005);
        AcePositionWithName tufa = new AcePositionWithName("Tufa", 2272002056L, 2.0, 186.9, 18.0);

        final double distance = ace3DDistance.distance(zaikhal, tufa);

        assertEquals(6901.566591156171, distance, DELTA);
    }
}