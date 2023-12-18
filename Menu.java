import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Menu extends JFrame implements ActionListener {
    private JLabel startLabel;
    private ImageIcon startImage;

    public Menu() {
        setTitle("Snake Game Menu");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ImageIcon backgroundImage = new ImageIcon("MenuBackground.png");
        Image img = backgroundImage.getImage().getScaledInstance(1280, 720, Image.SCALE_SMOOTH);
        ImageIcon scaledImage = new ImageIcon(img);

        JLabel backgroundLabel = new JLabel(scaledImage);
        backgroundLabel.setLayout(null); // Usando o layout nulo

        startImage = new ImageIcon("StartGameBackground.png");
        startLabel = new JLabel(startImage);
        startLabel.setBounds(500, 470, startImage.getIconWidth(), startImage.getIconHeight()); // Definindo coordenadas
        startLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                startButtonClicked(evt);
            }
        });

        backgroundLabel.add(startLabel);

        setContentPane(backgroundLabel);
    }

    private void startButtonClicked(java.awt.event.MouseEvent evt) {
        SnakeGame snakeGame = new SnakeGame();
        snakeGame.setVisible(true);
        dispose();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Implementation not needed for this example
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Menu menu = new Menu();
            menu.setVisible(true);
        });
    }
}
