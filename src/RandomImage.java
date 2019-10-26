import java.io.*;
import java.awt.image.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Random;

class RandomImage {

    public static final int MAXITER = 10;

    /**
     * The width of the square in the complex plane to examine.
     */
    private double width;

    /**
     * A two dimensional array holding the colours of the plot.
     */
    private Color[][] colors;

    /**
     * A flag indicating the type of plot to generate. If true, we choose
     * darker colors if a particular root takes longer to converge.
     */
    private boolean colorIterations;

    /**
     * A standard Java object which allows us to store a simple image in
     * memory. This will be set up by setupFractal -- you do not need to worry
     * about it!
     */
    private BufferedImage fractal;

    /**
     * This object is another standard Java object which allows us to perform
     * basic graphical operations (drawing lines, rectangles, pixels, etc) on
     * the BufferedImage. This will be set up by setupFractal -- you do not
     * need to worry about it!
     */
    private Graphics2D g2;

    /**
     * Defines the width (in pixels) of the BufferedImage and hence the
     * resulting image.
     */
    private int numPixels; //REMEMBER TO CHANGE THIS BACK TO 400!

    // ========================================================
    // Constructor function.
    // ========================================================

    /**
     * Constructor function which initialises the instance variables
     * above. IMPORTANT: Remember to call setupFractal at the end of this
     * function!!
     *
     * @param numPixels   The width of the square to image.
     */
    public RandomImage(int numPixels) {
        this.numPixels = numPixels;
        setupImage();
    }

    // ========================================================
    // Basic operations.
    // ========================================================

    // ========================================================
    // Fractal generating function.
    // ========================================================

    /**
     * Generate the fractal image. See the colorIterations instance variable
     * for a better description of its purpose.
     */
    public void createImage() {
        double r;
        int ourColor;
        Random rand = new Random();
        for (int i=0; i<numPixels; i++) {
            for (int j=0; j<numPixels; j++){
                r = rand.nextInt(100);
                if (r > 50) {
                    ourColor = 1;
                }
                else {
                    ourColor = 0;
                }
                colorPixel(i , j, ourColor, 1);
            }
        }
    }

    // ========================================================
    // Tester function.
    // ========================================================

    public static void main(String[] args) {

        RandomImage r = new RandomImage(11000);

        r.createImage();
        r.saveImage("Random.png");
    }

    // ====================================================================
    // OTHER FUNCTIONS
    //
    // The rest of the functions in this class are COMPLETE (with the
    // exception of the main function) since they involve quite complex Java
    // code to deal with the graphics. This means they *do not* and *should
    // not* need to be altered! But you should read their descriptions so you
    // know how to use them.
    // ====================================================================

    /**
     * Sets up all the fractal image. Make sure that your constructor calls
     * this function!
     */
    private void setupImage()
    {
        int i, j;

        this.colors       = new Color[2][MAXITER];
        this.colors[0][0] = Color.BLACK;
        this.colors[1][0] = Color.WHITE;

        for (i = 0; i < 2; i++) {
            float[] components = colors[i][0].getRGBComponents(null);
            float[] delta      = new float[3];

            for (j = 0; j < 3; j++)
                delta[j] = 0.8f*components[j]/MAXITER;

            for (j = 1; j < MAXITER; j++) {
                float[] tmp  = colors[i][j-1].getRGBComponents(null);
                colors[i][j] = new Color(tmp[0]-delta[0], tmp[1]-delta[1],
                        tmp[2]-delta[2]);
            }
        }

        fractal = new BufferedImage(numPixels, numPixels, BufferedImage.TYPE_INT_RGB);
        g2      = fractal.createGraphics();
    }

    /**
     * Colors a pixel in the image.
     *
     * @param i          x-axis co-ordinate of the pixel located at (i,j)
     * @param j          y-axis co-ordinate of the pixel located at (i,j)
     * @param ourColor  An integer between 0 and 4 inclusive indicating the
     *                   root number.
     * @param numIter    Number of iterations at this root.
     */
    private void colorPixel(int i, int j, int ourColor, int numIter)
    {
        if (colorIterations)
            g2.setColor(colors[ourColor][numIter-1]);
        else
            g2.setColor(colors[ourColor][0]);
        g2.fillRect(i,j,1,1);
    }

    /**
     * Saves the fractal image to a file.
     *
     * @param fileName  The filename to save the image as. Should end in .png.
     */
    public void saveImage(String fileName) {
        try {
            File outputfile = new File(fileName);
            ImageIO.write(fractal, "png", outputfile);
        } catch (IOException e) {
            System.out.println("I got an error trying to save! Maybe you're out of space?");
        }
    }
}