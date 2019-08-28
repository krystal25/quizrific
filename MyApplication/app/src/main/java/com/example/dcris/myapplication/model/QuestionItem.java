package com.example.dcris.myapplication.model;


import java.io.Serializable;

public class QuestionItem implements Serializable {
    private String mQuestion;
    private String mPoints;
    private String mImage;
    private String[] mCorrectArr;
    private String correctA; // for trivia db api
    private String[] mWrongArr;


    public QuestionItem(String question, String correctAnswer, String[] wrongArr){
        mQuestion = question;
        correctA = correctAnswer;
        mWrongArr = wrongArr;
    }
    public QuestionItem(String question, String points, String image, String[] correct, String[] wrongArr) {
        mQuestion = question;
        mPoints = points;
        mImage = image;
        mCorrectArr = correct;
        mWrongArr = wrongArr;

    }


    public String getQuestion() {
        return mQuestion;
    }

    public String getPoints() {
        return mPoints;
    }

    public String getImage() {
        return mImage;
    }

    public String[] getCorrectArr() {
        return mCorrectArr;
    }

    public String getCorrect() {
        return correctA;
    }

    public String[] getWrongArr(){ return mWrongArr;}


}