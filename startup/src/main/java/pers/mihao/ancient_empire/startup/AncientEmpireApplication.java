package pers.mihao.ancient_empire.startup;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableAspectJAutoProxy // 开启aop
@EnableTransactionManagement // 开启事务
@SpringBootApplication(scanBasePackages = {"pers.mihao.ancient_empire.**"})
@MapperScan("pers.mihao.ancient_empire.*.dao") // 开启mybatis mapper 扫描
public class AncientEmpireApplication {

    public static void main(String[] args) {
        SpringApplication.run(AncientEmpireApplication.class, args);
    }
}
