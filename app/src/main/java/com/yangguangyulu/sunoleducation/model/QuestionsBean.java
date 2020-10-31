package com.yangguangyulu.sunoleducation.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class QuestionsBean implements Parcelable {

    private List<QuestionOpenBean> questionOpens;

    public List<QuestionOpenBean> getQuestionOpens() {
        return questionOpens;
    }

    public void setQuestionOpens(List<QuestionOpenBean> questionOpens) {
        this.questionOpens = questionOpens;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.questionOpens);
    }

    public QuestionsBean() {
    }

    protected QuestionsBean(Parcel in) {
        this.questionOpens = in.createTypedArrayList(QuestionOpenBean.CREATOR);
    }

    public static final Parcelable.Creator<QuestionsBean> CREATOR = new Parcelable.Creator<QuestionsBean>() {
        @Override
        public QuestionsBean createFromParcel(Parcel source) {
            return new QuestionsBean(source);
        }

        @Override
        public QuestionsBean[] newArray(int size) {
            return new QuestionsBean[size];
        }
    };
}
