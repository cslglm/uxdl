package com.itheima.favourite.Pojo;

import javax.persistence.Table;

/**
 * 用户收藏实体类
 */
@Table(name = "tb_user_material")
public class Favourite {

    private String material_id;

    private Long user_id;

    public String getMaterial_id() {
        return material_id;
    }

    public void setMaterial_id(String material_id) {
        this.material_id = material_id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }
}
