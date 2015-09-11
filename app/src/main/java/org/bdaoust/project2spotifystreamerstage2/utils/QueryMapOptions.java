package org.bdaoust.project2spotifystreamerstage2.utils;

import java.lang.annotation.Annotation;
import java.util.HashMap;

import retrofit.http.QueryMap;

public class QueryMapOptions extends HashMap<String,Object> implements QueryMap{

    @Override
    public boolean encodeNames() {
        return false;
    }

    @Override
    public boolean encodeValues() {
        return false;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return null;
    }
}
