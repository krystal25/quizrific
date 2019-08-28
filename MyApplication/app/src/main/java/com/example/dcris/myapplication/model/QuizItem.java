package com.example.dcris.myapplication.model;

public class QuizItem {
    private String mQuiz;
    private String mCourse;
    private int mQuestions;

    public QuizItem(String quiz, String course, int questionsCount) {
        mQuiz = quiz;
        mCourse = course;
        mQuestions = questionsCount;
    }


    public String getQuiz() {
        return mQuiz;
    }

    public String getCourse() {
        return mCourse;
    }

    public int getQuestionsCount() {
        return mQuestions;
    }
}
