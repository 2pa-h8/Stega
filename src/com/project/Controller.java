package com.project;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class Controller {

    @FXML private Button embedWatermarkBtn;
    @FXML private Button readWatermarkBtn;
    @FXML private Button saveEmbeddingResultBtn;
    @FXML private Button saveReadResultBtn;
    @FXML private Button selectOriginalImageBtn;
    @FXML private Button selectWatermarkBtn;
    @FXML private Button selectFilledImageBtn;

    @FXML private TextField startPspTextField1;
    @FXML private TextField startPspTextField2;

    @FXML private ImageView originalImageView = new ImageView();
    @FXML private ImageView watermarkOriginalView = new ImageView();
    @FXML private ImageView watermarkReadView = new ImageView();
    @FXML private ImageView embeddingResultView = new ImageView();
    @FXML private ImageView readingResultView = new ImageView();

    @FXML private Stage stage;

    public void init( Stage primaryStage ) {
        stage = primaryStage;
    }
}