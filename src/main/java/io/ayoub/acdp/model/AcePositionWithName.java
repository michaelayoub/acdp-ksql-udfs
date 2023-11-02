package io.ayoub.acdp.model;

import io.ayoub.acdp.proto.POIsDBOuterClass.POI;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AcePositionWithName {
    private static final int POSITION_BLOCK_LENGTH = 192;
    private final String name;
    private final long objCellId;
    private final double originX;
    private final double originY;
    private final double originZ;

    public AcePositionWithName(String name, long objCellId, double originX, double originY, double originZ) {
        this.name = name;
        this.objCellId = objCellId;
        this.originX = originX;
        this.originY = originY;
        this.originZ = originZ;
    }

    public static AcePositionWithName fromPoi(POI poi) {
        return new AcePositionWithName(poi.getName(), poi.getObjCellId(), poi.getOriginX(), poi.getOriginY(), poi.getOriginZ());
    }

    public boolean isIndoors() {
        return (objCellId & 0xFFFF) >= 0x100;
    }

    public Optional<List<Double>> getGlobalPosition() {
        if (isIndoors()) {
            return Optional.empty();
        }

        final long landBlockX = objCellId >> 24 & 0xFF;
        final long landBlockY = objCellId >> 16 & 0xFF;

        final double globalX = landBlockX * POSITION_BLOCK_LENGTH + originX;
        final double globalY = landBlockY * POSITION_BLOCK_LENGTH + originY;
        final double globalZ = originZ;

        return Optional.of(List.of(globalX, globalY, globalZ));
    }

    private Optional<List<Double>> getMapCoordinates() {
        final var position = getGlobalPosition();
        return position.map(p -> {
            final double globalX = p.get(0);
            final double globalY = p.get(1);

            final double coordinatesX = globalX / 240.0 - 102;
            final double coordinatesY = globalY / 240.0 - 102;

            return List.of(coordinatesX, coordinatesY);
        });
    }

    public Optional<String> getMapCoordinatesString() {
        final var mapCoordinates = getMapCoordinates();
        return mapCoordinates.map(c -> {
            final double coordinatesX = c.get(0);
            final double coordinatesY = c.get(1);

            final String northSouthDesignation = coordinatesY >= 0 ? "N" : "S";
            final String eastWestDesignation = coordinatesX >= 0 ? "E" : "W";

            final String northSouth = String.format("%.1f", Math.abs(coordinatesY) - 0.05) + northSouthDesignation;
            final String eastWest = String.format("%.1f", Math.abs(coordinatesX) - 0.05) + eastWestDesignation;

            return northSouth + ", " + eastWest;
        });
    }

    public String getName() {
        return name;
    }

    public long getObjCellId() {
        return objCellId;
    }

    public double getOriginX() {
        return originX;
    }

    public double getOriginY() {
        return originY;
    }

    public double getOriginZ() {
        return originZ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AcePositionWithName that = (AcePositionWithName) o;
        return objCellId == that.objCellId && Double.compare(that.originX, originX) == 0 && Double.compare(that.originY, originY) == 0 && Double.compare(that.originZ, originZ) == 0 && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, objCellId, originX, originY, originZ);
    }

    @Override
    public String toString() {
        return "AcePositionWithName{" +
                "name='" + name + '\'' +
                ", objCellId=" + objCellId +
                ", originX=" + originX +
                ", originY=" + originY +
                ", originZ=" + originZ +
                '}';
    }
}
