package org.acme.callcenter.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Skill {

    CAR_INSURANCE("Car insurance"),
    LIFE_INSURANCE("Life insurance"),
    PROPERTY_INSURANCE("Property insurance"),
    FIRE_INSURANCE("Fire insurance"),
    THEFT_INSURANCE("Theft insurance"),
    GUITAR_INSURANCE("Guitar insurance"),
    JEWELERY_INSURANCE("Jewelery insurance");

    private String name;

    Skill(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }
}
