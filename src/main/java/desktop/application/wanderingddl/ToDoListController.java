package desktop.application.wanderingddl;

import desktop.application.wanderingddl.tools.DragUtil;
import desktop.application.wanderingddl.tools.SaverAndLoader;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;


//  两种模式，完成的移除/完成的仍显示
//  多种模板
public class ToDoListController extends ContentController {
    static private ToDoListController toDoListController;

    private LinkedList<ToDoItem> toDoItems ;
    private Text[] texts;
    private Rectangle[] deleters;
    private Label[] checks;
    private Color textColor=Color.BLACK;
    private ToDoMode nowMode;
    private double width;
    private Font qfont;
    private boolean ifRemove;
    private ToDoListController(){
        super();
        this.loadFont();
        this.setStage();
    }
    private void loadFont(){
        qfont = Font.loadFont(getClass().getResource("MainContent/font/todoListQfont.ttf").toExternalForm() ,80);
    }
    public static ToDoListController getInstance(){
        if(toDoListController==null){
            toDoListController = new ToDoListController();
        }
        return toDoListController;
    }
    //设置完启动入口
    public void newInit(LinkedList<ToDoItem> toDoItems,int mode,boolean ifRemove){

            //获取传参
            this.toDoItems = toDoItems;
            this.ifRemove = ifRemove;
            if(ifRemove) {
                for(ToDoItem toDoItem:toDoItems){
                    toDoItem.setUnCheckd();
                }
            }
            setMode(mode);
            setWidth(300);
            setTexts();
        try {
            this.start(stage);
        }catch (Exception e){
            System.out.println(e);
        }
    }
    private void setAnimation(Node node){
        FadeTransition ft = new FadeTransition(Duration.millis(1000),node);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.play();
    }
    protected void addTimer(Text text) {
        int period = 1100;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        removeTotalLy(text);
                    }

                });
            }
        }, period);

    }
    public void saveData(){
        toDoItems.removeIf(ToDoItem::isChecked);
        SaverAndLoader.tool.saveToDoItems(toDoItems);
    }

    private void setMode(int mode) {
        nowMode = new ToDoMode(mode);
    }
    private  void setWidth(double width) {
        this.width = width;
    }

    @Override
    public void start(Stage stage) throws IOException {
        Pane all = new Pane();
        VBox vBox = new VBox();

        //可修改颜色
        all.setStyle("-fx-background-color: "+nowMode.bgColor+";");

        vBox.getChildren().add(getHeader());
        vBox.getChildren().addAll(getListItems());
        if(nowMode.bottomImg!=null){
            vBox.getChildren().add(getFooter());
        }
        all.getChildren().addAll(vBox);
        Scene scene = new Scene(all, width, width*nowMode.headRatio+toDoItems.size()* nowMode.lineRatio*width+ nowMode.bottomRatio*width);
        stage.setHeight(scene.getHeight());
        scene.setFill(null);

        stage.setScene(scene);
        DragUtil.addDragListener(stage,all);
        if(!stage.isShowing())
            stage.show();
        MinWindow.getInstance().listen(2);
        setDeleters();
        addDeleters(all);
    }

    //头部图片
    private HBox getHeader() {
        HBox header = new HBox();

        BackgroundImage bImg = new BackgroundImage(nowMode.headImg,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(50,50,true,true,true,false));
        header.setPrefWidth(width);
        header.setPrefHeight(width*nowMode.headRatio);
        header.setBackground(new Background(bImg));

        return header;
    }
    private HBox getFooter() {
        HBox footer = new HBox();
        BackgroundImage bImg = new BackgroundImage(nowMode.bottomImg,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(50,50,true,true,true,false));
        footer.setPrefWidth(width);
        footer.setPrefHeight(width*nowMode.headRatio);
        footer.setBackground(new Background(bImg));
        return footer;
    }
    public int findIndex(Text text) {
        int index=-1;
        for(int i=0;i<this.texts.length;i++) {
            if (this.texts[i].equals(text))return i;
        }
       return index;
    }
    private void done(Text text,int abIndex) {
        int i=findIndex(text);
        System.out.print(text.getText()+" delete");
        System.out.print(i);
        if(ifRemove) {
            //i是相对顺序
            hideDelete(i,abIndex);
        }else {
            showDelete(i);
        }
    }
    private void removeTotalLy(Text text) {
        int i=findIndex(text);
        Node root= stage.getScene().getRoot();
        Pane all = (Pane) root;
        stage.setHeight(stage.getScene().getHeight()- nowMode.lineRatio*width);
        all.getChildren().remove(deleters[i]);
        all.getChildren().remove(checks[i]);
        ((VBox)all.getChildren().get(0)).getChildren().remove(i+1);
        Text[] first=new Text[]{};
        if(i>0)
            first = Arrays.copyOfRange(texts,0,i);
        Text[] last = new Text[]{};
        if(this.texts.length>i+1)
            last = Arrays.copyOfRange(texts,i+1,texts.length);

        this.texts = Arrays.copyOf(first, first.length+last.length);
        System.arraycopy(last, 0, texts, first.length, last.length);
        System.out.println(this.texts.length);
        for(Text text1:texts){
            System.out.print(text1.getText()+" ");
        }
        System.out.println(texts.length);
    }
    private void hideDelete(int i,int abIndex) {
        System.out.println(i);
        Node root= stage.getScene().getRoot();
        Pane all = (Pane) root;
        toDoItems.get(abIndex).setChecked();
        if(nowMode.mode==2) {
            all.getChildren().add(checks[i]);
            setAnimation(checks[i]);
        }
        all.getChildren().add(deleters[i]);
        setAnimation(deleters[i]);
        setAnimation(texts[i]);
        addTimer(texts[i]);
    }
    private void showDelete(int i){
        Node root= stage.getScene().getRoot();
        Pane all = (Pane) root;
        if(toDoItems.get(i).isChecked()) {
            all.getChildren().remove(root.lookup("#deleter"+i));
            toDoItems.get(i).setUnCheckd();
            if(nowMode.mode==2){
                all.getChildren().remove(root.lookup("#check"+i));
            }
        }else {
            if(nowMode.mode==2) {
                all.getChildren().add(checks[i]);
            }
            all.getChildren().add(deleters[i]);
            toDoItems.get(i).setChecked();
        }
    }
    private void addDeleters(Pane all){
        for(int i=0;i<toDoItems.size();i++){
            if(toDoItems.get(i).isChecked()) {
                if(nowMode.mode==2) {
                    if(!all.getChildren().contains(checks[i]))
                        all.getChildren().add(checks[i]);
                }
                if(!all.getChildren().contains(deleters[i]))
                    all.getChildren().add(deleters[i]);
            }
        }
    }
    //  删除线，样式的删除线有问题，遂手写
    //  根据checked判断
    private void setDeleters() {
        if(nowMode.mode==2) {
            checks = new Label[toDoItems.size()];
            for(int i=0;i<toDoItems.size();i++) {
                checks[i] = new Label();
                checks[i].setLayoutX(nowMode.lineRatio*width-10);
                checks[i].setLayoutY(nowMode.headRatio*width+i* nowMode.lineRatio*width);
                checks[i].setPrefWidth(15);
                checks[i].setPrefHeight(nowMode.lineRatio*width);
                checks[i].setTextFill(textColor);
                checks[i].setText("√");
                checks[i].setFont(qfont);
                checks[i].setStyle("-fx-font-size: 20px");
                checks[i].setId("check"+i);
            }
        }
        deleters = new Rectangle[toDoItems.size()];
        for(int i=0;i<toDoItems.size();i++) {
            deleters[i] = new Rectangle();
            deleters[i].setX(nowMode.lineRatio*width+10);
            deleters[i].setY(nowMode.headRatio*width+i* nowMode.lineRatio*width+nowMode.lineRatio*width/2.5);
            deleters[i].setWidth(texts[i].getLayoutBounds().getWidth());
            deleters[i].setHeight(2);
            deleters[i].setId("deleter"+i);
            deleters[i].setFill(textColor);
        }
        Node root= stage.getScene().getRoot();
        Pane all = (Pane) root;
        for(int i=0,j=0;i<toDoItems.size();i++) {
            if(toDoItems.get(i).isChecked()) {
               all.getChildren().add(deleters[i]);
               if(nowMode.mode==2) {
                   all.getChildren().add(checks[i]);
               }
            }
        }
    }
    //初始化所有代办事件块
    private HBox[] getListItems() {
        HBox[] items = new HBox[toDoItems.size()];

        BackgroundImage bImg = new BackgroundImage(nowMode.lineImg,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(50,50,true,true,true,false));
        for (int i=0;i<texts.length;i++) {
            HBox square = new HBox();
            square.setPrefHeight(nowMode.lineRatio*width);
            square.setPrefWidth(nowMode.lineRatio*width+10);
            items[i] = new HBox(square,texts[i]);

            items[i].setPrefWidth(width);
            items[i].setPrefHeight(nowMode.lineRatio*width);
            items[i].setBackground(new Background(bImg));
        }
       return items;
    }
    //  初始化所有代办事件文字
    private void setTexts() {
        texts = new Text[toDoItems.size()];
        for(int i=0;i<texts.length;i++){
            texts[i] = new Text(toDoItems.get(i).getText());
            texts[i].setFont(qfont);
            if(nowMode.bgColor.equals("transparent")){
                texts[i].setFill(textColor);
                texts[i].setStrokeWidth(0.6);
                texts[i].setStroke(textColor);
            }
            texts[i].setStyle("-fx-font-size: 26px;-fx-cursor: hand;");
            int now_index =i;
            Text text = texts[now_index];
            texts[i].setOnMouseClicked(event->{
                done(text,now_index);
            });
        }
    }

}


class ToDoMode {
    Image headImg;
    Image lineImg;
    Image bottomImg=null;
    double picWidth;
    double headRatio;
    double lineRatio;
    double bottomRatio;
    int mode;
    String bgColor;
    public ToDoMode(int index) {
        super();
        this.mode = index;
        switch (index) {
            case 1 -> bgColor ="#fffaef";
            case 2 -> bgColor = "rgba(255,255,255,0.5)";
            default -> {
            }
        }
        loadImage();
        setSize();
    }
    private void loadImage(){
        headImg = new Image(getClass().getResource("ContentSrc/todoImg/head-"+mode+".png").toExternalForm());
        lineImg =  new Image(getClass().getResource("ContentSrc/todoImg/line-"+mode+".png").toExternalForm());
        try {
            bottomImg = new Image(getClass().getResource("ContentSrc/todoImg/bottom-"+mode+".png").toExternalForm());
        }catch (Exception e){
            //no bottom
        }
    }
    private void setSize(){
        picWidth = headImg.getWidth();
        headRatio = headImg.getHeight()/picWidth;
        lineRatio = lineImg.getHeight()/picWidth;
        if(bottomImg!=null)bottomRatio=bottomImg.getHeight()/picWidth;
        else bottomRatio = lineRatio;
    }



}