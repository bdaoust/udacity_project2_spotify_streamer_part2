package org.bdaoust.project2spotifystreamerstage2.utils;

import java.util.List;

import kaaes.spotify.webapi.android.models.Image;

public class Tools {

    public static Image findPreferedSizeImageOrSmaller(List<Image> imageList, int preferedSize){
        Image image;
        for(int i=0; i< imageList.size(); i++){
            if(imageList.get(i).width == preferedSize && imageList.get(i).height == preferedSize){
                image = imageList.get(i);
                return image;
            }
        }
        if(imageList.size() > 0) {
            return imageList.get(imageList.size() - 1);
        }
        else {
            return null;
        }
    }

    public static Image findPreferedSizeImageOrLarger(List<Image> imageList, int preferedSize){
        Image image;
        for(int i=0; i< imageList.size(); i++){
            if(imageList.get(i).width == preferedSize && imageList.get(i).height == preferedSize){
                image = imageList.get(i);
                return image;
            }
        }
        if(imageList.size() > 0) {
            return imageList.get(0);
        }
        else {
            return null;
        }
    }

    public static String formatSecondsToTimeString(int seconds){
        if (seconds < 10) {
            return "00:0" + seconds;
        } else {
            return "00:" + seconds;
        }
    }
}
