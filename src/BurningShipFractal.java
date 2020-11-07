import java.awt.*;

public class BurningShipFractal extends Fractal {

    public BurningShipFractal(Complex centre, int imageWidth, int imageHeight, double zoomLevel, int maxIterations, ColorStyle col) {
        super(centre, imageWidth, imageHeight, zoomLevel, maxIterations);
        this.colors = new Color[3];
        this.colors[0] = Color.BLUE;
        this.colors[1] = Color.WHITE;
        this.colors[2] = Color.RED;
        this.colorStyle = col;
        this.iterator = new BurningShipIterator(maxIterations);
        this.iterator.setOrder(2);
        setupFractal();
    }


    /**
     * Colors a pixel in the image. Overrides method in fractal to account for inversion of desired image.
     *
     * @param x          x-axis co-ordinate of the pixel located at (i,j)
     * @param y          y-axis co-ordinate of the pixel located at (i,j)
     * @param ourColor   An integer indicating a base colour in this.colors.
     * @param numIterations    Number of iterations at this point.
     */
    public void colorPixel(int x, int y, int ourColor, int numIterations) {
        y = imageHeight-y-1;
        super.colorPixel(x, y, ourColor, numIterations);
    }

    /*public void colorPixel(int x, int y, float hue) {
        y = imageHeight-y-1;
        super.colorPixel(x, y, hue);
    }*/

}
