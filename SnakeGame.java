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

    private Clip backgroundMusic;

    private BackgroundPanel gamePanel;
    private JButton restartButton;

    private boolean paused = false;

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

        gamePanel = new BackgroundPanel();
        add(gamePanel);

        restartButton = new JButton("");
        restartButton.addActionListener(this);
        restartButton.setFocusable(false);
        restartButton.setVisible(false);

        ImageIcon restartButtonIcon = new ImageIcon("RestartBackground.png");
        restartButton.setIcon(restartButtonIcon);
        restartButton.setHorizontalTextPosition(JButton.CENTER);
        restartButton.setVerticalTextPosition(JButton.CENTER);
        restartButton.setBorderPainted(false);
        restartButton.setContentAreaFilled(false);

        restartButton.setBounds(515, 380, restartButton.getPreferredSize().width, restartButton.getPreferredSize().height);
        restartButton.setMargin(new Insets(0, 0, 0, 0));
        restartButton.setVisible(false);
        gamePanel.setLayout(null);
        gamePanel.add(restartButton);
    }

    private void updateBackground() {
        gamePanel.repaint();
    }

    public JButton getRestartButton() {
        return restartButton;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == restartButton) {
            restartGame();
            restartButton.setVisible(false);
        } else if (running && !paused) {
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
                    // Desenha um quadrado no estilo neon para a cabeça da cobra
                    drawNeonSquare(g, snake.get(i).x * 20, snake.get(i).y * 20);
                } else {
                    // Desenha um quadrado no estilo neon para o corpo da cobra
                    drawNeonSquare(g, snake.get(i).x * 20, snake.get(i).y * 20);
                }
            }

            // Desenha a fruta no estilo neon com brilho
            drawNeonFruit(g, fruit.x * 20, fruit.y * 20);

            g.setColor(Color.WHITE);
            g.drawString("Score: " + score, 30, 30);
        } else {
             g.setColor(Color.WHITE);
        Font gameOverFont = new Font("True Lies", Font.BOLD, 30);
        g.setFont(gameOverFont);

        // Obtenha a métrica da fonte para determinar a largura do texto
        FontMetrics fontMetrics = g.getFontMetrics(gameOverFont);
        String scoreText = "Score: " + score;
        int textWidth = fontMetrics.stringWidth(scoreText);

        // Calcule a posição x para centralizar o texto na tela
        int x = (getWidth() - textWidth) / 2;

        // Desenhe o texto com uma borda ao redor
        g.setColor(new Color(135, 206, 250)); // Azul claro
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                g.drawString(scoreText, x + i, 360 + j);
            }
        }

        g.setColor(Color.WHITE);
        g.drawString(scoreText, x, 360);
        stopBackgroundMusic();
    }
}

    private void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }

    private void drawNeonSquare(Graphics g, int x, int y) {
        // Cria um gradiente para o brilho externo
        GradientPaint gradient = new GradientPaint(x, y, new Color(255, 255, 255, 100), x + 20, y + 20, new Color(255, 255, 255, 0));

        // Pinta o quadrado principal com a cor do estilo neon
        g.setColor(new Color(0, 255, 255));
        g.fillRect(x, y, 20, 20);

        // Pinta o brilho externo usando o gradiente
        ((Graphics2D) g).setPaint(gradient);
        g.fillRect(x - 1, y - 1, 22, 22);
    }

    private void drawNeonFruit(Graphics g, int x, int y) {
        // Cria um gradiente para o brilho externo
        GradientPaint gradient = new GradientPaint(x, y, new Color(255, 255, 255, 100), x + 20, y + 20, new Color(255, 255, 255, 0));

        // Pinta o quadrado principal com a cor do estilo neon
        g.setColor(new Color(255, 0, 0)); // Cor vermelha para a fruta
        g.fillRect(x, y, 20, 20);

        // Pinta o brilho externo usando o gradiente
        ((Graphics2D) g).setPaint(gradient);
        g.fillRect(x - 1, y - 1, 22, 22);
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
        } else if (key == KeyEvent.VK_P) {
            if (!running) {
                restartGame();
                running = true;
                paused = false;
                timer.start();
            } else {
                paused = !paused;
                if (paused) {
                    timer.stop();
                } else {
                    timer.start();
                }
            }
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void move() {
        if (!paused) {
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

            // Altere a imagem de fundo para a tela de Game Over
            gamePanel.setBackgroundImage(new ImageIcon("GameOverBackground.png").getImage());
            updateBackground(); // Chama o método para atualizar o BackgroundPanel
        }
    }

    private void restartGame() {
        snake.clear();
        snake.add(new Point(5, 5));
        direction = KeyEvent.VK_RIGHT;
        running = true;
        fruit = new Point(10, 10);
        score = 0;

        // Altere a imagem de fundo de acordo com a tela principal do jogo
        gamePanel.setBackgroundImage(new ImageIcon("Background.png").getImage());

        backgroundMusic.setFramePosition(0);
        backgroundMusic.start();

        timer.start();

        updateBackground(); // Chama o método para atualizar o BackgroundPanel
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

    public BackgroundPanel() {
        this.backgroundImage = new ImageIcon("Background.png").getImage();
    }

    public void setBackgroundImage(Image backgroundImage) {
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
