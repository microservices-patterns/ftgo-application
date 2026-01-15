package net.chrisrichardson.ftgo.endtoendtests.dto;

public class CourierAvailability {
    private boolean available;

    public CourierAvailability() {
    }

    public CourierAvailability(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }
}
