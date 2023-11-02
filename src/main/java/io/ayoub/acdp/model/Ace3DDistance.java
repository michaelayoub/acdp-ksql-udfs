package io.ayoub.acdp.model;

import io.github.zabuzard.closy.external.Metric;

public class Ace3DDistance implements Metric<AcePositionWithName> {
    public final int POSITION_BLOCK_LENGTH = 192;

    @Override
    public double distance(AcePositionWithName first, AcePositionWithName second) {
        if (first.getObjCellId() == second.getObjCellId()) {
            final double dx = first.getOriginX() - second.getOriginX();
            final double dy = first.getOriginY() - second.getOriginY();
            final double dz = first.getOriginZ() - second.getOriginZ();
            return Math.sqrt(dx*dx + dy*dy + dz*dz);
        } else {
            final long firstLandBlockX = first.getObjCellId() >> 24 & 0xFF;
            final long firstLandBlockY = first.getObjCellId() >> 16 & 0xFF;
            final long secondLandBlockX = second.getObjCellId() >> 24 & 0xFF;
            final long secondLandBlockY = second.getObjCellId() >> 16 & 0xFF;
            final double dx = (firstLandBlockX - secondLandBlockX) * POSITION_BLOCK_LENGTH + first.getOriginX() - second.getOriginX();
            final double dy = (firstLandBlockY - secondLandBlockY) * POSITION_BLOCK_LENGTH + first.getOriginY() - second.getOriginY();
            final double dz = first.getOriginZ() - second.getOriginZ();
            return Math.sqrt(dx*dx + dy*dy + dz*dz);
        }
    }
}
