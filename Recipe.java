public class Recipe {

    private String name;
    private String instructions;


    public Recipe(String name, String instructions) {
        this.name = name;
        this.instructions = instructions;
    }


    public String getName() {
        return name;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
