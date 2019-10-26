import java.awt.*;

public class JuliaFractal extends Fractal {

    public JuliaFractal(Complex centre, int imageWidth, int imageHeight, double zoomLevel, int maxIterations, ColorStyle col, int order, Complex c) {
        super(centre, imageWidth, imageHeight, zoomLevel, maxIterations);
        this.colors        = new Color[3];
        this.colors[0]     = Color.BLUE;
        this.colors[1]     = Color.WHITE;
        this.colors[2]     = Color.RED;
        this.colorStyle = col;
        this.iterator = new JuliaIterator(order, c, maxIterations);
        this.iterator.setOrder(2);
    }

    public static void main(String[] args) {
        JuliaFractal f = new JuliaFractal(new Complex(0.9,0.25), 4000, 4000, 4.0, 500, ColorStyle.THREECOLOUR, 2, new Complex(0.585, 0.31));
        f.createFractal();
        f.saveFractal("Julia Test - dark.png");
    }
}
