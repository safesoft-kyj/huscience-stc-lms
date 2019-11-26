package com.dtnsm.lms.domain.constant;

public enum SkillLevel {
    BEGINNER("Beginner"), INTERMEDIATE("Intermediate"), ADVANCED("Advanced");

    private String label;
    SkillLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
