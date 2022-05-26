package uj.pwj2020.introduction;

public class Reverser {

    public String reverse(String input) {
        if (input == null)
            return null;
        else
            return new StringBuilder(input.strip()).reverse().toString();
    }

    public String reverseWords(String input) {
        if (input == null)
            return null;
        else
            return reversedAppend(input.strip().split(" "));
    }

    private static String reversedAppend(String[] words) {
        StringBuilder reversedWords = new StringBuilder();
        for (int i = words.length - 1; i > 0; i--)
            reversedWords.append(words[i]).append(" ");

        if (words.length > 0)
            reversedWords.append(words[0]);

        return reversedWords.toString();
    }
}
