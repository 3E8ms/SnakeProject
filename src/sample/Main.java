package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;


class Node {
    int x;
    int y;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

}


public class Main extends Application {
    // variable
    static int speed = 5;
    static int BLOCK_W = 32;
    static int BLOCK_H = 19;
    static int foodX = 20;
    static int foodY = 20;
    static int BLOCK_SIZE = 40;
    static ArrayList<Node> snake = new ArrayList<>();
    static ArrayList<Node> food1 = new ArrayList<>();
    static ArrayList<Node> food2 = new ArrayList<>();
    static ArrayList<Node> food3 = new ArrayList<>();
    static Dir direction = Dir.right;
    static boolean gameOver = false;
    static boolean welcome = true;
    static Random rand = new Random();
    static int score = 0;
    static int level = 1;
    static boolean pause = false;
    static long lastTick = 0;
    static long lastCount = 0;
    static int highest = 0;
    static int countDown = 30;
    int countconst = 30;
    ImageView pauseimg = new ImageView("image/pause.png");
    ImageView gameoverimg = new ImageView("image/gameover.png");
    ImageView win = new ImageView("image/win.png");



    // tick
    public static void tick(GraphicsContext gc, ArrayList<Node> food, Label lb, Label countdownlb, Label lvlb) {
        Media eatsound = new Media(new File("src/sound/eat.mp3").toURI().toString());
        MediaPlayer eatPlayer = new MediaPlayer(eatsound);
        Media lostsound = new Media(new File("src/sound/fail.mp3").toURI().toString());
        MediaPlayer lostPlayer = new MediaPlayer(lostsound);
        if (score > highest) {
            highest = score;
        }
        lvlb.setText("Level: " + level);
        lb.setText(Integer.toString(score));
        if (level != 3) {
            countdownlb.setText("Time remaining: " + countDown);
        } else {
            countdownlb.setText("Time remaining: unlimited");
        }
        for (int i = snake.size() - 1; i >= 1; i--) {
            snake.get(i).x = snake.get(i - 1).x;
            snake.get(i).y = snake.get(i - 1).y;
        }

        switch (direction) {
            case up:
                snake.get(0).y--;
                if (snake.get(0).y < 0) {
                    gameOver = true;
                    lostPlayer.play();
                }
                break;
            case down:
                snake.get(0).y++;
                if (snake.get(0).y > BLOCK_H - 1) {
                    gameOver = true;
                    lostPlayer.play();
                }
                break;
            case left:
                snake.get(0).x--;
                if (snake.get(0).x < 0) {
                    gameOver = true;
                    lostPlayer.play();
                }
                break;
            case right:
                snake.get(0).x++;
                if (snake.get(0).x > BLOCK_W - 1) {
                    gameOver = true;
                    lostPlayer.play();
                }
                break;

        }

        // eat food
        for (int i = 0; i < food.size(); i++) {
            if (food.get(i).x == snake.get(0).x && food.get(i).y == snake.get(0).y) {
                food.remove(i);
                eatPlayer.play();
                snake.add(new Node(-1, -1));
                score++;
                newFood();
            }
        }


        // self destroy
        for (int i = 1; i < snake.size(); i++) {
            if (snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y) {
                lostPlayer.play();
                gameOver = true;
            }
        }

        // fill
        // background
        Image img = new Image("image/bg.jpg");
        gc.drawImage(img, 0, 0);

        Image foodimg = new Image("image/apple50.png");


        for (Node n : food) {
            gc.drawImage(foodimg, n.x * BLOCK_SIZE, n.y * BLOCK_SIZE);
        }

        Image uph = new Image("image/up.png");
        Image dnh = new Image("image/down.png");
        Image lfh = new Image("image/left.png");
        Image rth = new Image("image/right.png");
        Image body = new Image("image/body.png");

        // snake
        int snakesize = snake.size();
        if (direction == Dir.up) {
            gc.drawImage(uph, snake.get(0).x * BLOCK_SIZE, snake.get(0).y * BLOCK_SIZE, BLOCK_SIZE - 2, BLOCK_SIZE - 2);
        } else if (direction == Dir.down) {
            gc.drawImage(dnh, snake.get(0).x * BLOCK_SIZE, snake.get(0).y * BLOCK_SIZE, BLOCK_SIZE - 2, BLOCK_SIZE - 2);
        } else if (direction == Dir.left) {
            gc.drawImage(lfh, snake.get(0).x * BLOCK_SIZE, snake.get(0).y * BLOCK_SIZE, BLOCK_SIZE - 2, BLOCK_SIZE - 2);
        } else if (direction == Dir.right) {
            gc.drawImage(rth, snake.get(0).x * BLOCK_SIZE, snake.get(0).y * BLOCK_SIZE, BLOCK_SIZE - 2, BLOCK_SIZE - 2);
        }
        for (int i = 1; i < snakesize; i++) {
            gc.setFill(Color.GREEN);
            gc.drawImage(body, snake.get(i).x * BLOCK_SIZE, snake.get(i).y * BLOCK_SIZE, BLOCK_SIZE - 2, BLOCK_SIZE - 2);
        }

    }

    // food
    public static void newFood() {
        start:
        while (true) {
            foodX = rand.nextInt(BLOCK_W);
            foodY = rand.nextInt(BLOCK_H);

            for (Node c : snake) {
                if (c.x == foodX && c.y == foodY) {
                    continue start;
                }
            }
            Node n = new Node(foodX, foodY);
            if (level == 1) {
                food1.add(n);
            } else if (level == 2) {
                food2.add(n);
            } else if (level == 3) {
                food3.add(n);
            }
            break;

        }
    }

    public static void main(String[] args) {
        launch(args);
    }


    public void start(Stage primaryStage) {

        try {
            Media winsound = new Media(new File("src/sound/win.mp3").toURI().toString());
            MediaPlayer winPlayer = new MediaPlayer(winsound);
            resetfood();
            ToolBar tb = new ToolBar();
            Label scorelb = new Label(Integer.toString(score));
            scorelb.setPrefWidth(70);
            Label levellb = new Label("Level: ");
            Label countdownlb = new Label("Time remaining:");
            final Pane leftSpacer = new Pane();
            HBox.setHgrow(
                    leftSpacer,
                    Priority.SOMETIMES
            );

            final Pane rightSpacer = new Pane();
            HBox.setHgrow(
                    rightSpacer,
                    Priority.SOMETIMES
            );
            tb.setPrefHeight(40);

            tb.getItems().add(levellb);
            tb.getItems().add(leftSpacer);
            tb.getItems().add(countdownlb);
            tb.getItems().add(rightSpacer);
            tb.getItems().add(scorelb);

            scorelb.setGraphic(new ImageView("image/apple.png"));


            BorderPane root = new BorderPane();
            root.setTop(tb);
            tb.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

            Canvas c = new Canvas(BLOCK_W * BLOCK_SIZE, BLOCK_H * BLOCK_SIZE);


            Background bg = new Background(new BackgroundImage(new Image("image/bg.jpg"), BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, null, null));
            root.setBackground(bg);

            VBox vb = new VBox();
            GraphicsContext gc = c.getGraphicsContext2D();
            root.setCenter(vb);
            vb.getChildren().add(c);

            Scene scene = new Scene(root, 1280, 800);
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);

            //highest panel
            Text highestlb = new Text("Highest score: " + highest + "\n" + "Your Score: " + score);
            highestlb.setFill(Color.RED);
            highestlb.setFont(Font.font("", FontWeight.BOLD, FontPosture.REGULAR, 20));
            StackPane sp = new StackPane();
            sp.setLayoutX(640);
            sp.setLayoutY(400);
            sp.getChildren().add(gameoverimg);
            sp.getChildren().add(highestlb);
            StackPane.setAlignment(highestlb, Pos.BASELINE_CENTER);

            AnimationTimer at = new AnimationTimer() {
                public void handle(long now) {
                    if (gameOver) {
                        highestlb.setText("Highest score: " + highest + "\n" + "Your Score: " + score);
                        root.getChildren().add(sp);
                        sp.setAlignment(Pos.CENTER);
                        stop();
                    }
                    if (level == 1) {
                        if (lastTick == 0 && welcome == false) {
                            lastTick = now;
                            tick(gc, food1, scorelb, countdownlb,levellb);
                            return;
                        }
                        if (now - lastTick > 1000000000.0 / speed && welcome == false) {
                            lastTick = now;
                            tick(gc, food1, scorelb, countdownlb,levellb);
                        }
                    } else if (level == 2) {
                        if (lastTick == 0 && welcome == false) {
                            lastTick = now;
                            tick(gc, food2, scorelb, countdownlb,levellb);
                            return;
                        }

                        if (now - lastTick > 1000000000.0 / speed && welcome == false) {
                            lastTick = now;
                            tick(gc, food2, scorelb, countdownlb,levellb);
                        }
                    } else if (level == 3) {
                        if (lastTick == 0 && welcome == false) {
                            lastTick = now;
                            tick(gc, food3, scorelb, countdownlb,levellb);
                            return;
                        }
                        if (now - lastTick > 1000000000.0 / speed && welcome == false) {
                            lastTick = now;
                            tick(gc, food3, scorelb, countdownlb,levellb);
                        }
                    }
                }
            };


            AnimationTimer cd = new AnimationTimer() {
                public void handle(long now) {
                    if (gameOver) {
                        this.stop();
                        return;
                    }
                    if(level==3 && countDown==countconst-2){
                        stop();
                    }
                    if (now - lastCount >= 1000000000) {
                        lastCount = now;
                        countDown--;
                    }
                    if (countDown == 0) {
                        winPlayer.play();
                        root.getChildren().add(win);
                        if (level == 1) {
                            level = 2;
                            countDown= countconst;
                            lastTick=0;
                            speed = 10;
                        } else if(level==2){
                            level = 3;
                            countDown = countconst;
                            lastTick=0;
                            speed = 15;
                        }
                    }
                    if(countDown==countconst-1){
                        root.getChildren().remove(win);
                    }
                }
            };


            BorderPane bx = new BorderPane();
            bx.setPrefSize(1280, 800);
            Image welimg = new Image("image/welcome.png");
            ImageView welcomelb = new ImageView(welimg);
            bx.getChildren().add(welcomelb);
            if (welcome == true) {
                root.getChildren().add(bx);
            }



            primaryStage.setTitle("Snake Game");
            primaryStage.getIcons().add(new Image("image/smallsnake.png"));
            primaryStage.show();


            // control
            scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
                if (key.getCode() == KeyCode.W || key.getCode() == KeyCode.UP && direction != Dir.down) {
                    direction = Dir.up;
                }
                if (key.getCode() == KeyCode.A || key.getCode() == KeyCode.LEFT && direction != Dir.right) {
                    direction = Dir.left;
                }
                if (key.getCode() == KeyCode.S || key.getCode() == KeyCode.DOWN && direction != Dir.up) {
                    direction = Dir.down;
                }
                if (key.getCode() == KeyCode.D || key.getCode() == KeyCode.RIGHT && direction != Dir.left) {
                    direction = Dir.right;
                }
                if (key.getCode() == KeyCode.DIGIT1) {
                    if (gameOver) {
                        root.getChildren().remove(sp);
                        gameOver = false;
                        snake.clear();
                        score = 0;
                        snake.add(new Node(BLOCK_W / 2, BLOCK_H / 2));
                        snake.add(new Node(BLOCK_W / 2, BLOCK_H / 2));
                        snake.add(new Node(BLOCK_W / 2, BLOCK_H / 2));
                    }
                    if (welcome) {
                        root.getChildren().remove(bx);
                        welcome = false;
                    }
                    resetfood();
                    cd.start();
                    level = 1;
                    speed = 5;
                    countDown = countconst;
                    at.stop();
                    lastTick = 0;
                    at.start();
                    if(pause){
                        at.stop();
                    }
                }
                if (key.getCode() == KeyCode.DIGIT2) {
                    if (gameOver) {
                        root.getChildren().remove(sp);
                        gameOver = false;
                        score = 0;
                        snake.clear();
                        snake.add(new Node(BLOCK_W / 2, BLOCK_H / 2));
                        snake.add(new Node(BLOCK_W / 2, BLOCK_H / 2));
                        snake.add(new Node(BLOCK_W / 2, BLOCK_H / 2));
                    }
                    if (welcome) {
                        root.getChildren().remove(bx);
                        welcome = false;
                    }

                    resetfood();
                    cd.start();
                    level = 2;
                    speed = 10;
                    countDown = countconst;
                    at.stop();
                    lastTick = 0;
                    at.start();
                    if(pause){
                        at.stop();
                    }
                }
                if (key.getCode() == KeyCode.DIGIT3) {
                    if (gameOver) {
                        root.getChildren().remove(sp);
                        score = 0;
                        gameOver = false;
                        snake.clear();
                        snake.add(new Node(BLOCK_W / 2, BLOCK_H / 2));
                        snake.add(new Node(BLOCK_W / 2, BLOCK_H / 2));
                        snake.add(new Node(BLOCK_W / 2, BLOCK_H / 2));
                    }
                    if (welcome) {
                        root.getChildren().remove(bx);
                        welcome = false;
                    }
                    resetfood();
                    cd.start();
                    level = 3;
                    speed = 15;
                    countDown = countconst;
                    at.stop();
                    lastTick = 0;
                    at.start();
                    if(pause){
                        at.stop();
                    }
                }
                if (key.getCode() == KeyCode.P) {
                    if (!welcome && !gameOver) {
                        pause = !pause;
                        if (pause) {
                            at.stop();
                            cd.stop();
                            root.getChildren().add(pauseimg);
                        } else if (!pause) {
                            at.start();
                            cd.start();
                            root.getChildren().remove(pauseimg);
                        }
                    }
                }
                if (key.getCode() == KeyCode.R) {
                    welcome = true;
                    root.getChildren().add(bx);
                    level = 1;
                    speed = 5;
                    resetfood();
                    at.stop();
                    lastTick = 0;
                }
                if (key.getCode() == KeyCode.Q) {
                    Platform.exit();
                }

            });

            // add start snake parts
            snake.add(new Node(BLOCK_W / 2, BLOCK_H / 2));
            snake.add(new Node(BLOCK_W / 2, BLOCK_H / 2));
            snake.add(new Node(BLOCK_W / 2, BLOCK_H / 2));


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void resetfood() {
        food1.clear();
        food2.clear();
        food3.clear();

        food1.add(new Node(2, 3));
        food1.add(new Node(12, 5));
        food1.add(new Node(7, 16));
        food1.add(new Node(22, 13));
        food1.add(new Node(30, 3));

        food2.add(new Node(5, 8));
        food2.add(new Node(17, 1));
        food2.add(new Node(29, 5));
        food2.add(new Node(8, 6));
        food2.add(new Node(31, 11));
        food2.add(new Node(11, 15));
        food2.add(new Node(3, 17));
        food2.add(new Node(17, 1));
        food2.add(new Node(20, 10));
        food2.add(new Node(1, 15));
        food2.add(new Node(4, 17));
        food2.add(new Node(25, 8));

        food3.add(new Node(7, 8));
        food3.add(new Node(9, 6));
        food3.add(new Node(13, 19));
        food3.add(new Node(5, 18));
        food3.add(new Node(31, 9));
        food3.add(new Node(32, 10));
        food3.add(new Node(6, 11));
        food3.add(new Node(4, 13));
        food3.add(new Node(12, 14));
        food3.add(new Node(19, 4));
        food3.add(new Node(17, 5));
        food3.add(new Node(21, 3));
        food3.add(new Node(23, 2));
        food3.add(new Node(27, 6));
        food3.add(new Node(29, 7));
        food3.add(new Node(28, 8));
        food3.add(new Node(24, 19));
        food3.add(new Node(1, 18));
        food3.add(new Node(20, 17));
        food3.add(new Node(10, 16));

    }


    public enum Dir {
        left, right, up, down
    }

}
