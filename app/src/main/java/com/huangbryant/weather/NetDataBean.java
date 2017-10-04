package com.huangbryant.weather;

/**
 * Created by kobe on 2017/10/3.
 */

public class NetDataBean {

    /**
     * data : {"id":"1","0":"1","url":"http://m.6769c.com","1":"http://m.6769c.com","type":"android","2":"android","show_url":"0","3":"0","appid":"test","4":"test","comment":"test","5":"test","createAt":"2009-10-20 11:10:45","6":"2009-10-20 11:10:45","updateAt":"2009-11-19 05:40:58","7":"2009-11-19 05:40:58"}
     * rt_code : 200
     */

    private DataBean data;
    private String rt_code;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getRt_code() {
        return rt_code;
    }

    public void setRt_code(String rt_code) {
        this.rt_code = rt_code;
    }

    public  class DataBean {
        /**
         * id : 1
         * 0 : 1
         * url : http://m.6769c.com
         * 1 : http://m.6769c.com
         * type : android
         * 2 : android
         * show_url : 0
         * 3 : 0
         * appid : test
         * 4 : test
         * comment : test
         * 5 : test
         * createAt : 2009-10-20 11:10:45
         * 6 : 2009-10-20 11:10:45
         * updateAt : 2009-11-19 05:40:58
         * 7 : 2009-11-19 05:40:58
         */

        private String id;
        @com.google.gson.annotations.SerializedName("0")
        private String _$0;
        private String url;
        @com.google.gson.annotations.SerializedName("1")
        private String _$1;
        private String type;
        @com.google.gson.annotations.SerializedName("2")
        private String _$2;
        private String show_url;
        @com.google.gson.annotations.SerializedName("3")
        private String _$3;
        private String appid;
        @com.google.gson.annotations.SerializedName("4")
        private String _$4;
        private String comment;
        @com.google.gson.annotations.SerializedName("5")
        private String _$5;
        private String createAt;
        @com.google.gson.annotations.SerializedName("6")
        private String _$6;
        private String updateAt;
        @com.google.gson.annotations.SerializedName("7")
        private String _$7;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String get_$0() {
            return _$0;
        }

        public void set_$0(String _$0) {
            this._$0 = _$0;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String get_$1() {
            return _$1;
        }

        public void set_$1(String _$1) {
            this._$1 = _$1;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String get_$2() {
            return _$2;
        }

        public void set_$2(String _$2) {
            this._$2 = _$2;
        }

        public String getShow_url() {
            return show_url;
        }

        public void setShow_url(String show_url) {
            this.show_url = show_url;
        }

        public String get_$3() {
            return _$3;
        }

        public void set_$3(String _$3) {
            this._$3 = _$3;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String get_$4() {
            return _$4;
        }

        public void set_$4(String _$4) {
            this._$4 = _$4;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String get_$5() {
            return _$5;
        }

        public void set_$5(String _$5) {
            this._$5 = _$5;
        }

        public String getCreateAt() {
            return createAt;
        }

        public void setCreateAt(String createAt) {
            this.createAt = createAt;
        }

        public String get_$6() {
            return _$6;
        }

        public void set_$6(String _$6) {
            this._$6 = _$6;
        }

        public String getUpdateAt() {
            return updateAt;
        }

        public void setUpdateAt(String updateAt) {
            this.updateAt = updateAt;
        }

        public String get_$7() {
            return _$7;
        }

        public void set_$7(String _$7) {
            this._$7 = _$7;
        }
    }
}
