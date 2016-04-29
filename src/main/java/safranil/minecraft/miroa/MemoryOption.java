package safranil.minecraft.miroa;

class MemoryOption {
    private final String javaOption;
    private final String displayString;

    MemoryOption(String javaOption, String displayString) {
        this.javaOption = javaOption;
        this.displayString = displayString;
    }

    String getJavaOption() {
        return javaOption;
    }

    @Override
    public String toString() {
        return displayString;
    }
}
