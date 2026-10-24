package com.fittshop.domain.dto;

import com.fittshop.domain.enums.FitnessGoal;
import jakarta.validation.constraints.NotNull;

public class AthleteProfileRequest {

    @NotNull(message = "Objetivo físico é obrigatório")
    private FitnessGoal goal;

    private Double weightKg;
    private Double heightMeters;
    private Integer weeklyWorkouts;
    private String experienceLevel;

    public AthleteProfileRequest() {}

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
}
