package com.leyou.user.service;


import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String key_prefix="user:verify:";



    public Boolean checkUser(String data, Integer type) {
        User user = new User();
        if (type ==1){
            user.setUsername(data);
        }else if (type==2){
            user.setPhone(data);
        }else {
            return null;
        }

        return userMapper.selectCount(user)==0;
    }

    public void sendVerifyCode(String phone) {
        if (StringUtils.isBlank(phone)){
            return;
        }
        //生成验证码
        String code = NumberUtils.generateCode(6);

        //发送消息到rabbitMQ
        Map<String,String> msg=new HashMap<>();
        msg.put("phone",phone);
        msg.put("code",code);
        amqpTemplate.convertAndSend("leyou.sms.exchange","verifycode.sms",msg);

        //把验证码保存到radis中
        stringRedisTemplate.opsForValue().set(key_prefix+phone,code,5, TimeUnit.MINUTES);
    }

    public void register(User user, String code) {

        String redisCode = stringRedisTemplate.opsForValue().get(key_prefix + user.getPassword());
        //校验验证码
        if (StringUtils.equals(code,redisCode)){
            return;
        }

        //生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);

        //加盐加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(),salt));

        //添加用户
        user.setId(null);
        user.setCreated(new Date());
        this.userMapper.insertSelective(user);
    }

    public User query(String username, String password) {
        //根据用户名查询用户
        User user = new User();
        user.setUsername(username);
        User user1 = this.userMapper.selectOne(user);
        if (user1==null){
            return null;
        }
        //拿到用户的盐进行加密
        password= CodecUtils.md5Hex(password, user1.getSalt());


        //两个密码对比
        if (StringUtils.equals(password,user1.getPassword())){
            //对比成功返回user
            return user1;
        }
        return null;


    }
}
