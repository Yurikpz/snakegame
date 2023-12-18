import javax.swing.*;
import java.awt.*;

public class GameOverScreen extends JPanel {

    private int score;

    public GameOverScreen(int score) {
        this.score = score;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Desenhe a tela de Game Over
        g.setColor(Color.WHITE);
        g.drawString("Game Over - Score: " + score, 100, 100);
    }
}
