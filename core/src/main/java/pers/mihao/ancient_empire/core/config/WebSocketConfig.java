package pers.mihao.ancient_empire.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import pers.mihao.ancient_empire.core.manger.GameCoreManger;

/**
 * @Author mihao
 * @Date 2020/9/11 22:40
 */
@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Bean
    public GameCoreManger gameCoreManger(){
        return new GameCoreManger();
    }

}
