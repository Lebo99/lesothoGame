package com.example.game;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HelloApplication extends Application {

    // Questions, Answers, and Image URLs

    private String[][] questionsAndAnswers = {
            {"Which of the following is the capital city of Lesotho?", "Maseru","Mohale's Hoek", "Teyateyaneng","Leribe"},
            {"What is the official language of Lesotho?", "Zulu", "Xhosa", "Sesotho", "English"},
            {"Lesotho is entirely surrounded by which country?", "South Africa", "Mozambique", "Zimbabwe", "Botswana"},
            {"Lesotho is known for its large population of which kind of wildlife?","Elephants","Lions","Zebras","Giraffes"},
            {"What is the currency of Lesotho?","Rand","Dollar","Pula","Loti"},
            {"What is the highest peak in Lesotho?","Mount Everest","Mont Aux Sources"," Thabana Ntlenyana","Kilimanjaro"},
    };

    private String[] questionImages = {
            "t.png",
            "c.png",
            "a.jpg",
            "zz.jpg",
            "m.jpg",
            "n.jpg",
    };

    private int currentQuestionIndex = 0;
    private int score = 0;
    private String videoLInk = "C:/Users/lucia/Desktop/New folder (2) - Copy/lesotho.mp4";
    private Label questionLabel;
    private RadioButton[] answerOptions;
    private Button submitButton;
    private Timeline timer;
    private Label timerLabel;
    private int timeSeconds = 5;

    @Override
    public void start(Stage primaryStage) {
        //String css = getClass().getResource("style.css").toExternalForm().toString();
        // Create welcome page layout
        StackPane welcomeLayout = new StackPane();
        welcomeLayout.setAlignment(Pos.CENTER);

            // Load background image for welcome page
            Image backgroundImage = new Image("p.jpg");
            BackgroundSize backgroundSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
            BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1.0, 1.0, true, true, false, false));
            welcomeLayout.setBackground(new Background(background));

            // Create VBox for welcome message and start button
            VBox welcomeBox = new VBox(20);
            welcomeBox.setAlignment(Pos.CENTER);

        // Add welcome message
        Label welcomeLabel = new Label("Welcome to Lesotho Trivia Game!");
        welcomeLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        welcomeLabel.setTextFill(Color.BLACK);

        File file = new File(videoLInk);
        Media media = new Media(file.toURI().toString());
        MediaPlayer  mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);
        mediaPlayer.play();
        mediaPlayer.setCycleCount(10);
        mediaView.fitWidthProperty().bind(primaryStage.widthProperty());
        mediaView.fitHeightProperty().bind(primaryStage.heightProperty());

        // Add start button
        Button startButton = new Button("Start Game");
        startButton.setOnAction(e -> {
            // Switch to game scene when start button is clicked
            Scene gameScene = createGameScene(primaryStage);
            //gameScene.getStylesheets().addAll(css);
            primaryStage.setScene(gameScene);
            startTimer(); // Start the timer for the first question
            displayQuestion(); // Display the first question
            mediaPlayer.stop();
        });

        // Add nodes to the welcome layout
        welcomeLayout.getChildren().addAll(mediaView,welcomeLabel, startButton);

        // Create welcome scene
        Scene welcomeScene = new Scene(welcomeLayout, 500, 500);
        //welcomeScene.getStylesheets().addAll(css);
        primaryStage.setScene(welcomeScene);
        primaryStage.setTitle("Lesotho Trivia Game");
        primaryStage.show();

    }

    private Scene createGameScene(Stage primaryStage) {
        questionLabel = new Label();
        questionLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 20));

        answerOptions = new RadioButton[4];
        ToggleGroup group = new ToggleGroup();
        for (int i = 0; i < 4; i++) {
            answerOptions[i] = new RadioButton();
            answerOptions[i].setToggleGroup(group);
        }

        submitButton = new Button("Submit");
        submitButton.setOnAction(e -> checkAnswer());

        // Add timer label
        timerLabel = new Label();
        timerLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 16));

        // VBox for question and image
        VBox questionVBox = new VBox(10);
        questionVBox.setAlignment(Pos.CENTER);
        questionVBox.getChildren().addAll(questionLabel);

        // VBox for answer options
        VBox optionsVBox = new VBox(10);
        optionsVBox.setAlignment(Pos.CENTER);
        for (RadioButton option : answerOptions) {
            optionsVBox.getChildren().add(option);
        }

        // HBox for timer and submit button
        HBox timerButtonHBox = new HBox(10);
        timerButtonHBox.setAlignment(Pos.CENTER);
        timerButtonHBox.getChildren().addAll(timerLabel, submitButton);

        // VBox for entire game scene
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(questionVBox, optionsVBox, timerButtonHBox);
        // Connect to CSS file for styling
        return new Scene(root, 600, 600);
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questionsAndAnswers.length) {
            questionLabel.setText(questionsAndAnswers[currentQuestionIndex][0]);

            // Load image dynamically based on the current question index
            Image questionImage = new Image(questionImages[currentQuestionIndex]);
            ImageView imageView = new ImageView(questionImage);
            imageView.setFitWidth(300); // Adjust width as needed
            imageView.setPreserveRatio(true);

            // Clear previous image and add the new one
            VBox questionVBox = (VBox) ((VBox) ((Scene) questionLabel.getScene()).getRoot()).getChildren().get(0);
            if (questionVBox.getChildren().size() > 1) {
                questionVBox.getChildren().remove(1);
            }
            questionVBox.getChildren().add(imageView);

            List<Integer> randomOrder = generateRandomOrder();
            for (int i = 0; i < 4; i++) {
                answerOptions[i].setText(questionsAndAnswers[currentQuestionIndex][randomOrder.get(i) + 1]);
            }
        } else {
            // Display final score
            questionLabel.setText("Quiz Completed! Your Score: " + score + " out of " + questionsAndAnswers.length);
            for (RadioButton option : answerOptions) {
                option.setVisible(false);
            }
            submitButton.setVisible(false);
        }
    }


    private List<Integer> generateRandomOrder() {
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            order.add(i);
        }
        java.util.Collections.shuffle(order);
        return order;
    }

    private void startTimer() {
        timeSeconds = 5;
        updateTimerLabel();
        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeSeconds--;
            if (timeSeconds <= 0) {
                timer.stop();
                // Handle timeout (e.g., move to the next question)
                displayNextQuestion();
            }
            updateTimerLabel();
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void updateTimerLabel() {
        int minutes = timeSeconds / 60;
        int seconds = timeSeconds % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void displayNextQuestion() {
        // Move to the next question...
        currentQuestionIndex++;
        displayQuestion();
    }

    private void checkAnswer() {
        RadioButton selectedOption = (RadioButton) answerOptions[currentQuestionIndex % 4].getToggleGroup().getSelectedToggle();
        if (selectedOption != null) {
            String selectedAnswer = selectedOption.getText().substring(0, 1).toLowerCase();
            if (selectedAnswer.equals(questionsAndAnswers[currentQuestionIndex][1].substring(0, 1).toLowerCase())) {
                score++;
            }
            timer.stop();
            displayNextQuestion();
            startTimer(); // Start the timer for the next question
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
