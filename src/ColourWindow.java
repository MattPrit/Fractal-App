import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ColourWindow {

    private Color colour;

    public Color getColour() {
        return this.colour;
    }

    public void setColour(Color colour) {
        this.colour = colour;
    }

    public ColourWindow(Color colour) {

        JColorChooser colourChooser = new JColorChooser(colour);
        JFrame window = new JFrame();
        colourChooser.setPreviewPanel(new JPanel());
        this.setColour(JColorChooser.showDialog(window, "Pick a colour", colour));

    }

    public static void main(String[] args) {

        ColourWindow cw = new ColourWindow(Color.RED);
        System.out.println(cw.getColour().toString());

    }
}
