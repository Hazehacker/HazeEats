package top.hazenix.test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

//@SpringBootTest
public class SpringDataRedisTest {

//    @Autowired
//    private RedisTemplate redisTemplate;
//
//    @Test
//    public void testRedisTemplate(){
//        System.out.println(redisTemplate);
//        ValueOperations valueOperations = redisTemplate.opsForValue();//【】
//        valueOperations.set("name","张三");
//        String name = (String) valueOperations.get("name");
//        System.out.println(name);
//        //这些对象的方法和命令并不完全一致
//        valueOperations.set("code","1234",3, TimeUnit.MINUTES);
//        valueOperations.setIfAbsent("code", "123456");//如果不存在就新增
//
//        HashOperations hashOperations = redisTemplate.opsForHash();//【】
//        hashOperations.put("100","name","Jack");
//        hashOperations.put("100","age",18);
//        String name1 = (String) hashOperations.get("100", "name");
//        hashOperations.delete("100","age");
//
//        ListOperations listOperations = redisTemplate.opsForList();//【】
//        listOperations.leftPushAll("mylist", "1","2","3");
//        listOperations.leftPush("mylist", "4");
//        listOperations.rightPop("mylist");//弹出
//        List mylist = listOperations.range("mylist", 0, -1);
//        System.out.println(mylist);
//        Long size = listOperations.size("mylist");
//        System.out.println(size);
//
//        SetOperations setOperations = redisTemplate.opsForSet();//【】
//        setOperations.add("set1","a","b","c","d");
//        setOperations.add("set2","a","b","e","f");
//        Set members = setOperations.members("set1");//获取set1集合的所有元素
//        System.out.println(members);
//        Long size1 = setOperations.size("set1");
//        System.out.println(size);
//        Set union = setOperations.intersect("set1", "set2");//得到两个集合的交集集合
//        System.out.println(union);
//        setOperations.remove("set1","a");//移除集合元素
//
//
//        ZSetOperations zSetOperations = redisTemplate.opsForZSet();//【】
//        zSetOperations.add("zset1","a",13);
//        zSetOperations.add("zset1","b",88);
//        zSetOperations.add("zset1","c",10);
//        Set zset1 = zSetOperations.range("zset1", 0, -1);
//        System.out.println(zset1);
//        zSetOperations.incrementScore("zset1","a",11);
//        zSetOperations.remove("zset1", "a", "b");
//
//
//        //通用命令
//        Set keys = redisTemplate.keys("*");//获取符合格式的key
//        System.out.println(keys);
//
//        Boolean name2 = redisTemplate.hasKey("name");//hasKey() 判断是否存在某个key
//        Boolean set1 = redisTemplate.hasKey("set1");
//        for(Object key :keys){
//            DataType type = redisTemplate.type(key);
//            System.out.println(type.name());
//        }
//
//        redisTemplate.delete("mylist");//根据key删除
//
//    }

}
