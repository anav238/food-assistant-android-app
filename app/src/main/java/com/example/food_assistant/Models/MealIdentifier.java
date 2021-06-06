package com.example.food_assistant.Models;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class MealIdentifier implements Serializable {
    String id;
    String mealName;

    public MealIdentifier() {
    }

    public MealIdentifier(String id, String mealName) {
        this.id = id;
        this.mealName = mealName;
    }

    public String getId() {
        return id;
    }

    public String getMealName() {
        return mealName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MealIdentifier mealIdentifier = (MealIdentifier) obj;
        return this.id.equals(mealIdentifier.id) && this.mealName.equals(mealIdentifier.mealName);
    }
}
