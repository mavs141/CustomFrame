package com.example.chenyijie.customframe.common;

import java.util.ArrayList;
import java.util.List;

/**
 * 這個method其實很單純只是提供array list的劃分，因為我不想要讓所有的
 * 資料都一次寫入DB，怕記憶體爆炸
 *
 * Created by chenyijie on 2017/5/21.
 */

public class ListManager {

    private static ListManager instance ;

    public static ListManager getInstance(){
        if(instance == null) instance = new ListManager();
        return instance;
    }

    public  <T> List<List<T>> chopped(List<T> list, final int L) {
        List<List<T>> parts = new ArrayList<>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
    }
}
