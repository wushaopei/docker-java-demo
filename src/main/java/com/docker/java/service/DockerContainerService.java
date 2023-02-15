package com.docker.java.service;
import com.docker.java.DockerEntity;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wushaopei
 * @create 2023-01-28 19:26
 */
@Component
public class DockerContainerService {


    @Autowired
    private DockerClient client;

    /**
     * 创建容器，映射单个端口
     * @return
     */
    public CreateContainerResponse createContainers(DockerEntity dockerEntity){
        CreateContainerCmd ccm = client.createContainerCmd(dockerEntity.getImageName()) // 镜像名
                .withName(dockerEntity.getContainerName()); // 容器名
        HostConfig hostConfig = new HostConfig();

        // 端口映射
        if (!CollectionUtils.isEmpty(dockerEntity.getBindingPorts())){
            // 封装端口映射
            List<PortBinding> list = new ArrayList<>();
            List<String> bindingPorts = dockerEntity.getBindingPorts();
            for (String bindingPort : bindingPorts) {
                String[] split = bindingPort.split(":");
                String serverPath = split[0];
                String volumePath = split[1];
                // 服务器暴露端口
                ccm = ccm.withExposedPorts(ExposedPort.parse(serverPath + "/tcp"));
                // 绑定主机端⼝ -> docker容器端⼝
                list.add(PortBinding.parse(serverPath + ":" + volumePath));
            }
            hostConfig.withPortBindings(list);
        }

        // 路径挂载
        if (!StringUtils.isEmpty(dockerEntity.getServerPath()) && !StringUtils.isEmpty(dockerEntity.getVolumePath())){
//        Bind bind = new Bind("服务器路径",new Volume("容器路径"));
            Bind bind = new Bind(dockerEntity.getServerPath(), new Volume(dockerEntity.getVolumePath()));
            hostConfig.setBinds(bind);
        }
        CreateContainerResponse container = ccm
                .withHostConfig(hostConfig)
                .exec();
        return container;
    }

    /**
     * 启动容器
     * @param containerId  容器ID
     */
    public void startContainer( String containerId){
        client.startContainerCmd(containerId).exec();
    }

    /**
     * 暂停容器
     * @param containerId 容器ID
     */
    public void pauseCon( String containerId) {
        client.pauseContainerCmd(containerId).exec();
    }

    /**
     * 重启容器
     * @param containerId 容器ID
     */
    public void restartCon( String containerId) {
        client.restartContainerCmd(containerId).exec();
    }

    /**
     * 停止容器
     * @param containerId  容器ID
     */
    public void stopContainer(String containerId){
        client.stopContainerCmd(containerId).exec();
    }

    /**
     * 删除容器
     * @param containerId  容器ID
     */
    public void removeContainer(String containerId){
        // 删除前要先停止容器
//        stopContainer(containerId);
        client.removeContainerCmd(containerId).exec();
    }

    /**
     * 执行命令
     * @param containerName
     * @param imageName
     * @return
     */
    public CreateContainerResponse createContainers4(String containerName,String imageName){
        HostConfig hostConfig = new HostConfig();
        CreateContainerResponse container = client.createContainerCmd(imageName)
                .withName(containerName)
                .withHostConfig(hostConfig)
                .withCmd("python","/root/scripts/test.py")
                .exec();
        return container;
    }

    /**
     * 创建容器，先挂载路径，再执行脚本进行安装软件（比如：安装jdk等）
     * @param client
     * @param containerName
     * @param imageName
     * @return
     */
    public CreateContainerResponse createContainers(DockerClient client,String containerName,String imageName){
        HostConfig hostConfig = new HostConfig();
        Bind bind = new Bind("服务器路径",new Volume("容器路径"));
        hostConfig.setBinds(bind);
        CreateContainerResponse container = client.createContainerCmd(imageName)
                .withName(containerName)
                .withHostConfig(hostConfig)
                .withCmd("source","/opt/jdk/install.sh")
                .exec();
        return container;
    }

    /**
     * 查询容器列表
     * @return
     */
    public List<Container> getContanierList() {
        List<Container> exec = client.listContainersCmd().withShowAll(true).exec();
        return exec;
    }
}
