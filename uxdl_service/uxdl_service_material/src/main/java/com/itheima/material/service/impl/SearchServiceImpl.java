package com.itheima.material.service.impl;

import com.alibaba.fastjson.JSON;
import com.itheima.material.dao.CategoryMapper;
import com.itheima.material.dao.KeywordMapper;
import com.itheima.material.pojo.*;
import com.itheima.material.service.MaterialService;
import com.itheima.material.service.NumInfoService;
import com.itheima.material.service.SearchService;
import com.itheima.user.feign.UsercenterFeign;
import com.itheima.uxdl.util.DateUtils;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeAction;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeRequestBuilder;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    @Autowired
    private UsercenterFeign usercenterFeign;

    @Autowired
    private KeywordMapper keywordMapper;

    /**
     * 条件查询（素材名称，分类id）聚合查询（文件格式）
     *
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> searchMaterial(Map<String, String> searchMap) {
        //结果集合
        Map<String, Object> resultMap = new HashMap();

        if (null != searchMap) {
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();


            String categoryId = searchMap.get("category_id");
            if (!StringUtils.isEmpty(categoryId)) {
                Category category = categoryMapper.selectByPrimaryKey(categoryId);
                //按照分类id精确查询
                if (!categoryId.equals("0")) {
                    if (category.getLevel() == 1) {
                        TermQueryBuilder termQuery = QueryBuilders.termQuery("category1_id", categoryId);
                        boolQuery.must(termQuery);
                    } else if (category.getLevel() == 2) {
                        TermQueryBuilder termQuery = QueryBuilders.termQuery("category2_id", categoryId);
                        boolQuery.must(termQuery);

                    } else if (category.getLevel() == 3) {
                        TermQueryBuilder termQuery = QueryBuilders.termQuery("category3_id", categoryId);
                        boolQuery.must(termQuery);
                    } else {
                        TermQueryBuilder termQuery = QueryBuilders.termQuery("category4_id", categoryId);
                        boolQuery.must(termQuery);
                    }
                }
            }

            //按照搜索条件匹配查询

            if (null != searchMap.get("name") && !"".equals(searchMap.get("name"))) {
                MultiMatchQueryBuilder multiMatchQuery = QueryBuilders.multiMatchQuery(searchMap.get("name"), "name", "category1_name", "category2_name", "category3_name", "category4_name");
                boolQuery.filter(multiMatchQuery);
                List<String> terms = getIkAnalyzeSearchTerms(searchMap.get("name"));
                ValueOperations valueOperations = redisTemplate.opsForValue();
                BoundHashOperations keywordHash = redisTemplate.boundHashOps("keyword");
                for (String term : terms) {
                    valueOperations.setIfAbsent(term,0,31,TimeUnit.DAYS);
                    Long increment = valueOperations.increment(term, 1);
                    if (!keywordHash.putIfAbsent(term,1)) {
                        keywordHash.put(term,increment);
                    }
                }

            }

            if (null != searchMap.get("file_type") && !"".equals(searchMap.get("file_type"))) {
                int i = 0;
                if ("1".equals(searchMap.get("file_type"))) {
                    TermQueryBuilder termQuery = QueryBuilders.termQuery("source_type", "jpg");
                    TermQueryBuilder termQuery2 = QueryBuilders.termQuery("source_type", "png");
                    TermQueryBuilder termQuery3 = QueryBuilders.termQuery("source_type", "jpeg");
                    boolQuery.filter(termQuery);
                    boolQuery.filter(termQuery2);
                    boolQuery.filter(termQuery3);
                    i=1+1;
                }
                if ("2".equals(searchMap.get("file_type"))) {
                    TermQueryBuilder termQuery = QueryBuilders.termQuery("source_type", "PSD");
                    boolQuery.filter(termQuery);
                    i=1+1;
                }
                if ("3".equals(searchMap.get("file_type"))) {
                    TermQueryBuilder termQuery = QueryBuilders.termQuery("source_type","AI" );
                    boolQuery.filter(termQuery);
                    i=1+1;
                }
                if ("0".equals(searchMap.get("file_type"))) {
                    i=1+1;
                }
                if (i==0){
                    TermQueryBuilder termQuery = QueryBuilders.termQuery("source_type",searchMap.get("file_type") );
                    boolQuery.filter(termQuery);
                }


            }

            //原生搜索实现类
            NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
            nativeSearchQueryBuilder.withQuery(boolQuery);

            //排序（时间，下载量，访问量（默认按照上传时间排序））
            if (null == searchMap.get("sortField") || "".equals(searchMap.get("sortField"))) {
                nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("create_time").order(SortOrder.ASC));
            } else {
                nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(searchMap.get("sortField")).order(SortOrder.DESC));
            }

            //文件格式聚合查询
            String filetype = "file_type";
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(filetype).field("source_type"));

            //分页
            String pageNum = searchMap.get("pageNum");
            String pageSize = searchMap.get("pageSize");
            if (null == pageNum) {
                pageNum = "1";
            }
            if (null == pageSize) {
                pageSize = "30";
            } else if (Integer.parseInt(pageSize) > 50) {
                pageSize = "50";
            }

            nativeSearchQueryBuilder.withPageable(PageRequest.of(Integer.parseInt(pageNum) - 1, Integer.parseInt(pageSize)));

            AggregatedPage<MaterialInfo> aggregatedPage = elasticsearchTemplate.queryForPage(nativeSearchQueryBuilder.build(), MaterialInfo.class, new SearchResultMapper() {
                @Override
                public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {

                    List<T> list = new ArrayList<>();

                    SearchHits hits = searchResponse.getHits();
                    if (null != hits) {
                        for (SearchHit hit : hits) {
                            MaterialInfo materialInfo = JSON.parseObject(hit.getSourceAsString(), MaterialInfo.class);
                            list.add((T) materialInfo);
                        }
                    }
                    return new AggregatedPageImpl<T>(list, pageable, hits.getTotalHits(), searchResponse.getAggregations());
                }
            });
            try {
                List<Material> favouriteList = (List<Material>) usercenterFeign.getFavorites().getData();
                //返回用户收藏列表
                resultMap.put("favouriteList", favouriteList);
            } catch (Exception e) {
                //总条数
                resultMap.put("total", aggregatedPage.getTotalElements());
                //总页数
                resultMap.put("totalPage", aggregatedPage.getTotalPages());
                //查询结果集合
                resultMap.put("rows", aggregatedPage.getContent());
                //获取规格聚合结果
                StringTerms filetypeTerms = (StringTerms) aggregatedPage.getAggregation(filetype);
                List<String> filetypeList = filetypeTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
                resultMap.put("filetype", filetypeList);
                //返回当前页
                resultMap.put("pageNum", pageNum);

                return resultMap;
            }
            //总条数
            resultMap.put("total", aggregatedPage.getTotalElements());
            //总页数
            resultMap.put("totalPage", aggregatedPage.getTotalPages());
            //查询结果集合
            resultMap.put("rows", aggregatedPage.getContent());
            //获取规格聚合结果
            StringTerms filetypeTerms = (StringTerms) aggregatedPage.getAggregation(filetype);
            List<String> filetypeList = filetypeTerms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
            resultMap.put("filetype", filetypeList);
            //返回当前页
            resultMap.put("pageNum", pageNum);

            return resultMap;
        }

        return null;
    }


    /**
     * 调用 ES 获取 IK 分词后结果
     *
     * @param searchContent
     * @return
     */

    private List<String> getIkAnalyzeSearchTerms(String searchContent) {

// 调用 IK 分词分词

        AnalyzeRequestBuilder ikRequest = new AnalyzeRequestBuilder(elasticsearchTemplate.getClient(),

                AnalyzeAction.INSTANCE, "materialinfo", searchContent);

        ikRequest.setTokenizer("ik_smart");

        List<AnalyzeResponse.AnalyzeToken> ikTokenList = ikRequest.execute().actionGet().getTokens();

// 循环赋值

        List<String> searchTermList = new ArrayList<>();


        ikTokenList.forEach(ikToken -> {
            searchTermList.add(ikToken.getTerm());
        });

        return searchTermList;

    }

}
