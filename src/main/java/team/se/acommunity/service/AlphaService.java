package team.se.acommunity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import team.se.acommunity.dao.AlphaDao;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

// 下面是业务层注解，业务层的类都要用这个注解
@Service
// 下面这个注解表示这个bean作用范围，标注整个spring容器中只能用一个这个bean,还是可以有多个这个bean
// 默认参数是singleton,单例
@Scope("prototype") // 这个属性表示这个bean可以在spring容器中有多个，每次访问这个bean，都会生成一个bean对象，也不会调用完自己就销毁了
public class AlphaService {
    // 将dao层的bean依赖注入给service
    @Autowired
    private AlphaDao alphaDao;

    public AlphaService() {
        System.out.println("实例化");
    }
    // 下面这个注解表示这个init方法在构造器（构造方法）之后调用，一般是用来初始化属性的
    @PostConstruct
    public void init() {
        System.out.println("初始化");
    }
    // 下面这个注解表示在销毁对象之前去调用它
    @PreDestroy
    public void destroy() {
        System.out.println("销毁");
    }

    public String find() {
        return alphaDao.select();
    }
}
