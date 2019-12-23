package com.dtnsm.lms.converter;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class RoleConverter implements AttributeConverter<String[], String> {
    @Override
    public String convertToDatabaseColumn(String[] strings) {
        if(ObjectUtils.isEmpty(strings)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean append = false;
        for(String s : strings) {
            if(append) sb.append(",");

            sb.append(s);
            append = true;
        }
        return sb.toString();
    }

    @Override
    public String[] convertToEntityAttribute(String s) {
        if(StringUtils.isEmpty(s)) {
            return null;
        }
        return s.split(",");
    }
}
