package com.project;

import com.project.image.Picture;
import com.project.image.Watermark;
import com.project.steganography.SpreadSpectrum;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Controller {
    @FXML private Button saveEmbeddingResultBtn;
    @FXML private Button saveReadResultBtn;

    @FXML private TextField startPspTextField = new TextField();
    @FXML private TextField watermarkWidth = new TextField();
    @FXML private TextField watermarkHeight = new TextField();

    @FXML private ImageView originalImageView = new ImageView();
    @FXML private ImageView watermarkOriginalView = new ImageView();
    @FXML private ImageView filledImageView = new ImageView();
    @FXML private ImageView embeddingResultView = new ImageView();
    @FXML private ImageView readingResultView = new ImageView();

    private Image originalImage;
    private File originalImageFile;

    private Image watermarkImage;
    private File watermarkImageFile;

    private Image filledImage;
    private File filledImageFile;

    private int startPsp;

    SpreadSpectrum spreadSpectrumMethod = new SpreadSpectrum();

    @FXML private Stage stage;

    public void init(Stage primaryStage) {
        stage = primaryStage;

        setButtonsDisabled(saveEmbeddingResultBtn, saveReadResultBtn);
    }

    // * Выбрать оригинальное изображение *
    public void onSelectOriginalImageBtn(ActionEvent event) {
        File file = chooseImage("Выбирете файл с оригинальным изображением");
        if (file != null) {
            originalImageFile = file;
            originalImage = new Image(file.toURI().toString());
            originalImageView.setImage(originalImage);
        }
    }

    // * Встроить ЦВЗ в изображение *
    public void onEmbedWatermarkBtn(ActionEvent event) {
        if (watermarkImage != null && originalImage != null) {
            try {
                Picture original = new Picture(originalImageFile.getPath());
                Watermark watermark = new Watermark(watermarkImageFile.getPath());

                // Размеры ЦВЗ
                int watermarkHeight = watermark.getHeight();
                int watermarkWidth = watermark.getWidth();

                // Начальное состояние регистра определяется случайным числом
                // в диапазоне от 1 до ширина ЦВЗ * длина ЦВЗ
                startPsp = new Random().nextInt(watermarkHeight * watermarkWidth);

                // Встраивание ЦВЗ в изображение
                Picture filledImage = spreadSpectrumMethod.encode(original, watermark, startPsp);

                // Отображение результата на форме
                Image image = SwingFXUtils.toFXImage(filledImage.getBufferedImage(), null);
                embeddingResultView.setImage(image);

                saveEmbeddingResultBtn.setDisable(false);

                showSimpleMessage(Alert.AlertType.INFORMATION, "Встраивание завершено",
                        "Встраивание успешно завершено, пожалуйста, сохраните"
                                + " следующую инфрмацию, она понадобиться при извлечении ЦВЗ из контейнера."
                                + "\n\n" + "Стартовая позиция псевдослучайной последовательности (ПСП) : "
                                + startPsp + "\n\n" + "Ширина ЦВЗ : " + watermarkWidth + "\n\n" + "Высота ЦВЗ : " + watermarkHeight);
            } catch (Exception e) {
                showSimpleMessage(Alert.AlertType.ERROR, "Ошибка при встраивании", e.getMessage());
            }
        } else {
            showSimpleMessage(Alert.AlertType.WARNING, "Предупреждение", "Пожалуйста, вставьте оригинальное " +
                    "изображение и ЦВЗ");
        }
    }

    // * Выбрать ЦВЗ *
    public void onSelectWatermarkBtn(ActionEvent event) {
        File file = chooseImage("Выбирете файл с цифровым водяным знаком (ЦВЗ)");
        if (file != null) {
            watermarkImageFile = file;
            watermarkImage = new Image(file.toURI().toString());
            watermarkOriginalView.setImage(watermarkImage);
        }
    }

    // * Выбрать заполненное изображение *
    public void onSelectFilledImageBtn(ActionEvent event) {
        File file = chooseImage("Выбирете файл с заполненным изображением");
        if (file != null) {
            filledImageFile = file;
            filledImage = new Image(file.toURI().toString());
            filledImageView.setImage(filledImage);
        }
    }

    // * Прочитать ЦВЗ *
    public void onReadWatermarkBtn(ActionEvent event) {
        if (!startPspTextField.getText().isEmpty() && !watermarkHeight.getText().isEmpty()
                && !watermarkWidth.getText().isEmpty() && filledImage != null) {

            int height = Integer.parseInt(watermarkHeight.getText());
            int width = Integer.parseInt(watermarkWidth.getText());
            int startPsp = Integer.parseInt(startPspTextField.getText());

            Picture filledImage = new Picture(filledImageFile.getPath());

            try {
                // Декодирование
                Watermark resultWatermark = spreadSpectrumMethod.decode(
                        filledImage,
                        width,
                        height,
                        startPsp);

                // Отображение результата на форме
                Image image = SwingFXUtils.toFXImage(resultWatermark.getBufferedImage(), null);
                readingResultView.setImage(image);

                saveReadResultBtn.setDisable(false);
            } catch (Exception e) {
                showSimpleMessage(Alert.AlertType.ERROR, "Ошибка извлечении", e.getMessage());
            }
        } else {
            showSimpleMessage(Alert.AlertType.WARNING, "Предупреждение", "Пожалуйста, заполните стартовую " +
                    "позицию ПСП, Длину ЦВЗ и Ширину ЦВЗ. А также вставьте изображение, из которого необходимо извлечь ЦВЗ.");
        }
    }

    // * Сохранить результат извлечения *
    public void onSaveReadResultBtn(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить ЦВЗ ...");
        fileChooser.setInitialDirectory(new File(filledImageFile.getParent()));
        // В название изображения встраивается стартовая позиция ПСП
        fileChooser.setInitialFileName("watermark" + startPsp + ".bmp");
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            Image image = readingResultView.getImage();
            saveImage(file, image);
        }
    }

    // * Сохранить результат встраивания *
    public void onSaveEmbeddingResultBtn(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить файл с изображением...");
        fileChooser.setInitialDirectory(new File(originalImageFile.getParent()));
        // В название изображения встраивается стартовая позиция ПСП
        fileChooser.setInitialFileName("encoded" + startPsp + ".bmp");
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            Image image = embeddingResultView.getImage();
            saveImage(file, image);
        }
    }

    // Сохранить изображение
    public void saveImage(File file, Image image) {
        BufferedImage tmpImage = SwingFXUtils.fromFXImage(image, null);
        BufferedImage newBuffImg = new BufferedImage(tmpImage.getWidth(), tmpImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        newBuffImg.createGraphics().drawImage(tmpImage, 0,0,null);

        try {
            boolean isSuccessful = ImageIO.write(newBuffImg, "bmp", file);

            if (!isSuccessful) {
                showSimpleMessage(Alert.AlertType.WARNING, "Ошибка", "Изображение не было сохранено");
            }
        } catch(IOException e) {
            e.printStackTrace();
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

    // Блокировка кнопок
    private void setButtonsDisabled(Button... btns) {
        for(Button b : btns) {
            b.setDisable(true);
        }
    }

    private void showSimpleMessage(Alert.AlertType type, String title, String text) {
        Alert alert = new Alert(type);
        alert.setHeight(300.0);
        alert.setWidth(300.0);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
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