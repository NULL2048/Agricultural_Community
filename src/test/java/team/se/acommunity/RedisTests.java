package team.se.acommunity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import team.se.acommunity.util.SensitiveFilter;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = AgriculturalCommunityApplication.class)
public class RedisTests {
    // 将配置好的RedisTemplate从spring容器中注入进来
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 测试String类型数据
     */
    @Test
    public void testString() {
        // 存入数据    redis规范建议将key值以一个单词开头然后加:后面写key的名字
        String redisKey = "test:count";
        // 像这个key中存入value
        redisTemplate.opsForValue().set(redisKey, 1);

        // 取数据
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        // 将值自增
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
        // 将值自减
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
    }

    @Test
    public void testHash() {
        String redisKey = "test:user";

        // 向hash里面存入值
        redisTemplate.opsForHash().put(redisKey, "id", 1);
        redisTemplate.opsForHash().put(redisKey, "username", "zhangsan");

        System.out.println(redisTemplate.opsForHash().get(redisKey, "id"));
        System.out.println(redisTemplate.opsForHash().get(redisKey, "username"));
    }

    @Test
    public void testLists() {
        String redisKey = "test:ids";

        // 从左边插入数据
        redisTemplate.opsForList().leftPush(redisKey, 101);
        redisTemplate.opsForList().leftPush(redisKey, 102);
        redisTemplate.opsForList().leftPush(redisKey, 103);

        // 获得这个列表的大小
        System.out.println(redisTemplate.opsForList().size(redisKey));
        // 获取指定下标位置的值
        System.out.println(redisTemplate.opsForList().index(redisKey, 0));
        // 获取某一个下标范围的数据
        System.out.println(redisTemplate.opsForList().range(redisKey, 0, 2));

        // 从左边弹出数据
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));
        System.out.println(redisTemplate.opsForList().leftPop(redisKey));

    }

    @Test
    public void testSet() {
        String redisKey = "test:teachers";
        // 向set中添加数据，参数可以写多个数据
        redisTemplate.opsForSet().add(redisKey, "刘备", "关羽", "张飞", "诸葛亮");

        // 集合中数据的个数
        System.out.println(redisTemplate.opsForSet().size(redisKey));
        // 从集合中删除一个数据
        System.out.println(redisTemplate.opsForSet().pop(redisKey));
        // 输出集合中的元素
        System.out.println(redisTemplate.opsForSet().members(redisKey));
    }

    @Test
    public void testSortedSet() {
        String redisKey = "test:students";

        // 有序集合存入数据有点像hash表，但是他插入的数据的key不能重复
        redisTemplate.opsForZSet().add(redisKey, "唐僧", 80);
        redisTemplate.opsForZSet().add(redisKey, "悟空", 90);
        redisTemplate.opsForZSet().add(redisKey, "八戒", 70);
        redisTemplate.opsForZSet().add(redisKey, "沙僧", 60);

        // 统计集合中的个数
        System.out.println(redisTemplate.opsForZSet().zCard(redisKey));
        // 查询某一个人的分数
        System.out.println(redisTemplate.opsForZSet().score(redisKey, "八戒"));
        System.out.println(redisTemplate.opsForZSet().score(redisKey, "悟空"));

        // 统计某一个人的排名，根据value来排名，这个是从小到大,返回的是索引
        System.out.println(redisTemplate.opsForZSet().rank(redisKey, "沙僧"));
        // 这个是从大到小排名
        System.out.println(redisTemplate.opsForZSet().reverseRank(redisKey, "沙僧"));
        // 取某一个范围内的数据, 这个范围是排名的范围，这个是从小到大排名
        System.out.println(redisTemplate.opsForZSet().range(redisKey, 0, 2));
        // 这个是从大到小排名，取排名范围
        System.out.println(redisTemplate.opsForZSet().reverseRange(redisKey, 0, 2));

    }

    @Test
    public void testKeys() {
        // 删除这个key的数据
        redisTemplate.delete("test:user");

        // 查看这个key是否存在，不存在返回false
        System.out.println(redisTemplate.hasKey("test:user"));

        // 设置数据的生命周期，设置生效时常,最后一个参数是指明时间单位
        redisTemplate.expire("test:students", 10, TimeUnit.SECONDS);
    }

    /**
     * 多次访问同一个Key,以绑定的形式来存在
     */
    @Test
    public void testBoundOperations() {
        String redisKey = "test:count";

        // 将operations这个对象和这个redisKey绑定起来，以后想要操作这个key里面的数据，就可以直接用这个对象，后面boundValueOps这个方法根据value的类型不同是使用不同方法的
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        // 自增
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }

    // 编程式事务
    @Test
    public void testTransactional() {
        // 要覆写这个SessionCallback类，将这个类通过execute方法传入redisTemplate，当执行redis命令的时候他会自动执行
        Object obj = redisTemplate.execute(new SessionCallback() {
            // redisOperations这个对象就是redis命令执行对象
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String redisKey = "test:tx";

                // 启用事务
                redisOperations.multi();

                // 添加数据
                redisOperations.opsForSet().add(redisKey, "zhangsan");
                redisOperations.opsForSet().add(redisKey, "lisi");
                redisOperations.opsForSet().add(redisKey, "wangwu");

                // 因为redis的事务事先将每一个命令添加到队列里面，然后一并提交到redis数据库执行，所以此时查询是查不到数据的
                System.out.println(redisOperations.opsForSet().members(redisKey));

                // 提交事务
                return redisOperations.exec();
            }
        });
        // 这个输出的前三个数据，是每一条命令影响的行数
        System.out.println(obj);
    }
}
