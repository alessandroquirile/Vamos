package com.quiriletelese.troppadvisorproject.my_exceptions;

/**
 * @author Alessandro Quirile, Mauro Telese
 */

public class TechnologyNotSupportedYetException extends RuntimeException {
    public TechnologyNotSupportedYetException(String technology) {
        super(technology + " is not supported yet or invalid");
    }
}
