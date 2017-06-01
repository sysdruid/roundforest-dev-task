package com.cib.roundforest;

/**
 *
 * @author Yury Altukhou
 */
public class InputRecord {
    private int id;
    private String productId;
    private String userId;
    private String profileName;
    private int helpfulnessNumerator;
    private int helpfulnessDenominator;
    private int score;
    private long time;
    private String summary;
    private String text;

    /**
     * @return the helpfulnessDenominator
     */
    public int getHelpfulnessDenominator() {
        return helpfulnessDenominator;
    }

    /**
     * @param helpfulnessDenominator the helpfulnessDenominator to set
     */
    public void setHelpfulnessDenominator(int helpfulnessDenominator) {
        this.helpfulnessDenominator = helpfulnessDenominator;
    }

    /**
     * @return the helpfulnessNumerator
     */
    public int getHelpfulnessNumerator() {
        return helpfulnessNumerator;
    }

    /**
     * @param helpfulnessNumerator the helpfulnessNumerator to set
     */
    public void setHelpfulnessNumerator(int helpfulnessNumerator) {
        this.helpfulnessNumerator = helpfulnessNumerator;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the productId
     */
    public String getProductId() {
        return productId;
    }

    /**
     * @param productId the productId to set
     */
    public void setProductId(String productId) {
        this.productId = productId;
    }

    /**
     * @return the profileName
     */
    public String getProfileName() {
        return profileName;
    }

    /**
     * @param profileName the profileName to set
     */
    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    /**
     * @return the score
     */
    public int getScore() {
        return score;
    }

    /**
     * @param score the score to set
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * @return the summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @param summary the summary to set
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the time
     */
    public long getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

}