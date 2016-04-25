package safranil.minecraft.miroa;

public class MemoryOption {
    private String javaOption;
    private String displayString;

    public MemoryOption(String javaOption, String displayString) {
        this.javaOption = javaOption;
        this.displayString = displayString;
    }

    public String getJavaOption() {
        return javaOption;
    }

    @Override
    public String toString() {
        return displayString;
    }
}
