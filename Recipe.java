public class Recipe {

    private String name;
    private String instructions;
    private String meal;

    public Recipe(String name, String instructions, String meal) {
        this.name = name;
        this.instructions = instructions;
        this.meal = meal;
    }


    public String getName() {
        return name;
    }
    public String getInstructions() {
        return instructions;
    }
    public String getMeal() { return meal; }

    public void setName(String name) {
        this.name = name;
    }
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    public void setMeal(String meal) { this.meal = meal; }
}
