public class Product {

    private String name;
    private Double caloriesPer100Grams;
    private Double gramsPerServing;
    private String mainIngredient;

    public Product(String name, double caloriesPer100Gram, double gramsPerServing, String mainIngredient) {
        this.name = name;
        this.caloriesPer100Grams = caloriesPer100Gram;
        this.gramsPerServing = gramsPerServing;
        this.mainIngredient = mainIngredient;
    }

    public String getName() {
        return name;
    }
    public Double getCaloriesPer100Grams() { return caloriesPer100Grams; }
    public Double getGramsPerServing() { return gramsPerServing; }
    public String getMainIngredient() { return mainIngredient; }

    public void setName(String name) { this.name = name; }
    public void setCaloriesPer100Gram(Double caloriesPer100Gram) { this.caloriesPer100Grams = caloriesPer100Gram; }
    public void setGramsPerServing(Double gramsPerServing) { this.gramsPerServing = gramsPerServing; }
    public void setMainIngredient(String mainIngredient) { this.mainIngredient = mainIngredient; }
}
