package uj.java.pwj2020.spreadsheet;

import static uj.java.pwj2020.spreadsheet.Spreadsheet.strValue;

public class Regex {
    private final static int MIN_LETTERS_IN_COMMAND = 3;
    private final static int MAX_LETTERS_IN_COMMAND = 3;

    protected final static String UINT_REGEX = "\\d+";
    protected final static String INT_REGEX = "-?" + UINT_REGEX;
    protected final static String LETTER_CHAIN_REGEX = "\\p{Upper}+";

    protected final static String REFERENCE_REGEX = "\\$" + LETTER_CHAIN_REGEX + UINT_REGEX;
    protected final static String VARIABLE_IN_EQUATION = "((" + REFERENCE_REGEX + ")|(" + INT_REGEX + "))";


    protected final static String COMMAND_REGEX =
            "=\\p{Upper}{" + strValue(MIN_LETTERS_IN_COMMAND) + "," + strValue(MAX_LETTERS_IN_COMMAND) + "}";

    protected final static String EQUATION_REGEX =
            COMMAND_REGEX + "\\(" + VARIABLE_IN_EQUATION + "," + VARIABLE_IN_EQUATION + "\\)";

}
