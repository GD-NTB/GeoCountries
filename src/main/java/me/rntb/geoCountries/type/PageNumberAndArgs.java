package me.rntb.geoCountries.type;

import java.util.Arrays;

public record PageNumberAndArgs(int pageNumber, String[] args) {

    // parses a string array by:
    // [] -> (1, null)
    // [arg0, arg1, ...] -> (1, args)
    // [pagenumber] -> (pagenumber, null)
    // [pagenumber, arg0, arg1, ...] -> (pagenumber, args)
    public static PageNumberAndArgs parse(String[] inputArgs) {
        int intPart = 1;
        String[] stringPart = null;

        if (inputArgs.length != 0) {
            try {
                intPart = Integer.parseInt(inputArgs[0]);

                // [pagenumber, arg0, arg1, ...] -> (pagenumber, args)
                if (inputArgs.length != 1)
                    stringPart = Arrays.copyOfRange(inputArgs, 1, inputArgs.length);
            }

            // [arg0, arg1, ...] -> (1, args)
            catch (NumberFormatException ignored) {
                stringPart = Arrays.copyOf(inputArgs, inputArgs.length);
            }
        }

        return new PageNumberAndArgs(intPart, stringPart);
    }
}
