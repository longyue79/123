package sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import sys.people.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
