import java.awt.*;

public class TricornFractal extends Fractal {
    /**
     * Constructor function which initialises instance variables and
     * sets up for the creation of fractalImage
     *
     * @param centre        The centre of the square to image.
     * @param imageWidth    The width of the image to be produced.
     * @param imageHeight   The height of the image to be produced
     * @param zoomLevel     Determines the zoom level of image to be produced
     * @param maxIterations The maximum number of iterations per pixel in image creation process
     */
    public TricornFractal(Complex centre, int imageWidth, int imageHeight, double zoomLevel, int maxIterations) {
        super(centre, imageWidth, imageHeight, zoomLevel, maxIterations);
        this.colors        = new Color[3];
        this.colors[0]     = Color.RED;
        this.colors[1]     = Color.YELLOW;
        this.colors[2]     = Color.BLUE;
        this.colorStyle = ColorStyle.TWOCOLOUR;
        this.iterator = new TricornIterator(this.maxIterations);
        this.iterator.setOrder(2);
        setupFractal();
    }
}
