package org.picarran.mycathome.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.picarran.mycathome.entity.GameRecordDO;

@Mapper
public interface GameRecordMapper extends BaseMapper<GameRecordDO> {
}
