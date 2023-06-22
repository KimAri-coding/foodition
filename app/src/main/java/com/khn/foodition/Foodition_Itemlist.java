package com.khn.foodition;

import android.graphics.drawable.Drawable;

public class Foodition_Itemlist {
    String name;
    String area;
    String stdate;
    String enddate;
    String dday;
    int img;

    public Foodition_Itemlist(String name, String area, String stdate, String enddate, String dday, int img) {

        this. name = name;
        this. area = area;
        this. stdate = stdate;
        this. enddate = enddate;
        this. dday = dday;
        this. img = img;
    }
    public void setName (String name) {  this.name = name;  }
    public String getName(){   return name;  }
    public void setArea (String area) {  this.area = area;  }
    public String getArea(){   return area;  }
    public void setStdate (String stdate) {  this.stdate = stdate;  }
    public String getStdate(){   return stdate;  }
    public void setEnddate (String enddate) {  this.enddate = enddate;  }
    public String getEnddate(){   return enddate;  }
    public void setDday (String dday) {  this.dday = dday;  }
    public String getDday(){ return dday;  }

    public void setImg (int img) {  this.img = img;  }
    public int getImg(){ return img; }
}

