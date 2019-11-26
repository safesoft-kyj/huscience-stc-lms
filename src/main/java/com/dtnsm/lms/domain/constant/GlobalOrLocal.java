package com.dtnsm.lms.domain.constant;

public enum GlobalOrLocal {
    GLOBAL("Global Study"), LOCAL("Local Study");

    private String label;

    GlobalOrLocal(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
