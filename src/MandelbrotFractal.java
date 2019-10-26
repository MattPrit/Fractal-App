import java.awt.*;

class MandelbrotFractal extends Fractal {

    /**
     * Constructor function which initialises the instance variables and
     * sets up fractal
     *
     * @param centre  The centre of the square to image.
     * @param imageWidth  The width of the image to be produced.
     * @param imageHeight  The height of the image to be produced
     * @param zoomLevel  Determines the zoom level of image to be produced
     */
    public MandelbrotFractal(Complex centre, int imageWidth, int imageHeight, double zoomLevel, int maxIterations, ColorStyle col) {
        super(centre, imageWidth, imageHeight, zoomLevel, maxIterations);
        this.colors        = new Color[3];
        this.colors[0]     = Color.BLACK;
        this.colors[1]     = Color.YELLOW;
        this.colors[2]     = Color.BLUE;
        this.colorStyle = col;
        this.iterator = new MandelbrotIterator(this.maxIterations);
        this.iterator.setOrder(2);
    }

    // ========================================================
    // Tester function.
    // ========================================================

    public static void main(String[] args) {
        MandelbrotFractal f = new MandelbrotFractal(new Complex(0,0), 800, 400, 1.0, 100, ColorStyle.NO_SHADING);
        f.createFractal();
        f.saveFractal("fractalImage-light.png");
    }
}