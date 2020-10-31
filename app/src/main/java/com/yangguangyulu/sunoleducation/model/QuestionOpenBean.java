package com.yangguangyulu.sunoleducation.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class QuestionOpenBean implements Parcelable {

//    {"questionOpens":[{"id":228,"question":"做的什么事赚大钱？","answers":["养羊","养猪"]},{"id":229,"question":"干了多久？","answers":["1年","2年"]}]}

    private int id;
    private String question;
    private List<String> answers;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.question);
        dest.writeStringList(this.answers);
    }

    public QuestionOpenBean() {
    }

    protected QuestionOpenBean(Parcel in) {
        this.id = in.readInt();
        this.question = in.readString();
        this.answers = in.createStringArrayList();
    }

    public static final Parcelable.Creator<QuestionOpenBean> CREATOR = new Parcelable.Creator<QuestionOpenBean>() {
        @Override
        public QuestionOpenBean createFromParcel(Parcel source) {
            return new QuestionOpenBean(source);
        }

        @Override
        public QuestionOpenBean[] newArray(int size) {
            return new QuestionOpenBean[size];
        }
    };
}
