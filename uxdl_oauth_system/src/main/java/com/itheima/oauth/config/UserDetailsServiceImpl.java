package com.itheima.oauth.config;


import com.itheima.oauth.dao.AdminMapper;
import com.itheima.oauth.util.UserJwt;
import com.itheima.system.pojo.SysAdmin;
import com.itheima.user.pojo.TbUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/*****
 * 自定义授权认证类
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    ClientDetailsService clientDetailsService;

    /*@Autowired
    private UserFeign userFeign;*/
    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /****
     * 自定义授权认证
     * @param adminname
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String adminname) throws UsernameNotFoundException {

        //取出身份，如果身份为空说明没有认证
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //没有认证统一采用httpbasic认证，httpbasic中存储了client_id和client_secret，开始认证client_id和client_secret
        if (authentication == null) {
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(adminname);
            if (clientDetails != null) {
                //秘钥
                String clientSecret = clientDetails.getClientSecret();
                //静态方式
                //return new User(username,new BCryptPasswordEncoder().encode(clientSecret), AuthorityUtils.commaSeparatedStringToAuthorityList(""));
                //数据库查找方式
                return new User(adminname, clientSecret, AuthorityUtils.commaSeparatedStringToAuthorityList(""));
            }
        }

        if (StringUtils.isEmpty(adminname)) {
            return null;
        }
        //创建User对象
        /*TbUser user = userFeign.findByPhone(name);*/
        Example example = new Example(SysAdmin.class);
        example.createCriteria().andEqualTo("username",adminname).andEqualTo("status",1);
        SysAdmin sysAdmin = adminMapper.selectOneByExample(example);
        //根据用户名查询用户信息
        //String pwd = new BCryptPasswordEncoder().encode("22222");
        //设置令牌权限信息
        String permissions = "page,back";
        UserJwt userDetails = new UserJwt(adminname, sysAdmin.getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList(permissions));
        //UserJwt userDetails = new UserJwt(phone,pwd,AuthorityUtils.commaSeparatedStringToAuthorityList(permissions));
        return userDetails;


    }


}
