package com.quiriletelese.troppadvisorproject.my_exceptions;

/**
 * @author Alessandro Quirile, Mauro Telese
 */
public class TechnologyNotSupportedYet extends RuntimeException {
    public TechnologyNotSupportedYet(String technology) {
        super(technology + " is not supported yet or invalid");
    }
}
