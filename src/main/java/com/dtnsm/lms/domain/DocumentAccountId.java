package com.dtnsm.lms.domain;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class DocumentAccountId implements Serializable {
    private static final long serialVersionUID = 1L;


//    @Column(name="course_id")
    private long document;

//    @Column(name="user_id")
    private String account;

    public DocumentAccountId(){}
    public DocumentAccountId(long document, String account){
        this.document = document;
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        return ((o instanceof DocumentAccountId) && document == ((DocumentAccountId)o).getDocument() && account == ((DocumentAccountId) o).getAccount());
    }

    @Override
    public int hashCode() {

        return (int)(document);

    }

}
