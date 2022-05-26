package uj.java.pwj2020.spreadsheet;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static uj.java.pwj2020.spreadsheet.Regex.*;

public class Spreadsheet {

    public String[][] calculate (String[][] input) {
        if (checkIfNull(input)) return null;

        String[][] output = new String[input.length][];
        for (int i = 0; i < input.length; i++) {
            output[i] = new String[input[i].length];
            for (int j = 0; j < input[i].length; j++) {
                output[i][j] = calculateCell(input[i][j], input);
                if (output[i][j] == null) return null;
            }
        }

        return output;
    }

    private String calculateCell (String input, String[][] arr) {
        if (input.matches(INT_REGEX)) {
            return input;
        } else if (input.matches(REFERENCE_REGEX)) {
            Pattern p = Pattern.compile(UINT_REGEX);
            Matcher m = p.matcher(input);
            if (!m.find()) return null;

            return referenceHandler (
                    firstCoordinateConverter(input.substring(1, m.start()), m.start() - 2),
                    Integer.parseInt(input.substring(m.start())),
                    arr
            );
        } else if (input.matches(EQUATION_REGEX)) {
            String[] data = input.split("[(,)]"); // Splits on (, ',' and )

            return equationHandler(data[0].substring(1), data[1], data[2], arr);
        } else {
            return null;
        }
    }

    private String referenceHandler (int y, int x, String[][] arr) {
        if ((x > 0 && x <= arr.length) && (y > 0 && y <= arr[x - 1].length))
            return calculateCell(arr[x - 1][y - 1], arr);
        else
            return null;
    }

    private String equationHandler (String command, String a, String b, String[][] arr) {
        return switch (command) {
            case "ADD" -> strValue(intValue(a, arr) + intValue(b, arr));
            case "SUB" -> strValue(intValue(a, arr) - intValue(b, arr));
            case "MUL" -> strValue(intValue(a, arr) * intValue(b, arr));
            case "DIV" -> strValue(intValue(a, arr) / intValue(b, arr));
            case "MOD" -> strValue(intValue(a, arr) % intValue(b, arr));
            default -> null;
        };
    }

    private int intValue (String input, String[][] arr) {
        return Integer.parseInt(Objects.requireNonNull(calculateCell(input, arr)));
    }

    protected static String strValue (int input) {
        return String.valueOf(input);
    }

    private int firstCoordinateConverter (String input, int index) {
        if (index == 0)
            return input.charAt(0) - 'A' + 1;
        else
            return ( 27 * firstCoordinateConverter(input, --index) ) + ( input.charAt(index) - 'A' + 1);
    }

    private boolean checkIfNull (String[][] arr) {
        if (arr == null) return true;
        for (String[] strings : arr)
            if (strings == null) return true;
        return false;
    }

}
