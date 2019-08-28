package com.example.dcris.myapplication.model;

public class ScheduledItem {
    private String mQuiz;
    private String mCourse;
    private String mProf;
    private String mDate;
    private String mTime;
    private String mDuration;

    public ScheduledItem(String quiz, String course, String user,String date, String time, String duration) {
        mQuiz = quiz;
        mCourse = course;
        mProf = user;
        mDate = date;
        mTime = time;
        mDuration = duration;
    }

    public String getQuiz() {
        return mQuiz;
    }

    public String getCourse() {
        return mCourse;
    }

    public String getProf() {
        return mProf;
    }

    public String getDate() {
        return mDate;
    }

    public String getTime() {
        return mTime;
    }

    public String getDuration() {
        return mDuration;
    }
}
