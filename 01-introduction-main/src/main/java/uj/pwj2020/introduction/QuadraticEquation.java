package uj.pwj2020.introduction;

import static java.lang.Math.sqrt;

public class QuadraticEquation {

    public double[] findRoots(double a, double b, double c) {
        double delta = b * b - 4 * a * c;
        double[] parameters = new double[] {2 * a, -b};

        return calculateRoots(delta, parameters);
    }

    private static double[] calculateRoots(double delta, double[] params) {
        if (delta < 0)
            return new double[0];
        else if (delta == 0)
            return new double[] { params[1] / params[0] };
        else
            return new double[] {
                    ( params[1] + sqrt(delta) ) / params[0],
                    ( params[1] - sqrt(delta) ) / params[0]
            };
    }

}

