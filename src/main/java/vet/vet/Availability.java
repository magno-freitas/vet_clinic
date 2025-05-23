package vet;


import java.sql.Date;
import java.sql.Timestamp;

public class Availability {
    private int slotId;
    private Date date;
    private Timestamp startTime;
    private Timestamp endTime;
    private boolean available;

    // Getters e Setters
    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public Date getDate() {
        return date;
    }


    public void setDate(java.util.Date date2) {
        if (date2 != null) {
            this.date = new java.sql.Date(date2.getTime());
        } else {
            this.date = null;
        }
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

}