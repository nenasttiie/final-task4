package com.cgvsu;

import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.render_engine.LightSource;
import com.cgvsu.render_engine.RenderEngine;
import com.cgvsu.render_engine.SceneTools;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.vecmath.Vector3f;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;

import static com.cgvsu.render_engine.RenderEngine.*;
import static com.cgvsu.render_engine.SceneTools.*;

public class GuiController {

    final private float TRANSLATION = 0.5F;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    @FXML
    private ListView<Model> modelsListView;
    @FXML
    private ListView<Camera> listCameras;
    @FXML
    private ListView<String> listTextures;
    @FXML
    private ListView<String> vertexListView;
    @FXML
    private  ListView<LightSource> lightningListView;
    @FXML
    private TextField screen;
    @FXML
    private TextField screenCamera;
    @FXML
    private TextField screenTexture;
    @FXML
    private TextField screenLight;

    @FXML
    private TextField eyeX;
    @FXML
    private TextField eyeY;
    @FXML
    private TextField eyeZ;

    @FXML
    private TextField lightX;
    @FXML
    private TextField lightY;
    @FXML
    private TextField lightZ;

    @FXML
    private TextField xTransform;
    @FXML
    private TextField yTransform;
    @FXML
    private TextField zTransform;

    @FXML
    private TextField xRotate;
    @FXML
    private TextField yRotate;
    @FXML
    private TextField zRotate;
    @FXML
    private TextField xScale;
    @FXML
    private TextField yScale;
    @FXML
    private TextField zScale;
    @FXML
    private ToggleButton toggleVertices;
    @FXML
    private VBox sidePanel;
    @FXML
    private HBox HBoxCanvas;
    @FXML
    private ColorPicker modelColorPicker;

    @FXML
    private VBox vBoxCameras;
    @FXML
    private HBox hBoxCameras;
    @FXML
    private CheckBox box1;
    @FXML
    private CheckBox box2;
    private boolean isDarkTheme = false;
    private Camera mainCamera = new Camera(
            new Vector3f(0, 00, 100),
            new Vector3f(0, 0, 0),
            1.0F, 1, 0.01F, 100);

    private Timeline timeline;

    private boolean isDoubleWindow = false;
    private Canvas newCanvas;

    private AnchorPane anchorPane1;
    private AnchorPane anchorPane2;
    private double originalWidth;

    @FXML
    private void addModelButton(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());
        String modelName = file.getName();

        try {
            String fileContent = Files.readString(fileName);
            Model model1 = ObjReader.read(fileContent);

            loadModelFromFile(file);
            Model model = SceneTools.addModel(file);
//            if (model != null) {
//                model.setFileName(modelName);
//            }

            //models.add(model);
            SceneTools.selectedModel = model;

            modelsListView.getItems().add(model);

            System.out.println("Model added successfully: " + modelName);
        } catch (IOException exception) {
            System.out.println("Failed to load model: " + exception.getMessage());
        }
    }

    @FXML
    private void displaySelectedModel(MouseEvent event) {
        Model model = modelsListView.getSelectionModel().getSelectedItem();

        if (model == null) {
            screen.setText("Noting Selected");
            updateButtonState();
        } else {
            selectedModel = model;
            updateVertexList();
            slidePanelIn();
            updateButtonState();
            screen.setText(model.toString() + " selected.");
        }

    }

    private boolean isSelectedCamera = false;
    @FXML
    private void displaySelectedCamera(MouseEvent event) {
        selectedCamera = listCameras.getSelectionModel().getSelectedItem();
//        isWindow1 = box1.isSelected();
//        isWindow2 = box2.isSelected();
        System.out.println(selectedCamera.toString());
        if (selectedCamera == null) {
            screenCamera.setText("Noting Selected");
        } else {
            isSelectedCamera = true;
            screenCamera.setText(selectedCamera.toString() + " selected.");
        }

        if (event.getClickCount() == 2) {
            openEditDialog(selectedCamera);
        }
    }

    private void openEditDialog(Camera camera) {
        if (camera == null) return;

        Stage editStage = new Stage();
        editStage.initStyle(StageStyle.TRANSPARENT);

        editStage.setTitle("Edit Camera");
        editStage.initModality(Modality.APPLICATION_MODAL);

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.CENTER);

        Label positionLabel = new Label("Camera Position:");
        TextField xField = new TextField(String.valueOf(camera.getPosition().x));
        xField.setPromptText("X Coordinate");
        TextField yField = new TextField(String.valueOf(camera.getPosition().y));
        yField.setPromptText("Y Coordinate");
        TextField zField = new TextField(String.valueOf(camera.getPosition().z));
        zField.setPromptText("Z Coordinate");

        Label targetLabel = new Label("Camera Target:");
        TextField targetXField = new TextField(String.valueOf(camera.getTarget().x));
        targetXField.setPromptText("Target X Coordinate");
        TextField targetYField = new TextField(String.valueOf(camera.getTarget().y));
        targetYField.setPromptText("Target Y Coordinate");
        TextField targetZField = new TextField(String.valueOf(camera.getTarget().z));
        targetZField.setPromptText("Target Z Coordinate");

        Label fovLabel = new Label("Field of View:");
        Slider fovSlider = new Slider(0.1, 180, camera.getFov());
        fovSlider.setShowTickLabels(true);
        fovSlider.setShowTickMarks(true);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");

        buttonBox.getChildren().addAll(saveButton, cancelButton);

        layout.getChildren().addAll(
                new Label("Edit Camera Parameters"),
                positionLabel,
                new Label("X:"), xField,
                new Label("Y:"), yField,
                new Label("Z:"), zField,
                targetLabel,
                new Label("Target X:"), targetXField,
                new Label("Target Y:"), targetYField,
                new Label("Target Z:"), targetZField,
                fovLabel, fovSlider,
                buttonBox
        );

        Scene scene = new Scene(layout, 350, 650);
        //scene.getStylesheets().add(getClass().getResource("/com/cgvsu/fxml/DarkTheme.css").toExternalForm());
        if (isDarkTheme) {
            scene.getStylesheets().add(getClass().getResource("/com/cgvsu/fxml/darkTheme.css").toExternalForm());
        } else {
            scene.getStylesheets().add(getClass().getResource("/com/cgvsu/fxml/lightTheme.css").toExternalForm());
        }
        editStage.setScene(scene);

        saveButton.setOnAction(e -> {
            try {
                float x = Float.parseFloat(xField.getText());
                float y = Float.parseFloat(yField.getText());
                float z = Float.parseFloat(zField.getText());

                float targetX = Float.parseFloat(targetXField.getText());
                float targetY = Float.parseFloat(targetYField.getText());
                float targetZ = Float.parseFloat(targetZField.getText());

                float fov = (float) fovSlider.getValue();

                Camera.setCamera(camera, x, y, z, targetX, targetY, targetZ, fov);
                screenCamera.setText(camera.toString());

                listCameras.refresh();

                editStage.close();
            } catch (NumberFormatException ex) {
                showMessage("Ошибка", "Некорректные значения в полях");
            }
        });

        cancelButton.setOnAction(e -> editStage.close());

        editStage.showAndWait();
    }


    @FXML
    private void createCamera() {
        if (!Objects.equals(eyeX.getText(), "") && !Objects.equals(eyeY.getText(), "") && !Objects.equals(eyeZ.getText(), "")) {
            float x = Float.parseFloat(eyeX.getText());
            float y = Float.parseFloat(eyeY.getText());
            float z = Float.parseFloat(eyeZ.getText());
            Camera newCamera = new Camera(new Vector3f(x, y, z), new Vector3f(0, 0, 0), 1.0F, 1, 0.01F, 100);
            cameras.add(newCamera);
            listCameras.getItems().add(newCamera);
        } else {
            showMessage("Ошибка", "Введите все координаты камеры");
        }
    }

    @FXML
    private void deleteModel() {
        if (selectedModel != null) {
            SceneTools.deleteModel(selectedModel);

            //models.remove(selectedModel);
            slidePanelOut();
            vertexListView.getItems().clear();
            modelsListView.getItems().remove(modelsListView.getSelectionModel().getSelectedItem());
            selectedModel = null;
            String s = "Nothing selected";
            screen.setText(s);
            modelsListView.getSelectionModel().clearSelection();
        } else {
            showMessage("Ошибка", "Нельзя удалить модель!");
        }
    }

    @FXML
    private void deleteCamera() {
        if (selectedCamera != null && (cameras.size() > 1) && !selectedCamera.equals(mainCamera)) {
            //cameras.remove(selectedCamera);
            SceneTools.removeCamera(selectedCamera);
            listCameras.getItems().remove(listCameras.getSelectionModel().getSelectedItem());
            selectedCamera = mainCamera;
            String s = "Main camera " + selectedCamera.getPosition();
            listCameras.getSelectionModel().select(listCameras.getItems().get(0));
            screenCamera.setText(s + " selected.");
        } else {
            showMessage("Ошибка", "Нельзя удалить камеру!");
        }
    }

    //LIGHT
    @FXML
    private void createLight() {
        if (!Objects.equals(lightX.getText(), "") && !Objects.equals(lightY.getText(), "") && !Objects.equals(lightZ.getText(), "")) {
            float x = Float.parseFloat(lightX.getText());
            float y = Float.parseFloat(lightY.getText());
            float z = Float.parseFloat(lightZ.getText());
            LightSource light = SceneTools.createLightSource(x, y, z);
            lightningListView.getItems().add(light);
        } else {
            showMessage("Ошибка", "Введите все координаты источника света");
        }
    }

    @FXML
    private void deleteLight() {
        if (selectedLight != null) {
            lightningListView.getItems().remove(lightningListView.getSelectionModel().getSelectedItem());
            SceneTools.removeLightSource(selectedLight);
            //selectedLight = null;
            screenLight.setText("Nothing selected.");
        } else {
            showMessage("Ошибка", "Нельзя удалить свет!");
        }
    }

    @FXML
    private void displaySelectedLight(MouseEvent event) {
        selectedLight = lightningListView.getSelectionModel().getSelectedItem();
        if (selectedLight == null) {
            screenLight.setText("Noting Selected");
        } else {
            screenLight.setText(selectedLight + " selected.");
        }
    }


    @FXML
    private void addTexture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files (*.png, *.jpg)", "*.png", "*.jpg"));
        fileChooser.setTitle("Load Texture");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        String textureName = SceneTools.addTexture(file.getName());
        //String texturePath = file.getAbsolutePath();

        //textures.add(texturePath);
        listTextures.getItems().add(textureName);
    }

    @FXML
    private void displaySelectedTexture(MouseEvent event) {
         selectedTexture = listTextures.getSelectionModel().getSelectedItem();
        if (selectedTexture == null) {
            screenTexture.setText("Noting Selected");
        } else {
            screenTexture.setText(selectedTexture + " selected");
        }
    }

    @FXML
    private void displaySelectedColor(MouseEvent event){
        selectedColor = modelColorPicker.getValue();
        screenTexture.setText("Color " + selectedColor.toString() + "selected");
    }


    @FXML
    private void onSaveModelMenuItemClick() {
        if (selectedModel == null) {
            showMessage("Ошибка", "Выберите модель");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Save Model");

        File file = fileChooser.showSaveDialog(canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            ObjWriter.write(selectedModel, file.getAbsolutePath());
            showMessage("Сообщение","Модель успешно сохранена: " + file.getAbsolutePath());
        } catch (IOException exception) {
            showMessage("Ошибка","Не удалось сохранить файл: " + exception.getMessage());
        }
    }

    private void showMessage(String headText, String messageText) {
        Alert message = new Alert(Alert.AlertType.INFORMATION);
        message.setHeaderText(headText);
        message.setContentText(messageText);
        message.showAndWait();
    }

    private Camera selectedCamera1;
    private Camera selectedCamera2;
    public void updateWindowState1(){
        isWindow1 = box1.isSelected();
        if (cameras.size()==1){
            selectedCamera1 = selectedCamera;
            selectedCamera2 = selectedCamera;
        }

        if (isWindow1 && isSelectedCamera){
            selectedCamera1 = selectedCamera;
            selectedCamera2 = cameras.get(cameras.size()-2);
        }

    }

    public void updateWindowState2(){
        isWindow2 = box2.isSelected();

        if (cameras.size()==1){
            selectedCamera1 = selectedCamera;
            selectedCamera2 = selectedCamera;
        }
        if (isWindow2 && isSelectedCamera){
            selectedCamera2 = selectedCamera;
            selectedCamera1 = cameras.get(cameras.size()-2);
        }
    }

    @FXML
    private void initialize() {
        switchToLightTheme();

        if (isDoubleWindow){
            anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth((newValue.doubleValue())*0.4));
            anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));
            anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> newCanvas.setWidth((newValue.doubleValue())*0.4));
            anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> newCanvas.setHeight(newValue.doubleValue()));
        } else {
            anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth((newValue.doubleValue())));
            anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));
        }


        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            if (isDoubleWindow){
                canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
                newCanvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            } else {
                canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            }

            mainCamera.setAspectRatio((float) (width / height));


            if (selectedModel != null && selectedCamera == null) {
                selectedCamera = mainCamera;
            }

            if (isDoubleWindow){

//                isWindow1 = box1.isSelected();
//                isWindow2 = box2.isSelected();
                updateWindowState1();
                updateWindowState2();
//                System.out.println(isWindow1);
//                System.out.println(isWindow2);

                if (isWindow1 && isWindow2){
                    for (Model model : models) {
                        RenderEngine.render(canvas.getGraphicsContext2D(), selectedCamera, model, (int) width, (int) height);
                        RenderEngine.render(newCanvas.getGraphicsContext2D(), selectedCamera, model, (int) width, (int) height);
                    }
                    System.out.println("обе камеры");
                }
                if((isWindow1) && (!isWindow2)){
                    for (Model model : models) {
                        RenderEngine.render(canvas.getGraphicsContext2D(), selectedCamera1, model, (int) width, (int) height);
                        RenderEngine.render(newCanvas.getGraphicsContext2D(), selectedCamera2, model, (int) width, (int) height);
                    }
                    System.out.println("1 камера");
                }
                if((!isWindow1) && (isWindow2)) {
                    for (Model model : models) {
                        RenderEngine.render(canvas.getGraphicsContext2D(), selectedCamera1, model, (int) width, (int) height);
                        RenderEngine.render(newCanvas.getGraphicsContext2D(), selectedCamera2, model, (int) width, (int) height);
                    }
                    System.out.println("2 камера");
                }
                if((!isWindow1) && (!isWindow2)){
                    showMessage("Ошибка", "Выберите окна!");
                    System.out.println("нет камер");
                }
            } else {
                for (Model model : models) {
                    RenderEngine.render(canvas.getGraphicsContext2D(), selectedCamera, model, (int) width, (int) height);
                    System.out.println("одно окно000000000000000000000000000000000000000000000000000000000");
                }
            }
        });

        vertexListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        vertexListView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                handleVertexSelection();
                event.consume();
            }
        });
        toggleVertices.setOnAction(event -> handleToggleVerticesAction());

        cameras.add(mainCamera);
        listCameras.getItems().add(mainCamera);
        timeline.getKeyFrames().add(frame);
        timeline.play();

    }

    private boolean isWindow1;
    private boolean isWindow2;

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            selectedModel = ObjReader.read(fileContent);
            // todo: обработка ошибок
        } catch (IOException exception) {

        }
    }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        mainCamera.movePosition(new Vector3f(0, 0, -TRANSLATION));
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        mainCamera.movePosition(new Vector3f(0, 0, TRANSLATION));
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        mainCamera.movePosition(new Vector3f(TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        mainCamera.movePosition(new Vector3f(-TRANSLATION, 0, 0));
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        mainCamera.movePosition(new Vector3f(0, TRANSLATION, 0));
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        mainCamera.movePosition(new Vector3f(0, -TRANSLATION, 0));
    }

    public void mous1(MouseEvent mouseEvent) {
    }

    //TRANSFORMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM


    @FXML
    private void applyTranslation(){
        try {
            float xT = Float.parseFloat(xTransform.getText());
            float yT = Float.parseFloat(yTransform.getText());
            float zT = Float.parseFloat(zTransform.getText());
            transform(selectedModel, xT, yT, zT);
            showMessage("Сообщение", "Трансформация: " + selectedModel.toString()
                    + " с x = " + xT + " y = " + yT + " z = " + zT);
        } catch (NumberFormatException e) {
            showMessage("Ошибка", "Возникла ошибка: укажите числовые значения");
        }
    }

    @FXML
    private void applyRotate(){
        try {
            float xR = Float.parseFloat(xRotate.getText());
            float yR = Float.parseFloat(yRotate.getText());
            float zR = Float.parseFloat(zRotate.getText());
            rotate(selectedModel, xR, yR, zR);
            showMessage("Сообщение", "Поворот: " + selectedModel.toString()
                    + " с  x = " + xR + " y = " + yR + " z = " + zR);
        } catch (NumberFormatException e) {
            showMessage("Ошибка", "Возникла ошибка: укажите числовые значения");
        }
    }

    @FXML
    private void applyScale(){
        try {
            float xS = Float.parseFloat(xScale.getText());
            float yS = Float.parseFloat(yScale.getText());
            float zS = Float.parseFloat(zScale.getText());
            scale(selectedModel, xS, yS, zS);
            showMessage("Сообщение", "Изменение масштаба: " + selectedModel.toString()
                    + " с x = " + xS + " y = " + yS + " z = " + zS);
        } catch (NumberFormatException e) {
            showMessage("Ошибка", "Возникла ошибка: укажите числовые значения");
        }
    }

    //DELETEEEE VS
    private void updateButtonState() {
        if (selectedModel == null) {
            toggleVertices.setSelected(false);
        } else {
            toggleVertices.setSelected(selectedModel.isVerticesVisible());
        }
    }

    private void slidePanelIn() {
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), sidePanel);
        slideIn.setToX(320);
        slideIn.play();
    }

    private void slidePanelOut() {
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), sidePanel);
        slideOut.setToX(0);
        slideOut.play();
    }

    private void renderScene() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();
        selectedCamera.setAspectRatio((float) (canvasWidth / canvasHeight));

        for (Model model : models) {
            RenderEngine.render(gc, selectedCamera, model, (int) canvasWidth, (int) canvasHeight);
        }
    }

    public void handleToggleVerticesAction() {
        if (selectedModel == null) {
            showMessage("Ошибка", "Модель не выбрана!");
            toggleVertices.setSelected(false);
            return;
        }

        RenderEngine.toggleVerticesVisibility(selectedModel);
        renderScene();
    }


    @FXML
    private void handleDeleteVertices() {
        if (selectedModel == null) {
            showMessage("Ошибка", "Модель не выбрана!");
            return;
        }
        deleteSelectedVertices(selectedModel);
        updateVertexList();
        renderScene();
    }

    @FXML
    private void handleVertexSelection() {
        ObservableList<Integer> selectedIndices = vertexListView.getSelectionModel().getSelectedIndices();

        for (Integer index : selectedIndices) {
            if (selectedVertexIndices.contains(index)) {
                selectedVertexIndices.remove(index);
            } else {
                selectedVertexIndices.add(index);
            }
        }
        updateVertexList();
    }

    private void updateVertexList() {
        vertexListView.getItems().clear();

        if (selectedModel != null) {
            for (int i = 0; i < selectedModel.vertices.size(); i++) {
                com.cgvsu.math.Vector3f vertex = selectedModel.vertices.get(i);
                vertexListView.getItems().add("Vertex " + i + ": (" + vertex.getX() + ", " + vertex.getY() + ", " + vertex.getZ() + ")");
            }
        }

        vertexListView.setCellFactory(param -> new VertexListCell(isDarkTheme));
    }

    public class VertexListCell extends ListCell<String> {
        private final CheckBox checkBox;
        private final Text vertexText;
        private boolean isDarkTheme;

        public VertexListCell(boolean isDarkTheme) {
            this.isDarkTheme = isDarkTheme;
            HBox hbox = new HBox(10);
            vertexText = new Text();
            checkBox = new CheckBox();
            hbox.getChildren().addAll(vertexText, checkBox);
            setGraphic(hbox);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setGraphic(null);
            } else {
                vertexText.setText(item);
                vertexText.setFill(isDarkTheme ? Color.WHITE : Color.BLACK);

                int index = getIndex();

                checkBox.setSelected(selectedVertexIndices.contains(index));
                checkBox.setOnAction(event -> toggleSelection(index));
                vertexText.setOnMouseClicked(event -> toggleSelection(index));

                HBox hbox = new HBox();
                hbox.setSpacing(10);
                hbox.getChildren().add(checkBox);
                hbox.getChildren().add(vertexText);
                hbox.setAlignment(Pos.CENTER_LEFT);

                setGraphic(hbox);
            }
        }

        private void toggleSelection(int index) {
            if (selectedVertexIndices.contains(index)) {
                selectedVertexIndices.remove(Integer.valueOf(index));
            } else {
                selectedVertexIndices.add(index);
            }

            updateVertexList();
        }
    }

    //ТЕМЫ

    private final List<String> originalStylesheets = new ArrayList<>();

    @FXML
    public void switchToLightTheme() {
        isDarkTheme = !isDarkTheme;
        if (originalStylesheets.isEmpty()) {
            originalStylesheets.addAll(anchorPane.getStylesheets());
        }

        clearStyles(anchorPane);
        anchorPane.getStylesheets().clear();
        anchorPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/cgvsu/fxml/lightTheme.css")).toExternalForm());
        updateVertexList();
    }

    private void clearStyles(Parent parent) {
        parent.getStylesheets().clear();

        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof Parent) {
                clearStyles((Parent) node);
            }

            if (!node.styleProperty().isBound()) {
                node.setStyle("");
            }
        }
    }

    @FXML
    public void switchToDarkTheme() {
        isDarkTheme = true;
        clearStyles(anchorPane);
        anchorPane.getStylesheets().clear();
        anchorPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/cgvsu/fxml/darkTheme.css")).toExternalForm());
        updateVertexList();
    }

    @FXML
    public void addWindow() {
        originalWidth = canvas.getWidth();

        isDoubleWindow = true;
        System.out.println(canvas.getWidth());
        double newWidth = canvas.getWidth()*0.4;
        double newHeight = canvas.getHeight();

        canvas.setWidth(newWidth);
        System.out.println(canvas.getWidth());

        anchorPane2 = new AnchorPane();
        anchorPane2.setPrefWidth(newWidth);
        anchorPane2.setPrefHeight(newHeight);


        anchorPane1 = new AnchorPane();
        anchorPane1.setPrefWidth(newWidth);
        anchorPane1.setPrefHeight(newHeight);
        anchorPane1.setStyle("-fx-border-width: 1px; -fx-border-color: #e0e0e1;");

        anchorPane1.getChildren().add(canvas);

        newCanvas = new Canvas(newWidth, newHeight);

        anchorPane2.getChildren().add(newCanvas);

        HBoxCanvas.getChildren().add(anchorPane1);
        HBoxCanvas.getChildren().add(anchorPane2);

        System.out.println(canvas.getLayoutX() + canvas.getWidth());

        box1 = new CheckBox("Window 1");
        box2 = new CheckBox("Window 2");
        box1.setSelected(true);
        box2.setSelected(true);

        hBoxCameras = new HBox();
        hBoxCameras.getChildren().add(box1);
        hBoxCameras.getChildren().add(box2);
        box1.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
        box2.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");

        vBoxCameras.getChildren().add(hBoxCameras);
    }

    @FXML
    public void oneWindow(){
        isDoubleWindow = false;
        HBoxCanvas.getChildren().remove(anchorPane2);
        System.out.println("remove");
        anchorPane1.setPrefWidth(originalWidth);
        canvas.setWidth(originalWidth);
        vBoxCameras.getChildren().remove(hBoxCameras);
    }

}