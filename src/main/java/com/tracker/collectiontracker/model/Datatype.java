package com.tracker.collectiontracker.model;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;

/**
 *
 */
public enum Datatype {
    TEXT("Text"),
    CHECKBOX("Checkbox"),
    DROPDOWN("Dropdown"),
    NUMBER("Number"),
    CHECKLIST("Checklist"),
    URL("URL");

    @Getter
    private final String name;

    Datatype(String name) {
        this.name = name;
    }

    public static Datatype getByName(String name) {
        Datatype datatype = TEXT;
        for (Datatype dt : values()) {
            if (StringUtils.equals(dt.getName(), name)) {
                datatype = dt;
            }
        }
        return datatype;
    }
}
