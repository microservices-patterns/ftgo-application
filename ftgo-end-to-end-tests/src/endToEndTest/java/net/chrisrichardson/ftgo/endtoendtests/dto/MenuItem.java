package net.chrisrichardson.ftgo.endtoendtests.dto;

public class MenuItem {
    private String id;
    private String name;
    private String price;

    public MenuItem() {
    }

    public String getId() {
        return id;
    }

    public MenuItem id(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public MenuItem name(String name) {
        this.name = name;
        return this;
    }

    public String getPrice() {
        return price;
    }

    public MenuItem price(String price) {
        this.price = price;
        return this;
    }
}
