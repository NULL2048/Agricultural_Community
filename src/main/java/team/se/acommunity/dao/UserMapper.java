package team.se.acommunity.dao;

import org.apache.ibatis.annotations.Mapper;
import team.se.acommunity.entity.User;

// 数据访问层的类都需要加上一个注解，装配到spring容器中，以前dao层都是用Repository这个注解，但是mybatis有自己的这个mapper注解，所以以后用这个就行
@Mapper
public interface UserMapper {
    // 想使用这些方法还需要写一个配置文件，配置文件中写了这些方法所需要的sql命令
    // mybatis会根据配置文件自动生成这个mapper接口的实现类，一般这个配置文件写在resource文件夹中的mapper文件夹中
    User getById(int id);
    User getByName(String name);
    User getByEmail(String email);
    int insertUser(User user);
    int updateStatus(int id, int status);
    int updateHeader(int id, String headerUrl);
    int updatePassword(int id, String password);
}
