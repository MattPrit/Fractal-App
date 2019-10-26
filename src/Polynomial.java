public class Polynomial {
    /**
     * An array storing the complex co-efficients of the polynomial.
     */
    Complex[] coeff;

    // ========================================================
    // Constructor functions.
    // ========================================================

    /**
     * General constructor: assigns this polynomial a given set of
     * co-efficients.
     *
     * @param coeff  The co-efficients to use for this polynomial.
     */
    public Polynomial(Complex[] coeff) {
        int i;
        for (i = coeff.length-1; i>=0; i--){
            if (coeff[i].abs2() > 0.0){
                break;
            }
        }
        this.coeff = new Complex[i+1];
        for (int j=0; j<=i; j++){
            this.coeff[j] = coeff[j];
        }
    }

    /**
     * Default constructor: sets the Polynomial to the zero polynomial.
     */
    public Polynomial() {
        this.coeff = new Complex[1];
        this.coeff[0] = new Complex(0);
    }

    // ========================================================
    // Operations and functions with polynomials.
    // ========================================================

    /**
     * Create a string representation of the polynomial.
     *
     * For example: (1.0+1.0i)+(1.0+2.0i)X+(1.0+3.0i)X^2
     */
    public String toString() {
        String string = "";
        string += this.coeff[0];
        for (int i=1; i<=this.coeff.length-1; i++){
            string += " + (" + this.coeff[i] + ")z^" + i  ;
        }
        return string;
    }

    /**
     * Returns the degree of this polynomial.
     */
    public int degree() {
        int degree = this.coeff.length - 1;
        return degree;
    }

    /**
     * Evaluates the polynomial at a given point z.
     *
     * @param z  The point at which to evaluate the polynomial
     * @return   The complex number P(z).
     */
    public Complex evaluate(Complex z) {
        Complex c = new Complex(0);
        for (int i = this.degree(); i >= 0; i--)
            c = coeff[i].add(c.multiply(z));
        return c;
    }

    /**
     * Calculate and returns the derivative of this polynomial.
     *
     * @return The derivative of this polynomial.
     */
    public Polynomial derivative() {
        if (this.degree() == 0) {
            return new Polynomial();
        }
        Complex[] newCoeff = new Complex[coeff.length-1];
        for (int i=0; i<coeff.length-1; i++) {
            //newCoeff[i] = coeff[i+1].multiply(new Complex(i+1));
            newCoeff[i] = coeff[i+1].multiply(i+1);
        }
        Polynomial derivative = new Polynomial(newCoeff);
        return derivative;
    }

    // ========================================================
    // Tester function.
    // ========================================================

    public static void main(String[] args) {
        // Tester method to test Ploynomial methods using P(z)=1+2z+3z^2
        Complex[] testArray = {new Complex(1), new Complex(2), new Complex(3), new Complex(), new Complex()};
        Polynomial test = new Polynomial(testArray);

        System.out.println("Polynomial: P(z) = " + test);
        System.out.println("Polynomial degree: " + test.degree());

        Complex z = new Complex(5);

        System.out.println("P(" + z + ") = " + test.evaluate(z));

        Polynomial testDerivative = test.derivative();

        System.out.println("P'(z) = " + testDerivative);
        System.out.println("Derivative degree: " + testDerivative.degree());
        System.out.println("P'(" + z + ") = " + testDerivative.evaluate(z));
    }
}
