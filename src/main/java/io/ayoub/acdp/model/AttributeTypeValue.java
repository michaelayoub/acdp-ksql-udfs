package io.ayoub.acdp.model;

public class AttributeTypeValue {
    private final int value;
    private final String label;

    private AttributeTypeValue(int value, String label) {
        this.value = value;
        this.label = label;
    }

    public static AttributeTypeValue of(int value, String label) {
        return new AttributeTypeValue(value, label);
    }

    public int getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }
}
