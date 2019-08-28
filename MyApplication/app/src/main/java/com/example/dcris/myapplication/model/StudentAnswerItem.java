package com.example.dcris.myapplication.model;


import java.io.Serializable;

public class StudentAnswerItem implements Serializable {
    private String mQuestion;
    private String[] mGivenAnsArr;



    public StudentAnswerItem(String question, String[] givenAnswers){
        mQuestion = question;
        mGivenAnsArr = givenAnswers;
    }

    public String getQuestion() {
        return mQuestion;
    }
    public String[] getGivenAnsArr() {
        return mGivenAnsArr;
    }

    public void setQuestion(String mQuestion) {
        this.mQuestion = mQuestion;
    }

    public void setGivenAnsArr(String[] mGivenAnsArr) {
        this.mGivenAnsArr = mGivenAnsArr;
    }

}