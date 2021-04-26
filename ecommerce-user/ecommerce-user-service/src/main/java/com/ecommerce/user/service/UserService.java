package com.ecommerce.user.service;

import com.ecommerce.common.utils.CodecUtils;
import com.ecommerce.common.utils.NumberUtils;
import com.ecommerce.user.mapper.UserMapper;
import com.ecommerce.user.pojo.User;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author wyr
 * @version 1.0
 * @name: UserService
 * @description
 * @date 2021/3/9 17:09
 */
@Service
public class UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private AmqpTemplate amqpTemplate;

    static final String KEY_PREFIX = "user:code:phone:";

    static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public Boolean checkData(String data, Integer type) {
        User record = new User();
        switch (type){
            case 1:
                record.setUsername(data);
                break;
            case 2:
                record.setPhone(data);
                break;
            default:
                return null;
        }
        return this.userMapper.selectCount(record) == 0;

    }

    public Boolean sendVerifyCode(String phone) {
        String code =  NumberUtils.generateCode(6);
        try {
            Map<String,String> msg = new HashMap<>();
            msg.put("phone",phone);
            msg.put("code",code);
            //this.amqpTemplate.convertAndSend("ecommerce.sms.exchange","sms.verify.code",msg);
            //将code存入redis
            this.stringRedisTemplate.opsForValue().set(KEY_PREFIX+phone,code,5, TimeUnit.MINUTES);
            return true;
        }catch (Exception e){
            logger.error("发送短信失败。phone：{}， code：{}", phone, code);
            return false;
        }


    }

    /**
     * 用户注册
     * @param user
     * @param code
     * @return
     */
    public Boolean register(User user, String code) {
        // 校验短信验证码
        String s = this.stringRedisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        if(!StringUtils.equals(code,s)) {
            return false;
        }
        // 生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        // 对密码加密
        String password = CodecUtils.md5Hex(user.getPassword(), salt);
        user.setPassword(password);
        // 强制设置不能指定的参数为null
        user.setId(null);
        user.setCreated(new Date());
        // 添加到数据库
        boolean i = this.userMapper.insertSelective(user) == 1;
        // 注册成功，删除redis中的记录
        if(i){
            this.stringRedisTemplate.delete(KEY_PREFIX+user.getPhone());
        }
        return i;
    }

    /**
     * 根据用户名密码查询用户信息
     * @param username
     * @param password
     * @return
     */
    public User queryUser(String username, String password) {
        // 查询
        User record = new User();
        record.setUsername(username);
        User user = this.userMapper.selectOne(record);
        // 校验用户名
        if(user==null){
            return null;
        }
        // 校验密码
        String checkPass = CodecUtils.md5Hex(password, user.getSalt());
        if(!StringUtils.equals(checkPass,user.getPassword())){
            return null;
        }
        // 用户名密码都正确
        return user;
    }
}
