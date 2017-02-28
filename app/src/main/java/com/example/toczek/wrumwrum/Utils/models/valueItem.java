package com.example.toczek.wrumwrum.Utils.models;

import lombok.Data;

@Data
public class ValueItem {
    private int id;
    private int value;
    private int maxValue;
    private String unit;

    public void setValue(String stringValue) {
        value = -1;
        if (stringValue != null && !stringValue.isEmpty()) {
            stringValue = stringValue.replaceAll("[^\\d]", "");
            if (!stringValue.isEmpty()) {
                value = Integer.parseInt(stringValue);
            }
        }
    }
}
