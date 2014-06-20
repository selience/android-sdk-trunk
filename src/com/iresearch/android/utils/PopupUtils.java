package com.iresearch.android.utils;

import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @file PopupUtils.java
 * @create 2013-7-17 下午05:19:08
 * @author Jacky.Lee
 * @description TODO 弹出框Popup常用操作
 */
public class PopupUtils {
    
    private PopupUtils() {
    }

     /** 设置PopupWindow显示位置 ：显示在View上方 */
    public static PopupWindow showPopupWindowAtTop(PopupWindow popup, View anchor) {
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        popup.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0], location[1]-popup.getHeight());
        return popup;
    }
    
    /** 设置PopupWindow显示位置 ：显示在View下方 */
    public static PopupWindow showPopupWindowAtBotton(PopupWindow popup, View anchor) {
        popup.showAsDropDown(anchor);
        return popup;
    }
    
    /** 设置PopupWindow显示位置 ：显示在View左边 */
    public static PopupWindow showPopupWindowAtLeft(PopupWindow popup, View anchor) {
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        popup.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0]-popup.getWidth(), location[1]);
        return popup;
    }
    
    /** 设置PopupWindow显示位置 ：显示在View左边 */
    public static PopupWindow showPopupWindowAtRight(PopupWindow popup, View anchor) {
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        popup.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0]+popup.getWidth(), location[1]);
        return popup;
    }
    
    /**
     * 反射处理PopupMenu显示默认图标
     * @param popupMenu
     */
    public static void showActionViewIcon(PopupMenu popupMenu) {
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
