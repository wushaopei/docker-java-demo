package com.docker.java.controller;

import com.alibaba.fastjson.JSON;
import com.docker.java.DockerEntity;
import com.docker.java.service.DockerContainerService;
import com.docker.java.service.DockerImageService;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wushaopei
 * @create 2023-01-28 15:08
 */
@RestController
public class DockerController {

    @Autowired
    private DockerImageService dockerImageService;

    @Autowired
    private DockerContainerService dockerContainerService;

    @RequestMapping("hello")
    public String hello() {
        return "hello";
    }

    /**
     * 查询镜像列表
     * @return
     */
    @GetMapping("imageList")
    public String getImageList(){
        List<Image> imageList = dockerImageService.getServerImageList();
        return JSON.toJSONString(imageList);
    }

    /**
     * 查询容器列表
     * @return
     */
    @PostMapping("containerList")
    public String getContanierList(){
        List<Container> imageList = dockerContainerService.getContanierList();
        return JSON.toJSONString(imageList);
    }


    @PostMapping("createContainer")
    public String createContainers(@RequestBody DockerEntity dockerEntity) throws URISyntaxException {
        CreateContainerResponse ubuntu = dockerContainerService.createContainers(dockerEntity);
        return ubuntu.getId();
    }


    @PostMapping("/startContainer")
    public void startContainer(@RequestParam("id") String id){
        dockerContainerService.startContainer(id);
    }
    /**
     * 暂停容器
     * @param containerId 容器ID
     */
    @PostMapping("/pauseCon")
    public void pauseCon(@RequestParam("containerId") String containerId){
        dockerContainerService.pauseCon(containerId);
    }
    /**
     * 重启容器
     * @param containerId 容器ID
     */
    @PostMapping("/restartCon")
    public void restartCon(@RequestParam("containerId") String containerId){
        dockerContainerService.restartCon(containerId);
    }
    /**
     * 停止容器
     * @param containerId  容器ID
     */
    @PostMapping("/stopContainer")
    public void stopContainer(@RequestParam("containerId") String containerId){
        dockerContainerService.stopContainer(containerId);
    }
    /**
     * 删除容器
     * @param containerId  容器ID
     */
    @PostMapping("/removeContainer")
    public void removeContainer(@RequestParam("containerId") String containerId){
        dockerContainerService.removeContainer(containerId);
    }

    /**
     * 拉取镜像
     */
    @PostMapping("/pullImage")
    public void pullImage(@RequestParam("repository") String repository){
        dockerImageService.pullImage(repository);
    }
}

