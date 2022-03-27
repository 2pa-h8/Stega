package com.project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

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
    @FXML private ImageView filledImageView = new ImageView();
    @FXML private ImageView embeddingResultView = new ImageView();
    @FXML private ImageView readingResultView = new ImageView();

    private Image originalImage;
    private String originalImagePath;
    private String originalImageFileName;
    private File originalImageFile;

    private Image watermarkImage;
    private String watermarkImagePath;
    private String watermarkImageFileName;
    private File watermarkImageFile;

    private Image filledImage;
    private String filledImagePath;
    private String filledImageFileName;
    private File filledImageFile;



    @FXML private Stage stage;

    public void init(Stage primaryStage) {
        stage = primaryStage;

        setButtonsDisabled(embedWatermarkBtn, readWatermarkBtn, saveReadResultBtn, saveEmbeddingResultBtn);
    }

    // * Выбрать оригинальное изображение *
    public void onSelectOriginalImageBtn(ActionEvent event) {
        File file = chooseImage("Выбирете файл с оригинальным изображением");
        if (file != null) {
            originalImageFileName = file.getName();
            originalImage = new Image(file.toURI().toString());
            originalImageView.setImage(originalImage);
        }
    }

    // * Выбрать ЦВЗ *
    public void onSelectWatermarkBtn(ActionEvent event) {
        File file = chooseImage("Выбирете файл с цифровым водяным знаком (ЦВЗ)");
        if (file != null) {
            watermarkImageFileName = file.getName();
            watermarkImage = new Image(file.toURI().toString());
            watermarkOriginalView.setImage(watermarkImage);
        }
    }

    // * Выбрать заполненное изображение *
    public void onSelectFilledImageBtn(ActionEvent event) {
        File file = chooseImage("Выбирете файл с заполненным изображением");
        if (file != null) {
            filledImageFileName = file.getName();
            filledImage = new Image(file.toURI().toString());
            filledImageView.setImage(filledImage);
        }
    }

    // Выбор и отображение изображения из локальной директории
    private File chooseImage(String message) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(message);
        fileChooser.setInitialDirectory(new File("C:\\Users\\user\\Desktop\\Учеба\\Стеганография\\Курсовая\\"));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter( "Изображения", "*.bmp" ));
        return fileChooser.showOpenDialog(stage);
    }

    // Блокировка всех кнопок
    private void setButtonsDisabled(Button... btns) {
        for(Button b : btns) {
            b.setDisable(true);
        }
    }

    // Очистка виджетов изображения
    private void clearImageWidgets() {
        originalImageView.setImage(null);
        watermarkOriginalView.setImage(null);
        filledImageView.setImage(null);
        embeddingResultView.setImage(null);
        readingResultView.setImage(null);
    }
}