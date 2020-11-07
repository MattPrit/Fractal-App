/*
 * PROJECT II: Newton.java
 *
 * This file contains a template for the class Newton. Not all methods are
 * implemented. Make sure you have carefully read the project formulation
 * before starting to work on this file.
 *
 * In this class, you will create a basic Java object responsible for
 * performing the Newton-Raphson root finding method on a given polynomial
 * f(z) with complex co-efficients. The formulation outlines the method, as
 * well as the basic class structure, details of the instance variables and
 * how the class should operate.
 *
 * Remember not to change the names, parameters or return types of any
 * variables in this file! You should also test this class using the main()
 * function.
 *
 * The function of the methods and instance variables are outlined in the
 * comments directly above them.
 */

class MandelbrotIterator extends Iterator{

    // ========================================================
    // Constructor function.
    // ========================================================

    /**
     * Basic constructor
     */
    public MandelbrotIterator(int maxIterations) {
        super(maxIterations);
    }

    // ========================================================
    // Mandelbrot iteration method
    // ========================================================

    /**
     * Given a complex number p, apply iterative Mandelbrot process
     *
     * One of two things may occur:
     *
     *   - point is found to not be in the Mandelbrot set after numIterations
     *    iterations. Error value set to 1
     *
     *   - iterative process hasn't diverged after numIterations iterations
     *     i.e. point isn't definitely not in Mandelbrot set; error value = 0
     *
     * @param p  The initial starting point for the algorithm.
     */

    public int[] iterate(Complex p) {
        int result[] = new int[2];
        result[0] = 0;
        result[1] = 1;
        Complex z_n = new Complex(0,0);
        Complex z_n_temp = new Complex();
        //Complex c = new Complex(p.getReal(), p.getImag());

        double x = p.getReal();
        double y = p.getImag();

        double a = (x - 0.25) * (x - 0.25) + y * y;
        double b = a * (a + (x - 0.25));
        double c = (x + 1) * (x + 1) + y * y;

        if ((b < (0.25 * y * y)) || (c < 0.0625)) {  // Checking whether point lies within main cardiod bulb or period 2 bulb parameterised by b=0.15 and c=0.0625 respectively
        }
        else for (int n = 1; n <= this.maxIterations; n++) {
            result[1] = n;

            if (z_n.abs2() > 4) {
                result[0] = 1;
                break;
            }

            z_n_temp.setReal(z_n.getReal());
            z_n_temp.setImag(z_n.getImag());
            z_n = z_n.multiply(z_n).add(p);
            if (z_n_temp.add(z_n.minus()).abs2() <= TOL*TOL) {
                break;
            }
        }
        return result;

    }


    /*
    Reimplementation of iteration method allowing for management of maximum number of iterations on a case-by-case basis
     - for use in generating bhuddabrot fractals in nebula mode
     */
    public int[] iterate(Complex p, int maxIterations) {
        int result[] = new int[2];
        result[0] = 0;
        result[1] = 1;
        Complex z_n = new Complex(0,0);
        Complex z_n_temp = new Complex();
        //Complex c = new Complex(p.getReal(), p.getImag());

        double x = p.getReal();
        double y = p.getImag();

        double a = (x - 0.25) * (x - 0.25) + y * y;
        double b = a * (a + (x - 0.25));
        double c = (x + 1) * (x + 1) + y * y;

        if ((b < (0.25 * y * y)) || (c < 0.0625)) {  // Checking whether point lies within main cardiod bulb or period 2 bulb parameterised by b=0.15 and c=0.0625 respectively
        }
        else for (int n = 1; n <= maxIterations; n++) {
            result[1] = n;

            if (z_n.abs2() > 4) {
                result[0] = 1;
                break;
            }

            z_n_temp.setReal(z_n.getReal());
            z_n_temp.setImag(z_n.getImag());
            z_n = z_n.multiply(z_n).add(p);
            if (z_n_temp.add(z_n.minus()).abs2() <= TOL*TOL) {
                break;
            }
        }
        return result;

    }

    // ========================================================
    // Tester function.
    // ========================================================

    public static void main(String[] args) {

        MandelbrotIterator f = new MandelbrotIterator(100);

        Complex p = new Complex(0.1,0);
        System.out.println(p.abs2());
        System.out.println((double)1/16);
        System.out.println(p.abs2() <= (double)1/16);
        f.iterate(p);

    }
}