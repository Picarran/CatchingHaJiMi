package org.picarran.catchinghajimi.service;

import org.picarran.catchinghajimi.entity.UserDO;

public interface UserService {
    void register(String username, String password, String nickname);
    UserDO loginByUsername(String username, String rawPassword);
    UserDO findById(Long id);
}
