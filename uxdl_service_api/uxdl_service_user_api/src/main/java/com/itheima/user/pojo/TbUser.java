package com.itheima.user.pojo;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体类
 */
@Table(name = "tb_user")
public class TbUser implements Serializable {
    @Id
    private Long id;//  用户名
    private String phone;//  手机号
    private String password;//  密码
    private String email;//  邮箱
    private Date create_time;//  创建时间
    private Date update_time;//  修改时间
    private String nick_name;//  昵称
    private String name;//  真实姓名
    private Integer status;//  状态
    private String head_pic_url;//  头像地址
    private String weibo;//  微博
    private String weixin;//  微信
    private String weixin_head_pic; //微信头像
    private Integer qq;//  QQ号
    private String qq_head_pic ; //qq头像
    private String is_mobile_check;//  手机号码是否验证
    private String is_email_check;//  邮箱是否验证
    private String sex;//  性别
    private String user_level;//  用户等级
    private Integer points;//  积分
    private Date birthday;//  生日
    private Date last_login_time;//  最后登录时间
    private String update_admin;//更新人
    private Integer material_num;//素材量


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getHead_pic_url() {
        return head_pic_url;
    }

    public void setHead_pic_url(String head_pic_url) {
        this.head_pic_url = head_pic_url;
    }

    public String getWeibo() {
        return weibo;
    }

    public void setWeibo(String weibo) {
        this.weibo = weibo;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    public String getWeixin_head_pic() {
        return weixin_head_pic;
    }

    public void setWeixin_head_pic(String weixin_head_pic) {
        this.weixin_head_pic = weixin_head_pic;
    }

    public Integer getQq() {
        return qq;
    }

    public void setQq(Integer qq) {
        this.qq = qq;
    }

    public String getQq_head_pic() {
        return qq_head_pic;
    }

    public void setQq_head_pic(String qq_head_pic) {
        this.qq_head_pic = qq_head_pic;
    }

    public String getIs_mobile_check() {
        return is_mobile_check;
    }

    public void setIs_mobile_check(String is_mobile_check) {
        this.is_mobile_check = is_mobile_check;
    }

    public String getIs_email_check() {
        return is_email_check;
    }

    public void setIs_email_check(String is_email_check) {
        this.is_email_check = is_email_check;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUser_level() {
        return user_level;
    }

    public void setUser_level(String user_level) {
        this.user_level = user_level;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(Date last_login_time) {
        this.last_login_time = last_login_time;
    }

    public String getUpdate_admin() {
        return update_admin;
    }

    public void setUpdate_admin(String update_admin) {
        this.update_admin = update_admin;
    }

    public Integer getMaterial_num() {
        return material_num;
    }

    public void setMaterial_num(Integer material_num) {
        this.material_num = material_num;
    }
}
