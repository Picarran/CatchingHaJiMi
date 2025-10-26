package org.picarran.mycathome.service.impl;

import org.picarran.mycathome.entity.UserDO;
import org.picarran.mycathome.mapper.UserMapper;
import org.picarran.mycathome.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public void register(String username, String password, String nickname) {
        UserDO u = new UserDO();
        u.setUsername(username);
        u.setPassword(encoder.encode(password));
        u.setNickname(nickname);
        u.setCreateTime(LocalDateTime.now());
        userMapper.insert(u);
    }

    @Override
    public UserDO loginByUsername(String username, String rawPassword) {
        UserDO u = userMapper.selectByUsername(username);
        if(u==null) return null;
        if(encoder.matches(rawPassword, u.getPassword())) return u;
        return null;
    }

    @Override
    public UserDO findById(Long id) {
        return userMapper.selectById(id);
    }
}
