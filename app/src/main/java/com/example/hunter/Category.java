package com.example.hunter;

import java.io.Serializable;

public class Category  {
    private String name;
    private Boolean value;
    private int pos;

    public Category(){}

    public Category(String name, Boolean value, int pos) {
        this.name = name;
        this.value = value;
        this.pos=pos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
