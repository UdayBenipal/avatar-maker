import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Controller {
    static Model model = new Model();
    static Group root = new Group();
    static ArrayList<Node> avatar_features = new ArrayList<>();
    static ArrayList<Node> widgets = new ArrayList<>();

    public void setView() throws Exception {
        Parent layout = FXMLLoader.load(getClass().getResource("./resources/scene.fxml"));
        root.getChildren().add(layout);

        Rectangle background = new Rectangle(400, 200, 200, 200);
        background.setFill(Color.WHITE);
        avatar_features.add(background);
        Group avatar = new Group(avatar_features.get(0));
        for(int i = 1; i < 8; i++) {
            if(i == 6) {
                SVGLoader loader = new SVGLoader();
                Group feature = loader.loadSVG(model.getFeature(i));
                SVGPath svgPath = (SVGPath) feature.getChildren().get(0);
                model.hairColor = svgPath.getFill();
                feature.setLayoutX(400);
                feature.setLayoutY(200);
                avatar_features.add(feature);
                avatar.getChildren().add(avatar_features.get(i));
            } else if(i == 7) {
                SVGLoader loader = new SVGLoader();
                Group clothes = loader.loadSVG(model.getFeature(i));
                for(int j = 0; j < 5; j++) {
                    avatar_features.add(clothes.getChildren().get(j));
                    clothes.getChildren().get(j).setLayoutX(400);
                    clothes.getChildren().get(j).setLayoutY(200);
                }
                avatar.getChildren().add(clothes);
            } else {
                ImageView feature = new ImageView(new Image(model.getFeature(i), 200, 200, true, true));
                feature.setX(400);
                feature.setY(200);
                if(i==2) feature.setY(195);
                avatar_features.add(feature);
                avatar.getChildren().add(avatar_features.get(i));
            }
        }
        deselectAll();
        featureTweaker();
        setWidgets();
        root.getChildren().add(avatar);
    }

    public void UpdateAvatar() {
        root.getChildren().remove(1);
        Group avatar = new Group(avatar_features.get(0));
        for(int i = 1; i < 12; i++) {
            if(i==2) {
                avatar_features.get(i).setLayoutY(model.brow_offset);
                continue;
            } else if(i==3) {
                avatar_features.get(i).setScaleY(model.eye_scale);
            } else if(i==6) {
                avatar.getChildren().add(avatar_features.get(2));
                /////////////////////////////////////////////////
                Group g = (Group) avatar_features.get(i);
                SVGPath svg = (SVGPath) g.getChildren().get(0);
                svg.setFill(model.hairColor);
            }
            DropShadow dp = new DropShadow(0, Color.TRANSPARENT);
            avatar_features.get(i).setEffect(dp);
            avatar.getChildren().add(avatar_features.get(i));
        }
        root.getChildren().add(avatar);
        if(model.theSelected() < 12) updateWidget(model.theSelected());
        featureTweaker();
    }

    @FXML
    public void updateFeature(Event event) {
        if(event.getEventType().toString().equals("MOUSE_CLICKED")) {
            Node n = (Node) event.getSource();
            String id = n.getId().toString();
            int num = Integer.parseInt(id.substring(id.length()-2));
            int i = num%10;
            int feature_i = num/10;

            model.updateFeature(feature_i, i);
            if(feature_i > 5) {
                SVGLoader loader = new SVGLoader();
                Group feature = loader.loadSVG(model.getFeature(feature_i));
                feature.setLayoutX(400);
                feature.setLayoutY(200);
                avatar_features.set(feature_i, feature);
            } else {
                ImageView feature = new ImageView(new Image(model.getFeature(feature_i), 200, 200, true, true));
                feature.setX(400);
                feature.setY(200);
                avatar_features.set(feature_i, feature);
            }
            UpdateAvatar();
        }
    }

    @FXML
    public void deselectAll() {
        for(Node n : widgets) {
            root.getChildren().remove(n);
        }
        for(int i = 0; i < 12; i++) {
            DropShadow dp = new DropShadow(0, Color.TRANSPARENT);
            avatar_features.get(i).setEffect(dp);
        }
        model.Selected(-1);
    }

    public void featureTweaker() {
        for(int i = 0; i < 12; ++i) {
            int index = i;

            if(i!=1 && i!=4 && i!=5) {
                avatar_features.get(index).setOnMouseEntered(mouseEvent -> {
                    if (avatar_features.get(index).getEffect().toString().contains("DropShadow")) {
                        DropShadow ds = new DropShadow(5, Color.CYAN);
                        avatar_features.get(index).setEffect(ds);
                    }
                });
                avatar_features.get(index).setOnMouseExited(mouseEvent -> {
                    if (avatar_features.get(index).getEffect().toString().contains("DropShadow")) {
                        DropShadow ds = new DropShadow(0, Color.TRANSPARENT);
                        avatar_features.get(index).setEffect(ds);
                    }
                });
                avatar_features.get(index).setOnMouseClicked(mouseEvent -> {
                    deselectAll();
                    model.Selected(index);
                    updateWidget(index);
                });
            } else {
                avatar_features.get(index).setOnMouseClicked(mouseEvent -> {
                    deselectAll();
                });
            }
        }
    }

    public void setWidgets() {
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setLayoutX(737.5);
        colorPicker.setLayoutY(100);
        colorPicker.setPrefWidth(220);
        colorPicker.setOnAction(new EventHandler() {
            public void handle(Event t) {
                int i = model.theSelected();
                if(i == 0) {
                    Rectangle r = (Rectangle) avatar_features.get(0);
                    model.bgColor = colorPicker.getValue();
                    r.setFill(model.bgColor);
                    if(checked) {
                        Parent p = (Parent) root.getChildren().get(0);
                        CheckBox cb = (CheckBox) p.getChildrenUnmodifiable().get(3);
                        cb.setSelected(false);
                        checked = !checked;
                    }
                } else if(i ==6) {
                    Group g = (Group) avatar_features.get(6);
                    SVGPath svg = (SVGPath) g.getChildren().get(0);
                    model.hairColor = colorPicker.getValue();
                    svg.setFill(colorPicker.getValue());
                } else {
                    SVGPath svg = (SVGPath) avatar_features.get(i);
                    svg.setFill(colorPicker.getValue());
                }
            }
        });
        widgets.add(colorPicker);

        Text offset = new Text(760, 117, "Vertical Offset :");
        offset.setScaleX(1.7);
        offset.setScaleY(1.7);
        Spinner<Integer> spinner = new Spinner<Integer>();
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(-8,8,0);
        spinner.setValueFactory(valueFactory);
        spinner.setLayoutX(897.5);
        spinner.setLayoutY(100);
        spinner.setPrefWidth(60);
        spinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            model.brow_offset = newValue/2.5;
            avatar_features.get(2).setLayoutY(model.brow_offset);
        });
        widgets.add(spinner);
        widgets.add(offset);

        Text scale = new Text(750, 114, "Scale :");
        scale.setScaleX(1.7);
        scale.setScaleY(1.7);
        Slider slider = new Slider();
        slider.setMin(1);
        slider.setMax(2);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setValue(1);
        slider.setBlockIncrement(0.1);
        slider.setLayoutX(820);
        slider.setLayoutY(100);
        slider.valueProperty().addListener((obs, oldValue, newValue) -> {
            model.eye_scale = (double)newValue/1.5;
            avatar_features.get(3).setScaleY(model.eye_scale);
        });
        widgets.add(slider);
        widgets.add(scale);
    }

    public void updateWidget(int index) {
        for(Node n : widgets) {
            root.getChildren().remove(n);
        }
        InnerShadow is = new InnerShadow(5, Color.BLUE);
        avatar_features.get(index).setEffect(is);
        if(index==2) {
            root.getChildren().add(widgets.get(1));
            root.getChildren().add(widgets.get(2));
        } else if(index==3) {
            root.getChildren().add(widgets.get(3));
            root.getChildren().add(widgets.get(4));
        } else {
            int temp = model.theSelected();
            ColorPicker colorPicker = (ColorPicker) widgets.get(0);
            if (temp == 6) {
                colorPicker.setValue((Color) model.hairColor);
            } else if (temp == 0) {
                colorPicker.setValue((Color) model.bgColor);
                if(checked) colorPicker.setValue(Color.TRANSPARENT);
            } else {
                SVGPath svg = (SVGPath) avatar_features.get(temp);
                colorPicker.setValue((Color) svg.getFill());
            }
            root.getChildren().add(widgets.get(0));
        }
    }

    @FXML
    public void save() {
        for(int i = 0; i < 12; i++) {
            DropShadow dp = new DropShadow(0, Color.TRANSPARENT);
            avatar_features.get(i).setEffect(dp);
        }
        Group avatar = (Group) root.getChildren().get(1);
        avatar.getChildren().remove(0);

        SnapshotParameters snapPara = new SnapshotParameters();
        snapPara.setFill(model.bgColor);
        if(checked) snapPara.setFill(Color.TRANSPARENT);
        WritableImage image = avatar.snapshot(snapPara, null);
        UpdateAvatar();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            String name = file.getName();
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    static double scale_x = 1;
    static double translate_x = 0;
    static double scale_y = 1;
    static double translate_y = 0;
    public void setResizable(Stage stage) {
        Parent p = (Parent) root.getChildren().get(0);
        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            double old_x = (double) oldVal;
            double new_x = (double) newVal;
            scale_x -= (old_x-new_x)/old_x;
            p.setScaleX(scale_x);
            translate_x -= (old_x-new_x)/2;
            root.setTranslateX(translate_x);
            for(Node n : widgets) {
                n.setScaleX(scale_x);
                n.setTranslateX(translate_x/1.35);
            }
        });
        
        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            double old_y = (double) oldVal;
            double new_y = (double) newVal;
            scale_y -= (old_y-new_y)/old_y;
            p.setScaleY(scale_y);
            translate_y -= (old_y-new_y)/2;
            root.setTranslateY(translate_y);
            for(Node n : widgets) {
                n.setScaleY(scale_y);
                n.setTranslateY(-translate_y);
            }
        });
    }

    static boolean checked = false;
    @FXML
    public void checkbox() {
        deselectAll();
        Rectangle r = (Rectangle) avatar_features.get(0);
        checked = !checked;
        if(checked) {
            r.setFill(Color.TRANSPARENT);
        } else {
            r.setFill(model.bgColor);
        }
    }
}
