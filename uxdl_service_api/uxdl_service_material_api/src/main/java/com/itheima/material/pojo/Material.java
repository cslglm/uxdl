package com.itheima.material.pojo;



import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 素材实体类
 */
@Table(name = "tb_material")
public class Material implements Serializable {

    @Id
    private String id;
    private String name;                 //名称
    private Integer category1_id;        //一级分类
    private Integer category2_id;        //二级分类
    private Integer category3_id;        //三级分类
    private Integer category4_id;        //四级分类
    private String category1_name;        //一级分类名称
    private String category2_name;        //二级分类名称
    private String category3_name;        //三级分类名称
    private String category4_name;        //四级分类名称
    private String user_id;              //用户id
    private Integer copyright_id;        //版权id
    private Copyright copyright;         //版权信息
    private String show_image;           //展示图片
    private String details_image;        //详情图片
    private Integer status;              //审核状态
    private String filetype;             //文件类型
    private String downloadurl;          //下载地址
    private Double price;                //价格
    private Date create_time;            //创建时间
    private Date updata_time;            //修改时间
    private String description;          //作品描述
    private String musage;               //作品用途
    private String tags;                  //素材标签Id
    private List<Tag> tagList;            //标签
    private String update_admin;          //更新人
    private String source_file;//  源文件
    private String source_type;//  源文件格式
    private String source_size;//  分辨率px
    private String source_dpi ;//  分辨率dpi
    private Integer draft      ;//  0表示发布，1表示保存为草稿（注意垃圾图片处理）
    private Integer isdel;     //是否删除0为不删除，1为删除
    private Integer favourite_num;//收藏数
    private Integer basic_favourite_num;//虚拟收藏数
    private Integer visit_num;//访问数
    private Integer basic_visit_num;//虚拟访问数
    private Integer download_num;//下载数
    private Integer basic_download_num;//虚拟下载数
    private String user;//用户名

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCategory1_name() {
        return category1_name;
    }

    public void setCategory1_name(String category1_name) {
        this.category1_name = category1_name;
    }

    public String getCategory2_name() {
        return category2_name;
    }

    public void setCategory2_name(String category2_name) {
        this.category2_name = category2_name;
    }

    public String getCategory3_name() {
        return category3_name;
    }

    public void setCategory3_name(String category3_name) {
        this.category3_name = category3_name;
    }

    public String getCategory4_name() {
        return category4_name;
    }

    public void setCategory4_name(String category4_name) {
        this.category4_name = category4_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCategory1_id() {
        return category1_id;
    }

    public void setCategory1_id(Integer category1_id) {
        this.category1_id = category1_id;
    }

    public Integer getCategory2_id() {
        return category2_id;
    }

    public void setCategory2_id(Integer category2_id) {
        this.category2_id = category2_id;
    }

    public Integer getCategory3_id() {
        return category3_id;
    }

    public void setCategory3_id(Integer category3_id) {
        this.category3_id = category3_id;
    }

    public Integer getCategory4_id() {
        return category4_id;
    }

    public void setCategory4_id(Integer category4_id) {
        this.category4_id = category4_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Integer getCopyright_id() {
        return copyright_id;
    }

    public void setCopyright_id(Integer copyright_id) {
        this.copyright_id = copyright_id;
    }

    public Copyright getCopyright() {
        return copyright;
    }

    public void setCopyright(Copyright copyright) {
        this.copyright = copyright;
    }

    public String getShow_image() {
        return show_image;
    }

    public void setShow_image(String show_image) {
        this.show_image = show_image;
    }

    public String getDetails_image() {
        return details_image;
    }

    public void setDetails_image(String details_image) {
        this.details_image = details_image;
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getDownloadurl() {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getUpdata_time() {
        return updata_time;
    }

    public void setUpdata_time(Date updata_time) {
        this.updata_time = updata_time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMusage() {
        return musage;
    }

    public void setMusage(String musage) {
        this.musage = musage;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<Tag> getTagList() {
        return tagList;
    }

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }

    public String getUpdate_admin() {
        return update_admin;
    }

    public void setUpdate_admin(String update_admin) {
        this.update_admin = update_admin;
    }

    public String getSource_file() {
        return source_file;
    }

    public void setSource_file(String source_file) {
        this.source_file = source_file;
    }

    public String getSource_type() {
        return source_type;
    }

    public void setSource_type(String source_type) {
        this.source_type = source_type;
    }

    public String getSource_size() {
        return source_size;
    }

    public void setSource_size(String source_size) {
        this.source_size = source_size;
    }

    public String getSource_dpi() {
        return source_dpi;
    }

    public void setSource_dpi(String source_dpi) {
        this.source_dpi = source_dpi;
    }

    public Integer getDraft() {
        return draft;
    }

    public void setDraft(Integer draft) {
        this.draft = draft;
    }

    public Integer getIsdel() {
        return isdel;
    }

    public void setIsdel(Integer isdel) {
        this.isdel = isdel;
    }

    public Integer getFavourite_num() {
        return favourite_num;
    }

    public void setFavourite_num(Integer favourite_num) {
        this.favourite_num = favourite_num;
    }

    public Integer getBasic_favourite_num() {
        return basic_favourite_num;
    }

    public void setBasic_favourite_num(Integer basic_favourite_num) {
        this.basic_favourite_num = basic_favourite_num;
    }

    public Integer getVisit_num() {
        return visit_num;
    }

    public void setVisit_num(Integer visit_num) {
        this.visit_num = visit_num;
    }

    public Integer getBasic_visit_num() {
        return basic_visit_num;
    }

    public void setBasic_visit_num(Integer basic_visit_num) {
        this.basic_visit_num = basic_visit_num;
    }

    public Integer getDownload_num() {
        return download_num;
    }

    public void setDownload_num(Integer download_num) {
        this.download_num = download_num;
    }

    public Integer getBasic_download_num() {
        return basic_download_num;
    }

    public void setBasic_download_num(Integer basic_download_num) {
        this.basic_download_num = basic_download_num;
    }

    @Override
    public String toString() {
        return "Material{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category1_id=" + category1_id +
                ", category2_id=" + category2_id +
                ", category3_id=" + category3_id +
                ", category4_id=" + category4_id +
                ", category1_name='" + category1_name + '\'' +
                ", category2_name='" + category2_name + '\'' +
                ", category3_name='" + category3_name + '\'' +
                ", category4_name='" + category4_name + '\'' +
                ", user_id='" + user_id + '\'' +
                ", copyright_id=" + copyright_id +
                ", copyright=" + copyright +
                ", show_image='" + show_image + '\'' +
                ", details_image='" + details_image + '\'' +
                ", status=" + status +
                ", filetype='" + filetype + '\'' +
                ", downloadurl='" + downloadurl + '\'' +
                ", price=" + price +
                ", create_time=" + create_time +
                ", updata_time=" + updata_time +
                ", description='" + description + '\'' +
                ", musage='" + musage + '\'' +
                ", tags='" + tags + '\'' +
                ", tagList=" + tagList +
                ", update_admin='" + update_admin + '\'' +
                ", source_file='" + source_file + '\'' +
                ", source_type='" + source_type + '\'' +
                ", source_size='" + source_size + '\'' +
                ", source_dpi='" + source_dpi + '\'' +
                ", draft=" + draft +
                ", isdel=" + isdel +
                ", favourite_num=" + favourite_num +
                ", basic_favourite_num=" + basic_favourite_num +
                ", visit_num=" + visit_num +
                ", basic_visit_num=" + basic_visit_num +
                ", download_num=" + download_num +
                ", basic_download_num=" + basic_download_num +
                ", user='" + user + '\'' +
                '}';
    }
}
