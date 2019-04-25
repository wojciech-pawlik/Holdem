package pl.erfean.holdem.model.interfaces;

public interface HandI {
    // Hand types based on suits
    int suitValue();
    void howManySuited();
    boolean isStraightFlush();
    // Hand types based on values
    void maxValuePowers();
    boolean isStraightAceHigh();
    boolean isStraight();
    // Main method of checking hand
    int checkHand();
    // Strings based on checked hand
    String getType();
    String getName();
    // Removing whole progress
    void destroyHand();
}