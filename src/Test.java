import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Test {

    private JFrame appFrame;
    private JLayeredPane mainMenu;
    NewtonFractal fractal;

    public Test() {

        this.fractal = new NewtonFractal();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                appFrame = new JFrame("Fractal App");
                appFrame.setIconImage(new ImageIcon(getClass().getResource("/Resources/Images/Iconv2.png")).getImage());
                appFrame.setSize(800, 480);
                appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }

                mainMenu = getMainMenu();

                appFrame.getContentPane().add(BorderLayout.CENTER, mainMenu);
                appFrame.setVisible(true);

            }
        });

    }

    private JLayeredPane getMainMenu() {

        JLayeredPane mainMenu = new JLayeredPane();
        mainMenu.setSize(this.appFrame.getSize());

        JPanel backgroundFrame = new JPanel();
        ImageIcon backgroundImage = new ImageIcon(getClass().getResource("/Resources/Images/background1.png"));
        JLabel menuBackground = new JLabel(backgroundImage);
        backgroundFrame.setBounds(0, -4, backgroundImage.getIconWidth(), backgroundImage.getIconHeight());
        backgroundFrame.add(menuBackground);

        JPanel fractalSelectionBox = new JPanel();
        fractalSelectionBox.setBorder(BorderFactory.createTitledBorder("Choose fractal type"));
        fractalSelectionBox.setSize(this.appFrame.getSize());
        fractalSelectionBox.setOpaque(false);
        int x = (appFrame.getWidth() - 420) / 2;
        int y = (appFrame.getHeight() - 420) / 2;
        fractalSelectionBox.setBounds(x,y,400,400);
        fractalSelectionBox.setLayout(new GridLayout(0, 1));

        JButton button = new JButton("Click me!");
        button.addActionListener((e) -> {
            fractalGetter.execute();
            this.appFrame.revalidate();
        });

        fractalSelectionBox.add(button);

        mainMenu.add(backgroundFrame, Integer.valueOf(0));
        mainMenu.add(new JPanel(), Integer.valueOf(1));
        mainMenu.add(fractalSelectionBox, Integer.valueOf(1));
        mainMenu.setVisible(true);

        return  mainMenu;

    }

    private SwingWorker fractalGetter = new SwingWorker<BufferedImage, Void>() {
        @Override
        protected BufferedImage doInBackground() throws Exception {

            fractal.setupFractal();
            fractal.createFractal();
            BufferedImage fractalImage = fractal.getFractalImage();
            return fractalImage;

        }

        @Override
        public void done() {

        }
    };

    // ========================================================
    // Tester function.
    // ========================================================

    public static void main(String[] args) {

        Fractal f = new MandelbrotFractal(new Complex(-0.44011979677524465, -0.5713273046271565),200, 200, 86.49755859375, 150, ColorStyle.RAINBOW);
        f.setupFractal();

        f.createFractal();
        f.saveFractal("Regular Image.png");

        f.supersampleTest();
        f.saveFractal("Supersampled Image.png");

    }

}