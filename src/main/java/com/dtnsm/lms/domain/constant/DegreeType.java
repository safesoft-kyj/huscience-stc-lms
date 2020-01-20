package com.dtnsm.lms.domain.constant;

import java.util.ArrayList;
import java.util.List;

public enum DegreeType {
    BACHELORS("Bachelor's Degree"), MASTERS("Masters's Degree"), PHD("Phd");

    public static List<DegreeType> degreeTypes = new ArrayList<>();
    static {
        degreeTypes.add(BACHELORS);
        degreeTypes.add(MASTERS);
        degreeTypes.add(PHD);
    }
    private String label;

    DegreeType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static List<DegreeType> getDegreeTypes() {
        return degreeTypes;
    }
}
