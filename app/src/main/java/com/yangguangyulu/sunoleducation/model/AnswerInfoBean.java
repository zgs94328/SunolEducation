package com.yangguangyulu.sunoleducation.model;

public class AnswerInfoBean {

//    {"note":"总共有2题。答对0题!您此次只能获得15分钟的学习时长!下次加油!","timeLong":15,"rightNum":0,"allNum":2}

    private String note;
    private long timeLong;
    private int rightNum;
    private int allNum;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getTimeLong() {
        return timeLong;
    }

    public void setTimeLong(long timeLong) {
        this.timeLong = timeLong;
    }

    public int getRightNum() {
        return rightNum;
    }

    public void setRightNum(int rightNum) {
        this.rightNum = rightNum;
    }

    public int getAllNum() {
        return allNum;
    }

    public void setAllNum(int allNum) {
        this.allNum = allNum;
    }

}
