package com.example.treasurehuntapp.createhunt;


public class ItemNextStep {

    private String stepId;

    private String stepLatitude;

    private String stepLongitude;

    private String stepMaxDuration;

    private String scorePointsGivenIfSuccess;

    private String stepDescription;

    private String riddleText;

    private String riddleJokerText;

    private  boolean isMCQRiddle;

    private String answerTextRidlle;

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public String getStepLatitude() {
        return stepLatitude;
    }

    public void setStepLatitude(String stepLatitude) {
        this.stepLatitude = stepLatitude;
    }

    public String getStepLongitude() {
        return stepLongitude;
    }

    public void setStepLongitude(String stepLongitude) {
        this.stepLongitude = stepLongitude;
    }

    public String getStepMaxDuration() {
        return stepMaxDuration;
    }

    public void setStepMaxDuration(String stepMaxDuration) {
        this.stepMaxDuration = stepMaxDuration;
    }

    public String getScorePointsGivenIfSuccess() {
        return scorePointsGivenIfSuccess;
    }

    public void setScorePointsGivenIfSuccess(String scorePointsGivenIfSuccess) {
        this.scorePointsGivenIfSuccess = scorePointsGivenIfSuccess;
    }

    public String getStepDescription() {
        return stepDescription;
    }

    public void setStepDescription(String stepDescription) {
        this.stepDescription = stepDescription;
    }

    public String getRiddleText() {
        return riddleText;
    }

    public void setRiddleText(String riddleText) {
        this.riddleText = riddleText;
    }

    public String getRiddleJokerText() {
        return riddleJokerText;
    }

    public void setRiddleJokerText(String riddleJokerText) {
        this.riddleJokerText = riddleJokerText;
    }

    public boolean isMCQRiddle() {
        return isMCQRiddle;
    }

    public void setMCQRiddle(boolean MCQRiddle) {
        isMCQRiddle = MCQRiddle;
    }

    public String getAnswerTextRidlle() {
        return answerTextRidlle;
    }

    public void setAnswerTextRidlle(String answerTextRidlle) {
        this.answerTextRidlle = answerTextRidlle;
    }
}
