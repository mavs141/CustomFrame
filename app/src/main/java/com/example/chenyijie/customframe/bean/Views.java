package com.example.chenyijie.customframe.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by chenyijie on 2017/5/18.
 */

public class Views {
    @Getter @Setter @SerializedName("Info") private List<ViewInfo> infos;
}
