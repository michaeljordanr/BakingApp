package com.michaeljordanr.baking.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Recipe implements Parcelable {

    private int id;
    private String name;
    private List<Step> steps;
    private List<Ingredient> ingredients;
    private int servings;
    private String image;

    protected Recipe(Parcel in) {
        id = in.readInt();
        name = in.readString();
        steps = in.createTypedArrayList(Step.CREATOR);
        ingredients = in.createTypedArrayList(Ingredient.CREATOR);
        servings = in.readInt();
        image = in.readString();
    }

    public Recipe(Integer id, String name, List<Ingredient> ingredients, List<Step> steps, Integer servings, String image) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = image;
    }

    public static Recipe mockObject() {
        List<Step> stepList = new ArrayList();
        stepList.add(Step.mockObject());
        stepList.add(Step.mockObject());
        stepList.add(Step.mockObject());
        List<Ingredient> ingredientsList = new ArrayList();
        ingredientsList.add(Ingredient.mockObject());
        ingredientsList.add(Ingredient.mockObject());
        ingredientsList.add(Ingredient.mockObject());
        return new Recipe(Integer.valueOf(1), "Nutella Pie", ingredientsList, stepList, Integer.valueOf(8), "http://www.singforyoursupperblog.com/wp271/wp-content/uploads/2010/05/nutellapie3-2.jpg");
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeTypedList(steps);
        parcel.writeTypedList(ingredients);
        parcel.writeInt(servings);
        parcel.writeString(image);
    }
}
