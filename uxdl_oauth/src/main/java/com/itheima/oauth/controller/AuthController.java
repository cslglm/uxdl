package com.itheima.oauth.controller;


import com.aliyuncs.exceptions.ClientException;
import com.itheima.oauth.service.AuthService;
import com.itheima.oauth.util.AuthToken;
import com.itheima.oauth.util.CookieUtil;
import com.itheima.uxdl.entity.Result;
import com.itheima.uxdl.entity.StatusCode;
import com.itheima.uxdl.util.SMSUtils;
import com.itheima.uxdl.util.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * 用户登录
 */
@Controller
@RequestMapping("/oauth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

   /* @Autowired
    private UserFeign userFeign;*/



    private static final String RCODE = "register:";

    @Value("${auth.clientId}")
    private String clientId;

    @Value("${auth.clientSecret}")
    private String clientSecret;

    @Value("${auth.cookieDomain}")
    private String cookieDomain;

    @Value("${auth.cookieMaxAge}")
    private int cookieMaxAge;

    @RequestMapping("/toLogin")//required = false 访问时不一定携带参数
    public String toLogin(@RequestParam(value = "FROM",required = false,defaultValue = "") String from, Model model){
        model.addAttribute("from",from);
        return "login";
    }

    /**
     * 手机号密码登录
     * @param phone
     * @param password
     * @param response
     * @return
     */
    @ResponseBody
    @PostMapping("/login")
    public Result login(String phone , String password, HttpServletResponse response){
        //1、判断用户名密码是否为空
        if (StringUtils.isEmpty(phone)){
            throw new RuntimeException("用户名不能为空");
        }
        if (StringUtils.isEmpty(password)){
            throw new RuntimeException("密码不能为空");
        }
        //设置登陆方式：phone为手机号密码
        String index = "-1";
        //2、获取令牌
        AuthToken authToken = authService.login(phone, password,index, clientId, clientSecret);
        //3、将短令牌保存到cookie
        this.svaeJtiToCookie(authToken.getJti(),response);
        //4、返回结果
        return new Result(true, StatusCode.OK,"登陆成功",authToken.getJti());
    }

    /**
     * 手机号验证码码登录
     * @param phone
     * @param code
     * @param response
     * @return
     */
    @ResponseBody
    @PostMapping("/login/phone")
    public Result loginCheck(String phone , String code, HttpServletResponse response){
        //1、判断用户名密码是否为空
        if (StringUtils.isEmpty(phone)){
            throw new RuntimeException("用户名不能为空");
        }
        if (StringUtils.isEmpty(code)){
            throw new RuntimeException("密码不能为空");
        }

        String resultCode = (String) redisTemplate.boundValueOps(RCODE + phone).get();
        if (resultCode == null){
            return new Result(false,StatusCode.ERROR,"手机号输入不一致");
        }
        if (!resultCode.equals(code)){
            return new Result(false,StatusCode.ERROR,"验证码输入错误");
        }
        //将验证码加密存入redis
        String encode = new BCryptPasswordEncoder().encode(resultCode);
        redisTemplate.boundValueOps("BCCode" + phone).set(encode,5, TimeUnit.MINUTES);

        //TbUser tbUser = userFeign.findByPhone(phone);
        //设置登陆方式，code为手机号验证码
        String index = "-0";

        //2、获取令牌
        AuthToken authToken = authService.login(phone,resultCode,index, clientId, clientSecret);
        //3、将短令牌保存到cookie
        this.svaeJtiToCookie(authToken.getJti(),response);

        redisTemplate.delete(RCODE + phone);
        //4、返回结果
        return new Result(true, StatusCode.OK,"登陆成功",authToken.getJti());
    }

    /**
     * 获取验证码
     * @param phone
     * @return
     */
    @ResponseBody
    @GetMapping("/msm/{phone}")
    public Result getCode(@PathVariable("phone") String phone) {
        try {
            String code4String = ValidateCodeUtils.generateValidateCode4String(6);
            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,phone,code4String);
            redisTemplate.boundValueOps(RCODE+phone).set(code4String,300, TimeUnit.SECONDS);
            return new Result(true,StatusCode.OK,"发送成功");
        } catch (ClientException e) {
           e.printStackTrace();
           return new Result(false,StatusCode.ERROR,"发送失败");
       }
    }

    /**
     * 用户注册
     * @return
     */
    @ResponseBody
    @PostMapping("/register")
    public Result register(String phone,String code,String password){
        String result = (String) redisTemplate.boundValueOps(RCODE + phone).get();
        if (result == null){
            return new Result(false,StatusCode.ERROR,"手机号输入不一致");
        }
        if (!result.equals(code)){
            return new Result(false,StatusCode.ERROR,"验证码输入错误");
        }
        if (StringUtils.isEmpty(password)){
            return new Result(false,StatusCode.ERROR,"密码未输入");
        }
        Result resultDate = authService.register(phone, password);

        if (resultDate.getCode() == 0){
            redisTemplate.delete(RCODE + phone);
        }
        return resultDate;

    }

    /**
     * 修改密码
     * @param phone
     * @param code
     * @param password
     * @return
     */
    @ResponseBody
    @PatchMapping("/pwd")
    public Result updatePassword(String phone,String code,String password,HttpServletResponse response){
        String result = (String) redisTemplate.boundValueOps(RCODE + phone).get();
        if (result == null){
            return new Result(false,StatusCode.ERROR,"手机号输入不一致");
        }
        if (!result.equals(code)){
            return new Result(false,StatusCode.ERROR,"验证码输入错误");
        }
        Result resultDate = authService.updatePassword(phone, password);
        redisTemplate.delete(RCODE + phone);
        //如果成功，调用登陆方法
        if (resultDate.getCode() == 0){
            AuthToken authToken = authService.login(phone, password, "-1", clientId, clientSecret);
            this.svaeJtiToCookie(authToken.getJti(),response);
        }
        return resultDate;
    }

    /**
     * 退出登录
     * @param request
     * @return
     */
    @ResponseBody
    @GetMapping("/logout")
    public Result logout(HttpServletRequest request){
        String jti = request.getHeader("uid");
//        String redisKey = CookieUtil.readCookie(request, "uid").get("uid");
//        CookieUtil.readCookie(request).remove("uid");
//        System.out.println(redisKey);
        stringRedisTemplate.delete(jti);
        return new Result(true,StatusCode.OK,"退出成功");
    }

    /**
     * 将短令牌保存到cookie
     * @param jti
     * @param response
     */
    private void svaeJtiToCookie(String jti, HttpServletResponse response) {
        response.addHeader("uid",jti);
        CookieUtil.addCookie(response,cookieDomain,"/","uid",jti,cookieMaxAge,false);
    }
}
