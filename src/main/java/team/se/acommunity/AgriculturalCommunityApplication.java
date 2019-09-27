package team.se.acommunity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 通常是程序的入口用这个注解，这个类里面就有main方法，所以是程序入口
@SpringBootApplication
public class AgriculturalCommunityApplication {

	public static void main(String[] args) {
		// 这个方法是启动spring，其中就包括启动tomcat，而且自动创建了spring容器,并且将bean装配到spring容器中，他会把所有添加了注解的bean放入spring容器中
		SpringApplication.run(AgriculturalCommunityApplication.class, args);
	}

}
