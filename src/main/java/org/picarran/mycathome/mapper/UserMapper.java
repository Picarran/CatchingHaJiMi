package org.picarran.mycathome.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.picarran.mycathome.entity.UserDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface UserMapper extends BaseMapper<UserDO> {
    @Select("SELECT * FROM `user` WHERE username = #{username} LIMIT 1")
    UserDO selectByUsername(String username);
}
