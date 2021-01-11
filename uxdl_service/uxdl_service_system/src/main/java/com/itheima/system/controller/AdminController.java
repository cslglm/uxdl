package com.itheima.system.controller;

import com.itheima.system.pojo.SysAdmin;
import com.itheima.system.service.AdminService;
import com.itheima.uxdl.entity.PageResult;
import com.itheima.uxdl.entity.Result;
import com.itheima.uxdl.entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员相关业务
 */
@RestController
@RequestMapping("/admins")
public class AdminController {

    @Autowired
    private AdminService adminService;


    /**
     * 条件查询
     * @param searchMap
     * @return
     */
    @GetMapping
    public Result findByCondition(@RequestParam Map searchMap){
        PageResult result = adminService.findByCondition(searchMap);
        return new Result(true,StatusCode.OK,"查询成功",result);
    }

    /**
     * 管理员状态切换
     * @param id
     * @return
     */
    @PatchMapping("/{id}")
    public Result updateStatus(@PathVariable("id") String id){
        adminService.updateStatus(id);
        return new Result(true,StatusCode.OK,"状态切换成功");
    }

    /**
     * 新建管理员
     * @param username
     * @param password
     * @param phone
     * @param status
     * @param sex
     * @return
     */
    @PostMapping
    public Result addAdmin(String username,String password,String phone,String status,String sex){
        SysAdmin sysAdmin = new SysAdmin();
        sysAdmin.setUsername(username);
        sysAdmin.setPassword(password);
        sysAdmin.setPhone(phone);
        sysAdmin.setStatus(Integer.parseInt(status));
        sysAdmin.setSex(sex);
        adminService.addAdmin(sysAdmin);
        return new Result(true,StatusCode.OK,"创建成功");
    }

    /**
     * 编辑管理员数据回显（根据id查询管理员）
     * @param id
     * @return
     */
    @GetMapping("/get/{id}")
    public Result findById(@PathVariable("id") String id){
        SysAdmin sysAdmin = adminService.findById(id);
        sysAdmin.setPassword("******");
        return new Result(true,StatusCode.OK,"查询成功",sysAdmin);
    }

    /**
     * 编辑管理员
     * @param username
     * @param password
     * @param phone
     * @param status
     * @param sex
     * @return
     */
    @PutMapping("/{id}")
    public Result update(@PathVariable("id")String id, String username,String password,String phone,String status,String sex){
        SysAdmin sysAdmin = new SysAdmin();
        if (id != null && !id.equals("")){
            sysAdmin.setId(Integer.parseInt(id));
        }
        if (username != null && !username.equals("")){
            sysAdmin.setUsername(username);
        }
        if (password != null){
            String pwd = new BCryptPasswordEncoder().encode(password);
            sysAdmin.setPassword(pwd);
        }
        if (phone != null && !phone.equals("")){
            sysAdmin.setPhone(phone);
        }
        if (status != null && !status.equals("")){
            sysAdmin.setStatus(Integer.parseInt(status));
        }
        if (sex != null && !sex.equals("")){
            sysAdmin.setSex(sex);
        }
        adminService.update(sysAdmin);
        return new Result(true,StatusCode.OK,"编辑成功");
    }

    /**
     * 删除管理员
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delAdmin(@PathVariable("id")String id){
        adminService.delAdmin(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }
}
