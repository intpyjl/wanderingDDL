package desktop.application.wanderingddl;

import desktop.application.wanderingddl.tools.DragUtil;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;

//TODO:联系时钟
public class WanderingController extends Application {
    private Font w_engFont;
    private Font w_znFont;
    private Font w_engTextFont;
    private String[] strings=new String[5];
    private final Stage stage = new Stage();
    static private WanderingController wanderingController;
    private Label[] texts = new Label[5]; //距离xxx,还有大概,30,space,天
    private  double startX ;

    private WanderingController(){
        super();
        this.loadFont();
        this.setStage();
    }
    public static WanderingController getInstance(){
        if(wanderingController==null){
            wanderingController=new WanderingController();
        }
        return wanderingController;
    }
    private void setStage(){
        //给外层套一个透明的以不显示任务栏图标
        Stage transparentStage = new Stage();
        transparentStage.initStyle(StageStyle.UTILITY);
        //将stage的透明度设置为0
        transparentStage.setOpacity(0);
        //stage展示出来，此步骤不可少，缺少此步骤stage还是会存在任务栏
        transparentStage.show();
        stage.initOwner(transparentStage);
        stage.initStyle(StageStyle.TRANSPARENT);//隐藏头标题); //去除窗口样式
        stage.setX(Screen.getPrimary().getBounds().getWidth()-480);
        stage.setY(Screen.getPrimary().getBounds().getHeight()-262);
        stage.setAlwaysOnTop(true);
    }

    private void loadFont() {
        w_engFont = Font.loadFont(Objects.requireNonNull(getClass().getResource("Alte DIN 1451 Mittelschrift gepraegt Regular.ttf")).toExternalForm(), 84);
        w_znFont = Font.loadFont(getClass().getResource("znFont.ttf").toExternalForm(), 36);
        w_engTextFont = Font.loadFont(getClass().getResource("znFont.ttf").toExternalForm(), 18);
    }

    //传参初始化
    private void initText() {
        for (int i = 0; i < 4; i++) {
            if (strings[i].length() > 0) {
                texts[i] = new Label(strings[i]);
            }else texts[i]= new Label("11");
        }
        texts[4] = new Label(" ");
        setZnFont(new Label[]{texts[0], texts[1], texts[3], texts[4]});//text4是空的占位label
        setNumFont(texts[2]);
    }
    public void newInit(String[] sentences){
        try {
            for(int i =0;i<5;i++)
                strings[i]=sentences[i];
            this.start(stage);
        }catch (Exception e){
            System.out.println(e);
        }
    }
    public void start(Stage stage) throws IOException {
        //设置字体
        this.initText();
        //建立节点
        Pane all = new Pane();
        //获取实际宽度
        initLengths(all);


        all.getChildren().addAll(getEngText(), getSeparater());

        all.setStyle("-fx-background-color: transparent;");

        Scene scene = new Scene(all, 480, 262);
        scene.setFill(null);

        stage.setScene(scene);
        DragUtil.addDragListener(stage,all);
        if(!stage.isShowing())
            stage.show();
        MinWindow.getInstance().listen(stage);
        stage.setX(Screen.getPrimary().getBounds().getWidth() - 480);
        stage.setY(Screen.getPrimary().getBounds().getHeight() - 262);

    }

    private HBox getHbox() {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.BOTTOM_RIGHT);
        hbox.getChildren().addAll(getLeft());
        hbox.getChildren().addAll(texts[2]);
        hbox.getChildren().addAll(getRight());
        hbox.setLayoutX(10);

        return hbox;
    }

    private VBox getLeft() {
        VBox left = new VBox();
        left.setAlignment(Pos.TOP_RIGHT);
        left.getChildren().addAll(texts[0], texts[1]);
        return left;
    }

    private VBox getRight() {
        VBox right = new VBox();
        right.setAlignment(Pos.TOP_RIGHT);
        right.getChildren().addAll(texts[4], texts[3]);
        return right;
    }

    private void setZnFont(Label[] labels) {
        for (Label label :
                labels) {
            label.setFont(w_znFont);
            label.setTextFill(Color.valueOf("#ffffff"));
        }
        setEffect(labels);

    }

    private void setNumFont(Label label) {
        label.setFont(w_engFont);
        label.setTextFill(Color.valueOf("#ff0000"));
        setEffect(label);
    }

    private void setTextFont(Label label) {
        label.setFont(w_engTextFont);
        label.setTextFill(Color.valueOf("#ffffff"));
        setEffect(label);
    }

    private void setEffect(Label[] labels) {
        for (Label label :
                labels) {
            setEffect(label);
        }
    }

    private void setEffect(Label label) {
        DropShadow ds = new DropShadow();
        ds.setOffsetY(0f);
        ds.setColor(Color.valueOf("#333333"));
        label.setEffect(ds);
    }

    //此方法为快速设置一遍文字的显示，以获取文字的实际长度和位置，从而设置分割线和英文的位置
    private void initLengths(Pane pane) {
        HBox hBox = getHbox();
        pane.getChildren().add(hBox);

        Scene scene = new Scene(pane, 480, 262);
        scene.setFill(null);
        stage.setScene(scene);
        stage.show();
        VBox vBox = (VBox)hBox.getChildren().get(0);
        startX = vBox.getChildren().get(1).getBoundsInParent().getMaxX()-vBox.getChildren().get(1).getLayoutBounds().getWidth();
        System.out.println(startX);
    }
    private Rectangle getSeparater() {
        Rectangle rectangle = new Rectangle();
        int evH = strings[4].length() * 18 * 20 / (strings[1].length() * 36 + strings[3].length() * 36 + strings[2].length() * 84);
        //计算理想高度
        rectangle.setHeight(evH + 40);
        rectangle.setWidth(6);
        rectangle.setFill(Paint.valueOf("#ff0000"));
        if (startX <= 0) {
            rectangle.setX(0);
        } else {
            rectangle.setX(startX-10);
        }

        rectangle.setY(48);
        return rectangle;
    }

    private Label getEngText() {
        Label label = new Label(strings[4].toUpperCase());
        setTextFont(label);
        label.setWrapText(true);
        label.setAlignment(Pos.TOP_LEFT);
        label.setPrefWidth(strings[1].length() * 36 + strings[3].length() * 36 + strings[2].length() * 84);//设置固定宽度，以控制自动换行
        if (startX <= 0) {
            label.setLayoutX(10);
        }else {
            label.setLayoutX(startX);
        }
        label.setLayoutY(96);
        return label;
    }
}
