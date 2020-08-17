public class Product {

    private String name;
    private Double caloriesPer100Gram;

    public Product(String name, double caloriesPer100Gram) {
        this.name = name;
        this.caloriesPer100Gram = caloriesPer100Gram;
    }

    public String getName() {
        return name;
    }
    public Double getCaloriesPer100Gram() {
        return caloriesPer100Gram;
    }
    // Double caloriesPerPiece;
}
