package com.itheima.web.gateway.filter;

public class URLFilter {

    public static String filterPath = "/usercenter/**,/favourite/**,/usercenter/personal/works,/users/info,/usercenter/works/**,/usercenter/personal/works";

    public static boolean hasAuthorize(String url){

        String[] split = filterPath.replace("**", "").split(",");

        for (String value : split) {

            if (url.startsWith(value)){
                return true;
            }
        }

        return false;
    }
}
