package org.among.springbatchstudy.domain;

public class Player {
    private String name;
    private String type;
    private String nationality;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", nationality='" + nationality + '\'' +
                '}';
    }
}
