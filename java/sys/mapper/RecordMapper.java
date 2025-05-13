package sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import sys.message.clock_record;

@Mapper
public interface RecordMapper extends BaseMapper<clock_record> {
}
