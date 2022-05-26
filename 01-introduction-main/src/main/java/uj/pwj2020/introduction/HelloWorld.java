package uj.pwj2020.introduction;

public class HelloWorld {
    public static void main(String[] args) {
        if (args == null || args.length == 0)
            System.out.print("No input parameters provided" + "\n");
        else
            printArgs(args);
    }

    private static void printArgs(String[] args) {
        for (String arg : args)
            System.out.print(arg + "\n");
    }
}
