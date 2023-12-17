import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;

public class SnakeGame extends JFrame implements ActionListener, KeyListener {

    private static final long serialVersionUID = 1L;

    private Timer timer;
    private ArrayList<Point> snake;
    private int direction;
    private boolean running;
    private Point fruit;
    private int score;

    private Image headImage;
    private Image bodyImage;
    private Image fruitImage;
    private Image backgroundImage;

    private Clip backgroundMusic;

    private BackgroundPanel gamePanel;
    private JButton restartButton;

    public SnakeGame() {
        setTitle("Snake Game");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        snake = new ArrayList<>();
        snake.add(new Point(5, 5));
        direction = KeyEvent.VK_RIGHT;
        running = true;

        fruit = new Point(10, 10);

        headImage = new ImageIcon("head.png").getImage();
        bodyImage = new ImageIcon("body.png").getImage();
        fruitImage = new ImageIcon("fruit.png").getImage();
        backgroundImage = new ImageIcon("Background.png").getImage();

        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("background_music.wav"));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioInputStream);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        timer = new Timer(100, this);
        timer.start();

        addKeyListener(this);
        setFocusable(true);

        gamePanel = new BackgroundPanel(backgroundImage);
        add(gamePanel);

        restartButton = new JButton("Restart");
        restartButton.addActionListener(this);
        restartButton.setFocusable(false);
        restartButton.setVisible(false);

        // Configuração do plano de fundo do botão
        ImageIcon restartButtonIcon = new ImageIcon("RestartBackground.png");
        restartButton.setIcon(restartButtonIcon);
        restartButton.setHorizontalTextPosition(JButton.CENTER);
        restartButton.setVerticalTextPosition(JButton.CENTER);
        restartButton.setBorderPainted(false);
        restartButton.setContentAreaFilled(false);

        // Configuração do tamanho e posição do botão
        restartButton.setBounds(580, 380, restartButton.getPreferredSize().width, restartButton.getPreferredSize().height);
        restartButton.setMargin(new Insets(0, 0, 0, 0));
        restartButton.setVisible(false);
        gamePanel.setLayout(null);
        gamePanel.add(restartButton);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == restartButton) {
            restartGame();
            restartButton.setVisible(false);
        } else if (running) {
            move();
            checkCollision();
            checkFruit();
            repaint();
        }
    }

    public void draw(Graphics g) {
        if (running) {
            for (int i = 0; i < snake.size(); i++) {
                if (i == 0) {
                    g.drawImage(headImage, snake.get(i).x * 20, snake.get(i).y * 20, this);
                } else {
                    g.drawImage(bodyImage, snake.get(i).x * 20, snake.get(i).y * 20, this);
                }
            }

            g.drawImage(fruitImage, fruit.x * 20, fruit.y * 20, this);

            g.setColor(Color.WHITE);
            g.drawString("Score: " + score, 10, 20);
        } else {
            g.setColor(Color.WHITE);
            g.drawString("Game Over - Score: " + score, 580, 360);
            stopBackgroundMusic();
        }
    }

    private void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) && (direction != KeyEvent.VK_RIGHT)) {
            direction = KeyEvent.VK_LEFT;
        } else if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) && (direction != KeyEvent.VK_LEFT)) {
            direction = KeyEvent.VK_RIGHT;
        } else if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) && (direction != KeyEvent.VK_DOWN)) {
            direction = KeyEvent.VK_UP;
        } else if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) && (direction != KeyEvent.VK_UP)) {
            direction = KeyEvent.VK_DOWN;
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void move() {
        Point head = snake.get(0);
        Point newHead;

        if (direction == KeyEvent.VK_LEFT) {
            newHead = new Point(head.x - 1, head.y);
        } else if (direction == KeyEvent.VK_RIGHT) {
            newHead = new Point(head.x + 1, head.y);
        } else if (direction == KeyEvent.VK_UP) {
            newHead = new Point(head.x, head.y - 1);
        } else {
            newHead = new Point(head.x, head.y + 1);
        }

        snake.add(0, newHead);

        if (!checkFruit()) {
            snake.remove(snake.size() - 1);
        }
    }

    public boolean checkFruit() {
        if (snake.get(0).equals(fruit)) {
            Random rand = new Random();
            fruit = new Point(rand.nextInt(39) + 1, rand.nextInt(29) + 1);
            score += 10;
            return true;
        }
        return false;
    }

    public void checkCollision() {
        Point head = snake.get(0);

        // Ajuste para levar em consideração as dimensões reais da área de jogo
        if (head.x < 0 || head.x >= getWidth() / 20 || head.y < 0 || head.y >= getHeight() / 20) {
            running = false;
        }

        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                running = false;
                break;
            }
        }

        if (!running) {
            timer.stop();
            stopBackgroundMusic();
            repaint();
            restartButton.setVisible(true);
        }
    }

    private void restartGame() {
        snake.clear();
        snake.add(new Point(5, 5));
        direction = KeyEvent.VK_RIGHT;
        running = true;
        fruit = new Point(10, 10);
        score = 0;

        backgroundMusic.setFramePosition(0);
        backgroundMusic.start();

        timer.start();

        gamePanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SnakeGame snakeGame = new SnakeGame();
            snakeGame.setVisible(true);
        });
    }
}

class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        SnakeGame snakeGame = (SnakeGame) SwingUtilities.getWindowAncestor(this);
        snakeGame.draw(g);
    }
}
