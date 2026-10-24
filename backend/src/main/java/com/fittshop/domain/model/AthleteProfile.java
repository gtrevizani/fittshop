package com.fittshop.domain.model;

import com.fittshop.domain.enums.FitnessGoal;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class AthleteProfile {

    @Enumerated(EnumType.STRING)
    private FitnessGoal goal;

    private Double weightKg;
    private Double heightMeters;
    private Integer weeklyWorkouts;
    private String experienceLevel; // INICIANTE, INTERMEDIARIO, AVANCADO, ATLETA

    public AthleteProfile() {}

    public AthleteProfile(FitnessGoal goal, Double weightKg, Double heightMeters, Integer weeklyWorkouts, String experienceLevel) {
        this.goal = goal;
        this.weightKg = weightKg;
        this.heightMeters = heightMeters;
        this.weeklyWorkouts = weeklyWorkouts;
        this.experienceLevel = experienceLevel;
    }

    public FitnessGoal getGoal() { return goal; }
    public void setGoal(FitnessGoal goal) { this.goal = goal; }

    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

    public Double getHeightMeters() { return heightMeters; }
    public void setHeightMeters(Double heightMeters) { this.heightMeters = heightMeters; }

    public Integer getWeeklyWorkouts() { return weeklyWorkouts; }
    public void setWeeklyWorkouts(Integer weeklyWorkouts) { this.weeklyWorkouts = weeklyWorkouts; }

    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }

    public Double calculateBmi() {
        if (weightKg == null || heightMeters == null || heightMeters == 0) return 0.0;
        return weightKg / (heightMeters * heightMeters);
    }
}
