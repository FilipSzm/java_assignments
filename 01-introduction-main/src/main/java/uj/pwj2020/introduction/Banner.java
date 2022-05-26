package uj.pwj2020.introduction;

import static uj.pwj2020.introduction.BannerFont.toFont;

public class Banner {
    final private static int HEIGHT_OF_FONT = 7;

    public String[] toBanner(String input) {
        if (input == null)
            return new String[0];
        else {
            input = input.toLowerCase();
            StringBuilder[] bannerBuilder = createBuilder();
            fillBanner(input, bannerBuilder);
            return returnStringWithoutTrailing(bannerBuilder);
        }
    }

    private StringBuilder[] createBuilder() {
        StringBuilder[] output = new StringBuilder[HEIGHT_OF_FONT];
        for (int i = 0; i < HEIGHT_OF_FONT; i++)
            output[i] = new StringBuilder();
        return output;
    }

    private void fillBanner(String input, StringBuilder[] banner) {
        for (int i = 0; i < input.length() - 1; i++) {
            String[] toAdd = toFont(input.charAt(i));
            for (int j = 0; j < HEIGHT_OF_FONT; j++)
                banner[j].append(toAdd[j]).append(" ");
        }

        if (input.length() > 0) {
            String[] toAdd = toFont(input.charAt(input.length() - 1));
            for (int j = 0; j < HEIGHT_OF_FONT; j++)
                banner[j].append(toAdd[j]);
        }
    }

    private String[] returnStringWithoutTrailing(StringBuilder[] banner) {
        String[] output = new String[HEIGHT_OF_FONT];
        for (int i = 0; i < HEIGHT_OF_FONT; i++)
            output[i] = banner[i].toString().stripTrailing();
        return output;
    }

}
