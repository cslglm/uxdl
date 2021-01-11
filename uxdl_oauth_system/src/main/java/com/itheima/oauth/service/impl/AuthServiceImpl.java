package com.itheima.oauth.service.impl;


import com.itheima.oauth.dao.AdminMapper;
import com.itheima.oauth.service.AuthService;
import com.itheima.oauth.util.AuthToken;
import com.itheima.system.pojo.SysAdmin;
import com.itheima.user.pojo.TbUser;
import com.itheima.uxdl.entity.Result;
import com.itheima.uxdl.entity.StatusCode;
import com.itheima.uxdl.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 登录验证
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${auth.ttl}")
    private Long ttl;


    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private IdWorker idWorker;


    @Override
    public AuthToken login(String adminname, String password, String clientId, String clientSecret) {
        SysAdmin sysAdmin =null;
        if (!StringUtils.isEmpty(adminname)){
            //tbUser = userFeign.findByPhone(phone);
            //SysAdmin admin = new SysAdmin();
            //admin.setUsername(adminname);
            //sysAdmin = adminMapper.selectOne(admin);
            Example example = new Example(SysAdmin.class);
            example.createCriteria().andEqualTo("username",adminname);
            sysAdmin = adminMapper.selectOneByExample(example);
        }
        //1、获取令牌
        ServiceInstance serviceInstance = loadBalancerClient.choose("system-auth");

        URI uri = serviceInstance.getUri();

        String url = uri + "/oauth/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type","password");
        body.add("username",adminname);
        body.add("password",password);
        if (sysAdmin != null){
            body.add("id",String.valueOf(sysAdmin.getId()));
            body.add("realname",sysAdmin.getRealname());
            body.add("birthday",String.valueOf(sysAdmin.getBirthday()));
            body.add("phone",sysAdmin.getPhone());
            body.add("sex",sysAdmin.getSex());
        }


        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization",this.setAuthorization(clientId,clientSecret));

        HttpEntity<MultiValueMap<String,String>> httpEntity = new HttpEntity<>(body,headers);


        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401){
                    super.handleError(response);
                }
            }
        });


        ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);

        //2、封装相关数据
        Map map = exchange.getBody();
        //判断结果
        if (map == null || map.get("access_token") == null || map.get("refresh_token") == null || map.get("jti") == null){
            throw new RuntimeException("令牌获取失败");
        }
        AuthToken authToken = new AuthToken();
        authToken.setAccessToken((String) map.get("access_token"));
        authToken.setRefreshToken((String) map.get("refresh_token"));
        authToken.setJti((String) map.get("jti"));

        //3、将数据存入Redis，key为短令牌，值为令牌
        stringRedisTemplate.boundValueOps(authToken.getJti()).set(authToken.getAccessToken(),ttl, TimeUnit.SECONDS);

        return authToken;
    }

    /**
     * 设置Authorization
     * @param clientId
     * @param clientSecret
     * @return
     */
    private String setAuthorization(String clientId, String clientSecret) {

        String value = clientId + ":" + clientSecret;
        byte[] encode = Base64Utils.encode(value.getBytes());

        return "Basic " + new String(encode);
    }

    /**
     * 修改登录密码
     * @param phone
     * @param password
     * @return
     */
    @Override
    public Result updatePassword(String phone, String password) {
        Example example = new Example(SysAdmin.class);
        example.createCriteria().andEqualTo("phone",phone);
        SysAdmin admin = adminMapper.selectOneByExample(example);

        if (admin == null){
            return new Result(false, StatusCode.ERROR,"手机号不存在");
        }
        String pwd = new BCryptPasswordEncoder().encode(password);
        admin.setPassword(pwd);
        adminMapper.updateByPrimaryKey(admin);
        return new Result(true,StatusCode.OK,"密码修改成功");
    }
}
