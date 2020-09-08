package pers.mihao.ancient_empire.startup;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAspectJAutoProxy // 开启aop
@EnableTransactionManagement // 开启事务
@EnableMongoRepositories
@MapperScan("com.mihao.ancient_empire.dao") // 开启mybatis mapper 扫描
public class AncientEmpireApplication {

    public static void main(String[] args) {
        SpringApplication.run(AncientEmpireApplication.class, args);
    }

    /**
     * mybatis 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        // paginationInterceptor.setLimit(最大单页限制数量，默认 500 条，小于 0 如 -1 不受限制);
        return paginationInterceptor;
    }
}
