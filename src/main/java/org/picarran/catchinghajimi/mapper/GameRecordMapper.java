package org.picarran.catchinghajimi.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.picarran.catchinghajimi.entity.GameRecordDO;

@Mapper
public interface GameRecordMapper extends BaseMapper<GameRecordDO> {
}
