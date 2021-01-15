package com.android.lib.media.player;

import com.google.gson.annotations.SerializedName;

/**
 * @author Alan
 * 时 间：2020/12/3
 * 简 述：<功能简述>
 */
public class PayInfoModel {


    /**
     * course_id : 1
     * course_name : 初高衔接
     * course_old_price : 100
     * course_price : 50
     * course_top_image : http://bsource.weicistudy.com/banner/gaozhong/2020sq-jc.png
     * course_synopsis : http://bsource.weicistudy.com/operation/linkmultipleimage.html?id=9
     * course_info : [{"id":1,"catalog_name":"开篇","children":[{"id":1,"title":"欢迎来到","try_flag":1,"try_time":0,"total_time":300}]},{"id":2,"catalog_name":"词汇篇","children":[{"id":2,"title":"构词法一","try_flag":1,"try_time":120,"total_time":3623},{"id":8,"title":"123","try_flag":0,"try_time":0,"total_time":600}]},{"id":3,"catalog_name":"阅读","children":[]},{"id":4,"catalog_name":"听力","children":[]}]
     * pay_flag : 0
     * <p>
     * share_url：""
     * title:"分享标题"
     * title_sub:"分享子标题"
     */

    @SerializedName("course_id")
    private int courseId;
    @SerializedName("course_name")
    private String courseName;
    @SerializedName("course_old_price")
    private float courseOldPrice;
    @SerializedName("course_price")
    private float coursePrice;
    @SerializedName("course_top_image")
    private String courseTopImage;
    @SerializedName("course_synopsis")
    private String courseSynopsis;
    @SerializedName("course_info")
    private String courseInfo;
    @SerializedName("pay_flag")
    private int payFlag;

    @SerializedName("share_url")
    private String shareUrl;

    @SerializedName("title")
    private String shareTitle;

    @SerializedName("title_sub")
    private String shareSubTitle;

    @SerializedName("share_image")
    private String imgUrl;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public float getCourseOldPrice() {
        return courseOldPrice;
    }

    public void setCourseOldPrice(int courseOldPrice) {
        this.courseOldPrice = courseOldPrice;
    }

    public float getCoursePrice() {
        return coursePrice;
    }

    public void setCoursePrice(int coursePrice) {
        this.coursePrice = coursePrice;
    }

    public String getCourseTopImage() {
        return courseTopImage;
    }

    public void setCourseTopImage(String courseTopImage) {
        this.courseTopImage = courseTopImage;
    }

    public String getCourseSynopsis() {
        return courseSynopsis;
    }

    public void setCourseSynopsis(String courseSynopsis) {
        this.courseSynopsis = courseSynopsis;
    }

    public String getCourseInfo() {
        return courseInfo;
    }

    public void setCourseInfo(String courseInfo) {
        this.courseInfo = courseInfo;
    }

    public int getPayFlag() {
        return payFlag;
    }

    public void setPayFlag(int payFlag) {
        this.payFlag = payFlag;
    }

    public boolean isPay() {
        return payFlag == 1;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public void setShareSubTitle(String shareSubTitle) {
        this.shareSubTitle = shareSubTitle;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public String getShareSubTitle() {
        return shareSubTitle;
    }
}
