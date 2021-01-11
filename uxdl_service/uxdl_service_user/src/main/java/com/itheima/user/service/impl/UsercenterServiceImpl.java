package com.itheima.user.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.material.feign.NumInfoFeign;
import com.itheima.material.feign.SearchFeign;
import com.itheima.material.pojo.*;
import com.itheima.user.dao.*;
import com.itheima.user.service.UsercenterService;
import com.itheima.uxdl.entity.PageResult;

import com.itheima.uxdl.entity.RedisConstant;
import com.itheima.uxdl.entity.Result;
import com.itheima.uxdl.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;


import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UsercenterServiceImpl implements UsercenterService {

    @Autowired
    private MaterialMapper materialMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private CopyrightMapper copyrightMapper;

    @Autowired
    private FavouriteMapper favouriteMapper;

    @Autowired
    private NumInfoFeign numInfoFeign;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SearchFeign searchFeign;

    @Autowired
    private DownloadHistoryMapper downloadHistoryMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 查询个人作品
     *
     * @param pageNum
     * @param pageSize
     * @param sort
     * @param category_id
     * @return
     */
    @Override
    public PageResult findPersonalWorks(String id, String pageNum, String pageSize, String sort, String category_id) {
        if (StringUtils.isEmpty(id)) {
            throw new RuntimeException("未登录");
        }
        if (StringUtils.isEmpty(pageNum)) {
            pageNum = "1";
        }
        if (StringUtils.isEmpty(pageSize)) {
            pageSize = "20";
        }

        Example example = new Example(Material.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("user_id", id).andEqualTo("isdel", 0);
        if (null != category_id) {
            Category category = categoryMapper.selectByPrimaryKey(category_id);
            if (null != category) {
                Integer level = category.getLevel();
                criteria.andEqualTo("category" + level + "_id", category_id);
            }
        }

        if (!StringUtils.isEmpty(sort)) {
            example.orderBy(sort).desc();
        } else {
            example.orderBy("updata_time").desc();
        }
        PageHelper.startPage(Integer.parseInt(pageNum), Integer.parseInt(pageSize));
        Page<Material> pageInfo = (Page<Material>) materialMapper.selectByExample(example);

        return new PageResult(pageInfo.getTotal(), pageInfo.getResult());
    }

    /**
     * 根据id查询作品
     *
     * @param id
     * @return
     */
    @Override
    public Material findMaterialById(String id) {
        Material material = materialMapper.selectByPrimaryKey(id);
        if (null == material) {
            throw new RuntimeException("素材不存在");
        }
        material.setCopyright(copyrightMapper.selectByPrimaryKey(material.getCopyright_id()));
        if (!StringUtils.isEmpty(material.getTags())) {
            String[] ids = material.getTags().split(",");
            List<Tag> tagList = new ArrayList<>();
            for (String tid : ids) {
                Tag tag = tagMapper.selectByPrimaryKey(tid);
                tagList.add(tag);
            }
            material.setTagList(tagList);
        }
        return material;
    }

    /**
     * 删除作品（逻辑删除）
     *
     * @param id
     */
    @Transactional
    @Override
    public void delById(String id) {
        Material material = new Material();
        material.setId(id);
        material.setIsdel(1);
        materialMapper.updateByPrimaryKeySelective(material);
        numInfoFeign.delete(id);
        redisTemplate.delete("Details" + id);
    }

    /**
     * 获取用户个人收藏
     *
     * @return
     */

    @Override
    public Map getFavorites(String id, String category_id,String pageNum,String pageSize) {
        Integer num = null ;
        Integer size = null ;
        if (StringUtils.isEmpty(pageNum)){
            num = 1;
        }else{
            num = Integer.parseInt(pageNum);
        }

        if (StringUtils.isEmpty(pageSize)){
            size = 10;
        }else {
            size = Integer.parseInt(pageSize);
            if (size>50) {
                size =50;
            }
        }
        int start = (num - 1) * size;
        Set<Integer> idSet = new HashSet<>();
        Map resultMap = new HashMap();
        if (category_id !=null ) {
            Set<Integer> categoryIds = this.getCategoryIds(Integer.parseInt(category_id), idSet);
            categoryIds.add(Integer.parseInt(category_id));
            String categorySetStr = categoryIds.toString();
            String s1 = categorySetStr.replace("[", "(");
            String categoryIdsStr = s1.replace("]", ")");
            PageHelper.clearPage();
            List<Material> favourites = materialMapper.getFavouritesByCategoryId(id, categoryIdsStr,start,size);
            Integer total = materialMapper.getFavouritesCountByCategoryId(id, categoryIdsStr);
            resultMap.put("total",total);
            resultMap.put("favourites",favourites);

        }else{
            List<Material> favourites = materialMapper.getFavourites(id, start, size);
            Integer total = materialMapper.getFavouritesCount(id);
            resultMap.put("total",total);
            resultMap.put("favourites",favourites);
        }
        return resultMap;
    }

    public Set<Integer> getCategoryIds(Integer parent_id,Set<Integer> idSet){
        List<Integer> ids = categoryMapper.getIds(parent_id);
        idSet.addAll(ids);
        if (ids.size()==0) {
            return idSet;
        }else {
            for (Integer id : ids) {
                idSet.add(id);
                this.getCategoryIds(id,idSet);
            }
        }
        return idSet;
    }

    /**
     * 取消收藏
     *
     * @param uid
     * @param id
     */
    @Transactional
    @Override
    public void delFavorites(String uid, String id) {
        Example example = new Example(Favourite.class);
        example.createCriteria().andEqualTo("material_id", id).andEqualTo("user_id", uid);
        favouriteMapper.deleteByExample(example);
        numInfoFeign.updateCollectionNum(id, 0);
    }

    /**
     * 添加收藏
     *
     * @param uid
     * @param id
     */
    @Transactional
    @Override
    public void addFavorites(String uid, String id) {
        Example example = new Example(Favourite.class);
        example.createCriteria().andEqualTo("material_id", id).andEqualTo("user_id", uid);
        Favourite favourite1 = favouriteMapper.selectOneByExample(example);
        if (favourite1 != null) {
            return;
        }
        Favourite favourite = new Favourite();
        Material material = materialMapper.selectByPrimaryKey(id);
        Category category = categoryMapper.selectByPrimaryKey(material.getCategory1_id());
        if (null != material.getCategory2_id()) {
            favourite.setCategory_id(material.getCategory2_id());
        } else {
            favourite.setCategory_id(material.getCategory1_id());
        }
        favourite.setCategory(category.getName());
        favourite.setCollect_time(new Date());
        favourite.setUser_id(Long.parseLong(uid));
        favourite.setMaterial_id(id);
        favouriteMapper.insertSelective(favourite);
        numInfoFeign.updateCollectionNum(id, 1);
    }

    /**
     * 获取用户收藏Id
     *
     * @param id
     * @return
     */
    @Override
    public List<Integer> getFavoritesId(String id) {
        List<Integer> ids = new ArrayList<>();
        Example example = new Example(Favourite.class);
        example.createCriteria().andEqualTo("user_id", id);
        List<Favourite> favourites = favouriteMapper.selectByExample(example);
        for (Favourite favourite : favourites) {
            ids.add(Integer.parseInt(favourite.getMaterial_id()));
        }
        return ids;
    }

    /**
     * 编辑作品信息
     *
     * @param id
     * @param material
     * @return
     */
    @Transactional
    @Override
    public Result editMaterial(String id, Material material) {
        if (null == id || "".equals(id)) {
            return new Result(false, StatusCode.ERROR, "作品id不能为空");
        }

        material.setId(id);
        material.setUpdata_time(new Date());
        Material material2 = addCategoryName(material);
        materialMapper.updateByPrimaryKeySelective(addCategoryName(material2));
        Material material1 = materialMapper.selectByPrimaryKey(material.getId());
        searchFeign.delete(material.getId());
        searchFeign.add(material1);
        redisTemplate.delete("Details" + material.getId());
        return new Result(true, StatusCode.OK, "编辑作品成功");
    }

    /**
     * 添加素材的分类名称
     *
     * @param material
     * @return
     */
    public Material addCategoryName(Material material) {
        if (null != material.getCategory1_id()) {
            String name = categoryMapper.selectByPrimaryKey(material.getCategory1_id()).getName();
            material.setCategory1_name(name);
        }
        if (null != material.getCategory2_id()) {
            String name = categoryMapper.selectByPrimaryKey(material.getCategory2_id()).getName();
            material.setCategory2_name(name);
        }
        if (null != material.getCategory3_id()) {
            String name = categoryMapper.selectByPrimaryKey(material.getCategory3_id()).getName();
            material.setCategory3_name(name);
        }
        if (null != material.getCategory4_id()) {
            String name = categoryMapper.selectByPrimaryKey(material.getCategory4_id()).getName();
            material.setCategory4_name(name);
        }

        return material;

    }

    /**
     * 新增作品（上传压缩包）信息
     *
     * @param material
     */
    @Transactional
    @Override
    public Result addMaterial(Material material) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String format = simpleDateFormat.format(new Date());
        String ms = String.valueOf(System.currentTimeMillis());
        String id = "SC" + format + ms.substring(11);
        String user_id = material.getUser_id();
        material.setId(id);
        material.setCreate_time(new Date());
        material.setUpdata_time(new Date());
        material.setIsdel(0);
        material.setStatus(1);
        if (null == material.getDraft()) {
            material.setDraft(0);
        }
        Material material1 = addCategoryName(material);
        materialMapper.insert(material1);
        SetOperations set = redisTemplate.opsForSet();
        set.add(RedisConstant.MATE_PIC_DB_RESOURCES, material.getName());
        //更新ES
        searchFeign.add(material1);
        //更新Mongo
        numInfoFeign.add(id);
        return new Result(true, StatusCode.OK, "上传作品信息成功", material);
    }

    /**
     * 添加下载记录
     *
     * @param userId
     * @param materialId
     */
    @Override
    public void addDownloadHistory(Long userId, String materialId) {
        Example example = new Example(DownloadHistory.class);
        example.createCriteria().andEqualTo("user_id", userId).andEqualTo("material_id", materialId);
        DownloadHistory downloadHistory = downloadHistoryMapper.selectOneByExample(example);
        if (downloadHistory == null) {
            downloadHistory = new DownloadHistory();
            Material material = materialMapper.selectByPrimaryKey(materialId);
            Category category = categoryMapper.selectByPrimaryKey(material.getCategory1_id());
            if (null != material.getCategory2_id()) {
                downloadHistory.setCategory_id(material.getCategory2_id());
            } else {
                downloadHistory.setCategory_id(material.getCategory1_id());
            }
            downloadHistory.setMaterial_id(materialId);
            downloadHistory.setUser_id(userId);
            downloadHistory.setCategory(category.getName());
            downloadHistory.setDownload_time(new Date());
            downloadHistoryMapper.insert(downloadHistory);
            numInfoFeign.updateDownloadNum(materialId);
        }
    }

    /**
     * 根据用户id查找下载记录
     *
     * @param userId
     * @param category_id
     * @return
     */
    @Override
    public Map findDownloadHistory(Long userId, String category_id,String pageNum,String pageSize) {
        Integer num = null ;
        Integer size = null ;
        if (StringUtils.isEmpty(pageNum)){
            num = 1;
        }else{
            num = Integer.parseInt(pageNum);
        }

        if (StringUtils.isEmpty(pageSize)){
            size = 10;
        }else {
            size = Integer.parseInt(pageSize);
            if (size>50) {
                size =50;
            }
        }
        int start = (num - 1) * size;
        Set<Integer> idSet = new HashSet<>();
        Map resultMap = new HashMap();
        if (category_id !=null ) {
            Set<Integer> categoryIds = this.getCategoryIds(Integer.parseInt(category_id), idSet);
            categoryIds.add(Integer.parseInt(category_id));
            String categorySetStr = categoryIds.toString();
            String s1 = categorySetStr.replace("[", "(");
            String categoryIdsStr = s1.replace("]", ")");
            PageHelper.clearPage();
            List<Material> downloadHistory = downloadHistoryMapper.getDownloadHistoryByCategoryId(userId, categoryIdsStr,start,size);
            Integer total = downloadHistoryMapper.getDownloadHistoryCountByCategoryId(userId, categoryIdsStr);
            resultMap.put("total",total);
            resultMap.put("downloadHistory",downloadHistory);
        }else{
            List<Material> downloadHistory = downloadHistoryMapper.getDownloadHistory(userId, start, size);
            Integer total = downloadHistoryMapper.getDownloadHistoryCount(userId);
            resultMap.put("total",total);
            resultMap.put("downloadHistory",downloadHistory);
        }
        return resultMap;
    }

    /**
     * 删除下载历史记录
     *
     * @param id
     */
    @Transactional
    @Override
    public void deleteDownloadHistory(String id) {
        Example example = new Example(DownloadHistory.class);
        example.createCriteria().andEqualTo("material_id", id);
        downloadHistoryMapper.deleteByExample(example);
    }
}
