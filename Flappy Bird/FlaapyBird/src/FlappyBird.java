import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener , KeyListener {
    int boardWidth = 360;
    int boardHeight = 620;

    //Image
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    //Bird
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img ;
        }
    }

    //pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipewidth = 64 ;
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipewidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false ;

        Pipe(Image img) {
            this.img = img;
        }
    }

    // game logic
    Bird bird;
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer gameloop;
    Timer placePipesTimer;
    boolean gameOver = false;
    double score = 0 ;

    FlappyBird(){
        setPreferredSize(new Dimension(boardWidth , boardHeight));
        //setBackground(Color.BLUE);
        setFocusable(true);
        addKeyListener(this); 

        //load images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        //bird
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        //place pipes timer
        placePipesTimer = new Timer(1500 , new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start(); 

        //game timer 
        gameloop = new Timer(1000/60, this); //1000/60 = 16.6
        gameloop.start();
    }

    public void placePipes() {
        //(0-1) * pipeHeight/2 -> (0-256)
        //128
        //0 - 128 - (0-256) --> pipeHeight/4 -> 3/4 pipeHeight

        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openningSpace = boardHeight/4;


        Pipe toPipe= new Pipe(topPipeImg);
        toPipe.y = randomPipeY;
        pipes.add(toPipe);

        Pipe bottompipe = new Pipe(bottomPipeImg);
        bottompipe.y = toPipe.y + pipeHeight + openningSpace ;
        pipes.add(bottompipe);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        
        //background
        g.drawImage(backgroundImg, 0, 0, boardWidth,boardHeight,null);
        
        //bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width,bird.height,null);

        //pipes
        for(int i = 0 ; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN,32));
        if (gameOver) {
            g.drawString("Game Over : " + String.valueOf((int) score), 10 , 35);
        }
        else {
            g.drawString(String.valueOf((int) score), 10 ,  35);
        }
    }

    public void move() {
        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y,0);

        //pipes
        for(int i = 0; i<pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if(!pipe.passed && bird.x > pipe.x + pipewidth) {
                pipe.passed = true;
                score += 0.5;
            }

            if(collision(bird, pipe)) {
                gameOver = true ;
            }
        }

        if(bird.y > boardHeight) {
            gameOver = true;
        }
    }

    public boolean collision(Bird a, Pipe b) {
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x && 
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver) {
            placePipesTimer.stop();
            gameloop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
        }
    }

    // donot want
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}




