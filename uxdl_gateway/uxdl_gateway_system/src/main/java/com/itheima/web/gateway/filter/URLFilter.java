package com.itheima.web.gateway.filter;

public class URLFilter {

    public static String filterPath = "/admin/virtual_data,/admin/virtual_data/**,/admin/materials,/admin/materials/**,/admin/ads,/admin/ads/**,/admin/admins,/admin/admins/**,/admin/categorys/**,/admin/categorys,/categorys,/admin/tags,/admin/tags/**,/admin/users/**,/admin/users,/admin/statistics/core/,/admin/statistics/core/**";

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
