public class BurningShipIterator extends Iterator {

    public BurningShipIterator(int maxIterations) {
        super(maxIterations);
    }

    @Override
    int[] iterate(Complex p) {
        int[] result = new int[2];

        Complex z_n = new Complex();
        result[1] = 1;

        while (result[1] < this.maxIterations && z_n.abs2() < 4) {

            z_n.setReal(Math.abs(z_n.getReal()));
            z_n.setImag(Math.abs(z_n.getImag()));

            z_n = z_n.multiply(z_n).add(p);

            result[1] ++;
        }
        if (result[1] == this.maxIterations) {
            result[0] = 0;
        }
        else {
            result[0] = 1;
        }
        return result;
    }
}
