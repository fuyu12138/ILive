package com.graduation.fuyu.ilive.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * 标签
 * Created by root on 18-5-17.
 */

public class Label {
    public static List<String> label=new ArrayList<>();
    public static List<String> classification=new ArrayList<>();
    private static List<String> common=new ArrayList<>();
    private List<String> entertainment;
    private List<String> pcGame;
    private List<String> phoneGame;
    private List<String> painting;

    private List<LabelBean> entertainmentBean;
    private List<LabelBean> pcGameBean;
    private List<LabelBean> phoneGameBean;
    private List<LabelBean> paintingBean;
    private List<LabelBean> commonBean;

    private List<String> entertainment_sample;
    private List<String> pcGame_sample;
    private List<String> phoneGame_sample;
    private List<String> painting_sample;


    public static void setCommon(List<String> common) {
        Label.common = common;
    }



    public static void setLabel(List<String> label) {
        Label.label = label;
    }

    public static void setClassification(List<String> classification) {
        Label.classification = classification;
    }

    public List<String> getLabel() {
        return label;
    }

    public List<String> getClassification() {
        return classification;
    }

    public List<LabelBean> getCommonBean(){
        commonBean=new ArrayList<>();
        for (String labels: common) {
            LabelBean labelBean=new LabelBean(labels,false);
            commonBean.add(labelBean);
        }
        if (commonBean.size()!=0){
            commonBean.get(0).setSelected(true);
        }
        return commonBean;
    }

    public  List<LabelBean> getEntertainmentBean(){
        if (entertainmentBean==null){
            entertainmentBean =new ArrayList<>();
            LabelBean labelBean;
//            entertainmentBean.add(labelBean);
            for (int i=0;i<label.size();i++){
                if (classification.get(i).equals("entertainment")){
                    labelBean=new LabelBean(label.get(i),false);
                    entertainmentBean.add(labelBean);
                }
            }

        }
        if (entertainmentBean.size()!=0){
            entertainmentBean.get(0).setSelected(true);
        }
        return entertainmentBean;
    }

    public  List<LabelBean> getPcGameBeanBean(){
        if (pcGameBean==null){
            pcGameBean =new ArrayList<>();
            LabelBean labelBean;
            for (int i=0;i<label.size();i++){
                if (classification.get(i).equals("pcgame")){
                    labelBean=new LabelBean(label.get(i),false);
                    pcGameBean.add(labelBean);
                }
            }
        }
        if (pcGameBean.size()!=0){
            pcGameBean.get(0).setSelected(true);
        }
        return pcGameBean;
    }

    public  List<LabelBean> getphoneGameBean(){
        if (phoneGameBean==null){
            phoneGameBean =new ArrayList<>();
            LabelBean labelBean;
            for (int i=0;i<label.size();i++){
                if (classification.get(i).equals("phonegame")){
                    labelBean=new LabelBean(label.get(i),false);
                    phoneGameBean.add(labelBean);
                }
            }
        }
        if (phoneGameBean.size()!=0){
            phoneGameBean.get(0).setSelected(true);
        }
        return phoneGameBean;
    }

    public  List<String> getEntertainment(){
        if (entertainment==null){
            entertainment =new ArrayList<>();
            entertainment.add("全部");
            for (int i=0;i<label.size();i++){
                if (classification.get(i).equals("entertainment")){
                    entertainment.add(label.get(i));
                }
            }
        }
        return entertainment;
    }

    public  List<String> getPcGame(){
        if (pcGame==null){
            pcGame =new ArrayList<>();
            pcGame.add("全部");
            for (int i=0;i<label.size();i++){
                if (classification.get(i).equals("pcgame")){
                    pcGame.add(label.get(i));
                }
            }
        }
        return pcGame;
    }

    public  List<String> getphoneGame(){
        if (phoneGame==null){
            phoneGame =new ArrayList<>();
            phoneGame.add("全部");
            for (int i=0;i<label.size();i++){
                if (classification.get(i).equals("phonegame")){
                    phoneGame.add(label.get(i));
                }
            }
        }
        return phoneGame;
    }
//    public  List<String> getpainting(){
//        if (painting==null){
//            painting =new ArrayList<>();
//            painting.add("全部");
//            for (int i=0;i<label.size();i++){
//                if (classification.get(i).equals("painting")){
//                    painting.add(label.get(i));
//                }
//            }
//        }
//        return painting;
//    }

    /*
    ======================================No全部=====================================================
     */
    public  List<String> getEntertainment_sample(){
        if (entertainment_sample==null){
            entertainment_sample =new ArrayList<>();
            for (int i=0;i<label.size();i++){
                if (classification.get(i).equals("entertainment")){
                    entertainment_sample.add(label.get(i));
                }
            }
        }
        return entertainment_sample;
    }

    public  List<String> getPcGame_sample(){
        if (pcGame_sample==null){
            pcGame_sample =new ArrayList<>();
            for (int i=0;i<label.size();i++){
                if (classification.get(i).equals("pcgame")){
                    pcGame_sample.add(label.get(i));
                }
            }
        }
        return pcGame_sample;
    }

    public  List<String> getphoneGame_sample(){
        if (phoneGame_sample==null){
            phoneGame_sample =new ArrayList<>();
            for (int i=0;i<label.size();i++){
                if (classification.get(i).equals("phonegame")){
                    phoneGame_sample.add(label.get(i));
                }
            }
        }
        return phoneGame_sample;
    }
    public  List<String> getpainting_sample(){
        if (painting_sample==null){
            painting_sample =new ArrayList<>();
            for (int i=0;i<label.size();i++){
                if (classification.get(i).equals("painting")){
                    painting_sample.add(label.get(i));
                }
            }
        }
        return painting_sample;
    }
    public  List<String> getCommon(){
        return common;
    }
}
