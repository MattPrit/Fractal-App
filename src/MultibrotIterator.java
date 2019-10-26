public class MultibrotIterator extends Iterator {

    /**
     * Basic constructor
     */
    public MultibrotIterator(double power, int maxIterations) {
        super(maxIterations);
        this.power = power;
    }

    // ========================================================
    // Mandelbrot iteration method
    // ========================================================

    /**
     * Given a complex number p, apply iterative Mandelbrot process
     *
     * One of two things may occur:
     *
     *   - point is found to not be in the Multibrot set after numIterations
     *    iterations. Error value set to 1
     *
     *   - iterative process hasn't diverged after numIterations iterations
     *     i.e. point isn't definitely not in Multibrot set; error value = 0
     *
     * @param p  The initial starting point for the algorithm.
     * @return an array containing the determined output value and number of iterations needed for decision
     */

    public int[] iterate(Complex p) {
        int[] result = new int[2];
        Complex z_n = new Complex(0,0);
        Complex z_n_temp = new Complex();
        Complex c = new Complex(p.getReal(), p.getImag());

        /**
         * if (p.abs2() <= (double)1/16) {
         *             err = 0;
         *         }
         *         else
         */

        for (int n = 1; n <= this.maxIterations; n++) {
            result[1] = n;
            result[0] = 0;

            if (z_n.abs2() > 4) {
                result[0] = 1;
                break;
            }
            z_n_temp.setReal(z_n.getReal());
            z_n_temp.setImag(z_n.getImag());
            z_n = z_n.pow(this.power).add(c);
            //z_n = z_n.multiply(z_n.multiply(z_n)).add(c);
            if (z_n_temp.add(z_n.minus()).abs2() <= TOL * TOL) {
                //result[0] = 0;
                break;
            }
            //result[0] = 0;
        }
        return result;
    }

    public static void main(String[] args) {

        MultibrotIterator m = new MultibrotIterator(2.5, 10);
        m.iterate(new Complex(-0.279));
        System.out.println(m.err);

    }
}
