package com.docker.java.config;

import com.alibaba.fastjson.JSONObject;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import java.time.Duration;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;

/**
 * @author wushaopei
 * @create 2023-01-28 14:52
 */
@Configuration
public class DockerConfig {

    /**
     * 连接docker服务器
     * @return
     */
    @Bean("dockerClient")
    public DockerClient connectDocker(){
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerTlsVerify(false)
                // 这里填最上面填的ip端口号，ip换成服务器ip
                .withDockerHost("tcp://192.168.126.138:2375")
                // 这里也可以用另一种配置的
                // .withDockerHost("unix://var/run/docker.sock")
                // docker API版本号，可以用docker version查看
                .withApiVersion("20.10.23")
                // 默认
                .withRegistryUrl("https://index.docker.io/v1/").build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);

        Info info = dockerClient.infoCmd().exec();
        String infoStr = JSONObject.toJSONString(info);
        System.out.println("docker的环境信息如下：=================");
        System.out.println(infoStr);
        return dockerClient;
    }
}