package growmate.model;

import java.awt.Color;
import java.time.LocalDate;

public class Plant {
    private int id;
    private int userId;
    private String name;
    private String species;
    private int waterIntervalDays;
    private int fertilizeIntervalDays;
    private LocalDate lastWatered;      // null => never watered (show yellow)
    private LocalDate lastFertilized;   // null => never fertilized
    private String notes;

    public Plant() {}

    public Plant(String name, String species, int waterIntervalDays, int fertilizeIntervalDays) {
        this.name = name;
        this.species = species;
        this.waterIntervalDays = waterIntervalDays;
        this.fertilizeIntervalDays = fertilizeIntervalDays;
        this.lastWatered = null;             // new plants start as "not yet watered"
        this.lastFertilized = LocalDate.now(); // optional: treat as recently fertilized
    }

    // --- getters / setters ---
    public int getId() { return id; }                 public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }         public void setUserId(int userId) { this.userId = userId; }
    public String getName() { return name; }          public void setName(String name) { this.name = name; }
    public String getSpecies() { return species; }    public void setSpecies(String species) { this.species = species; }
    public int getWaterIntervalDays() { return waterIntervalDays; }  public void setWaterIntervalDays(int v) { this.waterIntervalDays = v; }
    public int getFertilizeIntervalDays() { return fertilizeIntervalDays; } public void setFertilizeIntervalDays(int v) { this.fertilizeIntervalDays = v; }
    public LocalDate getLastWatered() { return lastWatered; }        public void setLastWatered(LocalDate d) { this.lastWatered = d; }
    public LocalDate getLastFertilized() { return lastFertilized; }  public void setLastFertilized(LocalDate d) { this.lastFertilized = d; }
    public String getNotes() { return notes; }        public void setNotes(String notes) { this.notes = notes; }

    // --- watering logic ---
    public boolean needsWatering() {
        // overdue if lastWatered exists and today > lastWatered + interval
        return lastWatered != null && LocalDate.now().isAfter(lastWatered.plusDays(waterIntervalDays));
    }

    public boolean isWateredRecently() {
        if (lastWatered == null) return false;
        // still within safe watering period
        return !LocalDate.now().isAfter(lastWatered.plusDays(waterIntervalDays));
    }

    // --- fertilizing logic (this is what your code was missing) ---
    public boolean isFertilizedRecently() {
        if (fertilizeIntervalDays <= 0) return true;         // no schedule -> treat as fine
        if (lastFertilized == null) return false;            // never fertilized
        return !LocalDate.now().isAfter(lastFertilized.plusDays(fertilizeIntervalDays));
    }

    public boolean needsFertilizing() {
        if (fertilizeIntervalDays <= 0) return false;        // no schedule -> never needed
        if (lastFertilized == null) return true;             // never fertilized -> needs it
        return LocalDate.now().isAfter(lastFertilized.plusDays(fertilizeIntervalDays));
    }

    // --- row color in the list (based on WATERING only, as you requested) ---
    public Color getStatusColor() {
        if (isWateredRecently()) {
            return new Color(144, 238, 144); // green: watered
        }
        if (lastWatered == null) {
            return new Color(255, 255, 153); // yellow: new / not yet watered
        }
        return new Color(255, 160, 122);     // red: overdue watering
    }

    @Override
    public String toString() {
        return name + (species == null || species.isBlank() ? "" : " (" + species + ")");
    }
}
