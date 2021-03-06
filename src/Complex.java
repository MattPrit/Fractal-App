/*
 * This class implements a new type for complex numbers and corresponding
 * arithmetic operations.
 */

public class Complex {
    /**
     * Real part x of the complex number x+iy.
     */
    private double x;

    /**
     * Imaginary part y of the complex number x+iy.
     */
    private double y;

    // ========================================================
    // Constructor functions.
    // ========================================================

    /**
     * Constructor: Initializes x, y.
     *
     * @param x  The initial value of the real component.
     * @param y  The initial value of the imaginary component.
     */
    public Complex(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Real constructor - initialises with a real number.
     *
     * @param x  The initial real number to initialise to.
     */
    public Complex(double x) {
        this.x = x;
        this.y = 0;
    }

    /**
     * Default constructor; initialiase x and y to zero.
     */
    public Complex() {
        this.x = 0;
        this.y = 0;
    }

    // ========================================================
    // Accessor and mutator methods.
    // ========================================================

    /**
     * Accessor Method: get real part of the complex number.
     *
     * @return The real part of the complex number.
     */
    public double getReal() {
        return x;
    }

    /**
     * Accessor Method: get imaginary part of the complex number.
     *
     * @return The imaginary part of the complex number
     */
    public double getImag() {
        return y;
    }

    /**
     * Mutator method: set the real part of the complex number.
     *
     * @param x  The replacement real part of z.
     */
    public void setReal(double x) {
        this.x = x;
    }

    /**
     * Mutator method: set the imaginary part of the complex number.
     *
     * @param y  The replacement imaginary part of z.
     */
    public void setImag(double y) {
        this.y = y;
    }

    // ========================================================
    // Operations and functions with complex numbers.
    // ========================================================

    /**
     * Converts the complex number to a string. This is an important method as
     * it allows us to print complex numbers using System.out.println.
     *
     * @return A string describing the complex number.
     */
    public String toString() {
        if (y < 0.0)
            return x + "-" + Math.abs(y) + "i";
        else
            return x + "+" + Math.abs(y) + "i";
    }

    public String toShortString() {
        if (y < 0.0)
            return String.format("%10.2f - %10.2fi", x, Math.abs(y));
        else
            return String.format("%10.2f + %10.2fi", x, Math.abs(y));
    }

    /**
     * Computes square of the absolute value (magnitude) of the complex number
     * (i.e. |z|^2).
     *
     * @return The square of the absolute value of this complex number.
     */
    public double abs2() {
        return x*x + y*y;
    }

    /**
     * Computes absolute value (magnitude) of the complex number.
     *
     * @return The absolute value of the complex number.
     */
    public double abs() {
        return Math.sqrt(this.abs2());
    }

    /**
     * Computes the argument of the complex number
     *
     * @return The argument of the complex number
     */
    public double arg() { return Math.atan2(this.getImag(), this.getReal()); }

    /**
     * Calculates the conjugate of this complex number.
     *
     * @return A Complex contaning the conjugate.
     */
    public Complex conjugate() {
        return new Complex(this.x,-this.y);
    }

    /**
     * Adds a complex number to this one.
     *
     * @param b  The complex number to add to this one.
     * @return   The sum of this complex number with b.
     */
    public Complex add(Complex b) {
        return new Complex(this.x + b.x, this.y + b.y);
    }

    /**
     * Calculates -z.
     *
     * @return The complex number -z = -x-iy
     */
    public Complex minus() {
        return new Complex(-this.x,-this.y);
    }

    /**
     * Multiplies this complex number by a constant.
     *
     * @param alpha   The constant to multiply by.
     * @return        The product of alpha with z.
     */
    public Complex multiply(double alpha) {
        return new Complex(alpha*this.x, alpha*this.y);
    }

    /**
     * Multiplies this complex number by another complex number.
     *
     * @param b   The complex number to multiply by.
     * @return    The product of b with z.
     */
    public Complex multiply(Complex b) {
        return new Complex(this.x*b.x - this.y*b.y, this.x*b.y + this.y*b.x);
    }

    /**
     * Divide this complex number by another.
     *
     * @param b  The complex number to divide by.
     * @return   The division z/b.
     */
    public Complex divide(Complex b) {
        double answerDenominator = b.abs2();
        Complex answerNumerator = this.multiply(b.conjugate());
        Complex answer = answerNumerator.multiply(1.0/answerDenominator);
        return answer;
    }

    /**
     * Raise this complex number to a real power
     *
     * @param n   The power to be raised to
     * @return    The value of z^n
     */
    public Complex pow(double n) {

        if ((this.getReal() == 0.0) && (this.getImag() == 0.0)) {
            return this;
        }

        Complex answer = new Complex();
        double arg = this.arg();

        answer.setImag(Math.sin(n * arg));
        answer.setReal(Math.cos(n * arg));

        double magnitude = Math.pow(this.abs(), n);

        return answer.multiply(magnitude);
    }

    /**
     * Raise this complex number to an integer power
     *
     * @param n   The power to be raised to
     * @return    The value of z^n
     */
    public Complex pow(int n) {

        if ((this.getReal() == 0.0) && (this.getImag() == 0.0)) {
            return this;
        }

        if (n == 0) {return new Complex(1);}

        Complex answer = new Complex(this.getReal(), this.getImag());

        for (int i = 0; i < n; i++) {
            answer = answer.multiply(answer);
        }

        return answer;
    }
}