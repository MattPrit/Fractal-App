import java.awt.*;

public class MultibrotFractal extends Fractal {

    private double power;

    /**
     * Constructor function which initialises the instance variables and
     * sets up fractal
     *
     * @param centre        The centre of the square to image.
     * @param imageWidth    The width of the image to be produced.
     * @param imageHeight   The height of the image to be produced
     * @param zoomLevel     Determines the zoom level of image to be produced
     * @param maxIterations The maximum number of iterations for iterative process
     * @param col           Determines how the fractal image should be coloured
     */
    public MultibrotFractal(Complex centre, int imageWidth, int imageHeight, double zoomLevel, int maxIterations, ColorStyle col, double power) {
        super(centre, imageWidth, imageHeight, zoomLevel, maxIterations);
        this.colors        = new Color[3];
        this.colors[0]     = Color.BLACK;
        this.colors[1]     = Color.YELLOW;
        this.colors[2]     = Color.RED;
        this.setColorStyle(col);
        this.power = power;
        this.iterator = new MultibrotIterator(power, maxIterations);
        this.iterator.setOrder(2);
        setupFractal();
    }

    public static void main(String[] args) {

        long start1 = System.currentTimeMillis();
        MultibrotFractal f = new MultibrotFractal(new Complex(0.6769979932827924,0.5285027992009677), 8000, 8000, 4.841938267250439E9, 400, ColorStyle.THREECOLOUR, 3.0);
        f.createFractal();
        f.saveFractal("testMultiFractalImage-dark.png");
        long time1 = System.currentTimeMillis() - start1;
        System.out.println(time1);
    }
}
