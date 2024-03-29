package desktop.application.wanderingddl.tools;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import desktop.application.wanderingddl.AnswerBookController;
import desktop.application.wanderingddl.MuYuController;
import desktop.application.wanderingddl.ToDoItem;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;


import java.io.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class SaverAndLoader {
    private String  pathHead ="src/main/resources/desktop/application/wanderingddl/Cache/";

    public static SaverAndLoader tool=new SaverAndLoader();
    private SaverAndLoader(){

    }
    private class WanderingItem {
        String content;
        public WanderingItem(String str) {
            super();
            content = str;
        }
    }
    public void loadPage(Node node, int index) {
        switch (index) {
            case 0 -> loadWanderingInput(node);
            case 1 -> loadToDoInput();
            case 2 -> loadAnswerBook();
            case 3 -> loadMerit();
            default -> {
            }
        }
    }
    private void loadAnswerBook(){
        JSONArray jsa = read("src/main/resources/desktop/application/wanderingddl/ContentSrc/AnswerBooks/","allAnswers");
        AnswerBookController.setAnswers(jsa);
    }

    public LinkedList<ToDoItem> loadToDoInput() {
        JSONObject jsonObject = read("ToDoList");
        LinkedList<ToDoItem> toDoItems = new LinkedList<ToDoItem>();
        if(jsonObject==null)
            return toDoItems;
        JSONArray jsonArray = jsonObject.getJSONArray("toDoItems");
        if(jsonArray==null)
            return toDoItems;
        for(int i=0;i<jsonArray.size();i++) {
            JSONObject object=jsonArray.getJSONObject(i);
            final ToDoItem toDoItem = new ToDoItem(object.getString("text"));
            if(object.getString("checked").equals("true")){
                toDoItem.setChecked();
            }
            toDoItems.add(toDoItem);
        }
        return toDoItems;
    }

    public void loadWanderingInput(Node node){
        JSONObject jsonObject = read("wanderingDDL");
        if(jsonObject==null)return;
        JSONArray jsa=jsonObject.getJSONArray("last-modified");
        if(jsa==null)return;
        Pane all = (Pane)node;
        System.out.println(jsa);
        ((TextField) all.lookup("#0")).setText(jsa.get(0).toString());//某某作业
        ((TextField) all.lookup("#1")).setText(jsa.get(1).toString());
        int x = countDate(jsonObject,Integer.parseInt(jsa.get(2).toString()),jsa.get(3).toString());
        ((Spinner) all.lookup("#2")).getValueFactory().setValue(x);//x
        ((MenuButton) all.lookup("#3")).setText(jsa.get(3).toString());//天
        ((Label) all.lookup("#6")).setText(Chi2Eng(jsa.get(3).toString()));//days
        ((TextField) all.lookup("#4")).setText(jsa.get(4).toString());
        ((TextField) all.lookup("#7")).setText(jsa.get(5).toString());
    }
    public void loadMerit(){
        JSONObject jsonObject = read("MuYuMerit");
        if(jsonObject==null)return;
        try{
            int count =(int)jsonObject.get("last-merit");
            MuYuController.getInstance().setCount(count);
        }catch (Exception e) {
            return;
        }
    }
    private String Chi2Eng(String sentences) {
        switch (sentences) {
            case "小时              ":
                return " hours";
            case "天":
                return " days";
            case "星期":
                return " weeks";
            case "月":
                return " months";
            case "秒":
                return "seconds";
            case "分":
                return "minutes";
            default:
                break;
        }
        return sentences;
    }
    public int countDate(JSONObject jsonObject,int originNumber,String timeCount) {
        Date saveDate = new Date(jsonObject.getString("time-stamp"));
        System.out.println(saveDate);
        Date nowDate = new Date();
        long time=0;
        long between = nowDate.getTime() - saveDate.getTime();
        switch (timeCount) {
            case "seconds" -> time = between / 1000;
            case "minutes" -> time = between / (1000 * 60);
            case "hours" -> time = between / (60 * 60 * 1000);
            case "days" -> time = between / (24 * 60 * 60 * 1000);
            case "weeks" -> time = between / (24 * 60 * 60 * 1000 * 7);
            case "months" -> time = between / (24L * 60 * 60 * 1000 * 7 * 30);
        }
        int finalNumber = originNumber-(int)time;
        if(finalNumber<0) finalNumber=0;
        return finalNumber;

    }

    public void saveWanderingInput(String[] data){
        WanderingItem[] list = new WanderingItem[data.length];
        for (int i = 0; i < data.length; i++) {
            list[i] = new WanderingItem(data[i]);
        }
        Date nowDate = new Date();
        JSONArray jsonstr = JSONArray.from(data);
        JSONObject jsonObject = read("wanderingDDL");

        jsonObject.put("last-modified",jsonstr);
        jsonObject.put("time-stamp", nowDate.toString());
        save(jsonObject,"wanderingDDL");
    }
    public void saveWanderingInput(String data){
        Date nowDate = new Date();
        JSONObject jsonObject = read("wanderingDDL");

        JSONArray jsonstr = jsonObject.getJSONArray("last-modified");
        jsonstr.set(2,data);
        jsonObject.put("last-modified",jsonstr);
        jsonObject.put("time-stamp", nowDate.toString());
        save(jsonObject,"wanderingDDL");
    }
    public void saveToDoItems(LinkedList<ToDoItem> toDoItems){
        JSONArray jsonArray = JSONArray.from(toDoItems);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("toDoItems",jsonArray);
        save(jsonObject,"ToDoList");
    }
    public void saveMuYuMerit(int number) {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("last-merit",number);
        save(jsonObject,"MuYuMerit");
    }
    private void save(JSONObject jsonObject, String filePath){
        try{ BufferedWriter bw = new BufferedWriter(new FileWriter(pathHead+filePath+".json"));
            bw.write(jsonObject.toString());
            bw.flush();
            bw.close();
        }catch (IOException e) {
            System.out.println("save error");
        }
    }
    private JSONObject read(String filename){
        String path=pathHead+filename+".json";
        StringBuffer stringBuffer = new StringBuffer();
        try {
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"));

            String line;
            while((line=bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//开始解析

        JSONObject arr = JSONObject.parseObject(stringBuffer.toString());
        return arr;
    }
    private JSONArray read(String pathHead,String filename) {
        String path=pathHead+filename+".json";
        StringBuffer stringBuffer = new StringBuffer();
        try {
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"));

            String line;
            while((line=bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//开始解析

        JSONArray arr = JSONArray.parseArray(stringBuffer.toString());
        return arr;
    }


}
