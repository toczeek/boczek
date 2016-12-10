package com.example.toczek.wrumwrum.Utils.models;


import android.support.v4.app.Fragment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Created by Toczek on 2016-12-10.
 */
//TODO change constructor to lombok annotation
@Data
public class NavItem {
    private int navIcon;
    private String navTitle;
    private Fragment fragment;
    public NavItem(int navIcon, String navTitle, Fragment fragment) {
        this.navIcon = navIcon;
        this.navTitle = navTitle;
        this.fragment = fragment;
    }
}
