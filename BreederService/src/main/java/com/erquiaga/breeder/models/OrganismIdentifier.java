package com.erquiaga.breeder.models;

public class OrganismIdentifier {
    public int id;
    public String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public OrganismIdentifier(int id, String type) {
        this.id = id;
        this.type = type;
    }

    public OrganismIdentifier() {
    }
}
