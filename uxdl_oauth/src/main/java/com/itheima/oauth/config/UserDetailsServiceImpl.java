package com.itheima.oauth.config;


import com.itheima.oauth.dao.UserMapper;
import com.itheima.oauth.util.UserJwt;
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
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /****
     * 自定义授权认证
     * @param phone
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        System.out.println(phone);
        String[] strs = phone.split("-");
        String name = strs[0];

        //取出身份，如果身份为空说明没有认证
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //没有认证统一采用httpbasic认证，httpbasic中存储了client_id和client_secret，开始认证client_id和client_secret
        if (authentication == null) {
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(name);
            if (clientDetails != null) {
                //秘钥
                String clientSecret = clientDetails.getClientSecret();
                //静态方式
                //return new User(username,new BCryptPasswordEncoder().encode(clientSecret), AuthorityUtils.commaSeparatedStringToAuthorityList(""));
                //数据库查找方式
                return new User(name, clientSecret, AuthorityUtils.commaSeparatedStringToAuthorityList(""));
            }
        }

        //判断登陆方式

        if (strs[1].equals("0")) {
            String pwd = (String) redisTemplate.boundValueOps("BCCode" + name).get();
            String permissions = "page,back";
            UserJwt userDetails = new UserJwt(name, pwd, AuthorityUtils.commaSeparatedStringToAuthorityList(permissions));
            redisTemplate.delete("BCCode" + name);
            return userDetails;

        }


        if (StringUtils.isEmpty(name)) {
            return null;
        }
        Example example = new Example(TbUser.class);
        example.createCriteria().andEqualTo("phone",name).andEqualTo("status",1);
        TbUser tbUser = userMapper.selectOneByExample(example);
        //根据用户名查询用户信息
        //String pwd = new BCryptPasswordEncoder().encode("22222");
        //设置令牌权限信息
        String permissions = "page,back";
        UserJwt userDetails = new UserJwt(name, tbUser.getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList(permissions));
        //UserJwt userDetails = new UserJwt(phone,pwd,AuthorityUtils.commaSeparatedStringToAuthorityList(permissions));
        return userDetails;


    }


}
