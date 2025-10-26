package org.picarran.mycathome.service;

import org.picarran.mycathome.entity.UserDO;

public interface UserService {
    void register(String username, String password, String nickname);
    UserDO loginByUsername(String username, String rawPassword);
    UserDO findById(Long id);
}
