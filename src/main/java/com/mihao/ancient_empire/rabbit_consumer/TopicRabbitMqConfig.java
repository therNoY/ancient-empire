package com.mihao.ancient_empire.rabbit_consumer;


import com.mihao.ancient_empire.constant.RabbitMqAdd;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicRabbitMqConfig {

    @Bean
    public Queue mongoCdr() {
        return new Queue(RabbitMqAdd.MONGO_CDR);
    }

    @Bean
    public Queue mysqlCdr() {
        return new Queue(RabbitMqAdd.MYSQL_CDR);
    }

    @Bean
    public TopicExchange topicCdr() {
        return new TopicExchange(RabbitMqAdd.CDR);
    }

    @Bean
    Binding bindingMongoMessage (Queue mongoCdr, TopicExchange topicCdr) {
        return BindingBuilder.bind(mongoCdr).to(topicCdr).with(RabbitMqAdd.MONGO_CDR);
    }

    @Bean
    Binding bindingMysqlMessage (Queue mysqlCdr, TopicExchange topicCdr) {
        return BindingBuilder.bind(mysqlCdr).to(topicCdr).with(RabbitMqAdd.MYSQL_CDR);
    }
}
