package com.project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Controller {

    @FXML
    private ImageView sourceImage = new ImageView();

    @FXML
    private ImageView outputImage = new ImageView();

    @FXML
    private TextField encodingText;

    @FXML
    private Button encodeBtn;

    @FXML
    private Button openImageBtn;

    @FXML
    private Button saveImageBtn;

    @FXML
    private Button readImageBtn;

    @FXML
    private ComboBox< String > algorithmList;

    private Stage _stage;
    private Image _sourceImage;
    private String _sourceImageFileName;
    private String _sourceImagePath;
    private WritableImage _outputImage;

    public void init( Stage primaryStage ) {
        _stage = primaryStage;
    }
}