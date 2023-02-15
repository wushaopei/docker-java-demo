package com.docker.java.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.command.LoadImageCmd;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.*;
import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author wushaopei
 * @create 2023-01-28 15:13
 */
@Component
public class DockerImageService {

    @Autowired
    private DockerClient dockerClient;

    /**
     * ping
     * @throws URISyntaxException
     */
    private void dockerPing() throws URISyntaxException {
        dockerClient.pingCmd().exec();
        System.out.println("ping...");
    }

    /**
     * docker info
     * @throws URISyntaxException
     */
    private void dockerInfo() throws URISyntaxException {
        Info info = dockerClient.infoCmd().exec();
        System.out.println("docker info : " + info.toString());
    }

    /**
     * 搜索镜像
     * @param imageName
     * @return
     * @throws URISyntaxException
     */
    private List<String> searchImages(String imageName) throws URISyntaxException {
        List dockerSearch = dockerClient.searchImagesCmd("images").exec();
        List<String> list = new ArrayList<>();
        for (Object search : dockerSearch) {
            SearchItem search1 = (SearchItem) search;
            System.out.println(search1.getName());
            list.add(((SearchItem) search).getName());
        }

        return dockerSearch;
    }


    /**
     * 从Dockerfile构建镜像
     * @return
     * @throws URISyntaxException
     */
    public String buildImage(String imageName, String imageTag, String dockerFile) throws URISyntaxException {
        ImmutableSet<String> tag = ImmutableSet.of(imageName + ":" + imageTag);
//        String imageId = dockerClient.buildImageCmd(new File("/opt/tmp/Dockerfile"))
        String imageId = dockerClient.buildImageCmd(new File(dockerFile))
                .withTags(tag)
                .start()
                .awaitImageId();
        return imageId;
    }

    /**
     * 获取服务器上所有的镜像
     * @return
     */
    public List<Image> getServerImageList(){
        List<Image> imageList = dockerClient.listImagesCmd().exec();
//        System.out.println(imageList.size());
        for(Image image : imageList) {
            // 打印镜像名
            System.out.println(image.getRepoTags()[0]);
        }
        return imageList;
    }

    /**
     * 判断服务器上是否存在某个镜像
     * @return
     */
    public boolean existServerImage(){
        boolean existFlag = false;
        List images = dockerClient.listImagesCmd().withImageNameFilter("busybox").exec();

        if (images.isEmpty()) {
            System.out.println("不存在 busybox 镜像。");
        } else {
            existFlag = true;
            System.out.println("存在 busybox 镜像。");
        }
        return existFlag;
    }


    /**
     * 加载镜像
     * @param filePath
     * @return
     */
    public LoadImageCmd loadImage(String filePath){
        LoadImageCmd loadImageCmd = null;
        try {
            loadImageCmd = dockerClient.loadImageCmd(new FileInputStream(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return loadImageCmd;
    }

    /**
     * 拉取镜像
     * @param repository 镜像名称:tag名称  "nginx:latest"
     * @return
     */
    public boolean pullImage(String repository){
        boolean isSuccess = false;
        try {
//            isSuccess = dockerClient.pullImageCmd(repository)
//                    .start()
//                    .awaitCompletion(30, TimeUnit.SECONDS);
//            isSuccess = dockerClient.pullImageCmd("nginx:latest").exec(new PullImageResultCallback()).awaitCompletion(30, TimeUnit.SECONDS);
            ResultCallback<PullResponseItem> exec = dockerClient.pullImageCmd(repository).exec(new ResultCallback<PullResponseItem>() {

                public void onStart(Closeable closeable) {
                    System.out.println("开始下载!");
                }

                public void onNext(PullResponseItem object) {
                    // 实时显示出下载信息
                    System.out.println(object.getStatus());
                }

                public void onError(Throwable throwable) {
                    throwable.printStackTrace();
                }

                public void onComplete() {
                    System.out.println("下载完毕!");
                }

                public void close() throws IOException {

                }

            });

        } finally {
            return isSuccess;
        }
    }


    /**
     * 推送到Harbor仓库
     * @throws URISyntaxException
     * @throws InterruptedException
     */
    public void pushImage(String username, String password, String harborAddress, String imagesName) throws URISyntaxException, InterruptedException {
        AuthConfig authConfig = new AuthConfig()
                .withUsername(username) //"admin"
                .withPassword(password) // "Harbor12345"
                .withRegistryAddress(harborAddress); // "172.16.10.151:80"

        dockerClient.pushImageCmd(imagesName)
                .withAuthConfig(authConfig)
                .start()
                .awaitCompletion(30, TimeUnit.SECONDS);
    }

    /**
     * 打镜像tag
     * @throws URISyntaxException
     */
    public void tagImage(String imageName, String tagName, String version) throws URISyntaxException {
//        dockerClient.tagImageCmd("nginx:latest", "172.16.10.151:80/library/nginx", "v2").exec();
        dockerClient.tagImageCmd(imageName, tagName, version).exec();
    }

    /**
     * 查看镜像详细信息
     * @throws URISyntaxException
     * @throws InterruptedException
     */
    public void inspectImage(String tagName) throws URISyntaxException, InterruptedException {
//        InspectImageResponse response = dockerClient.inspectImageCmd("172.16.10.151:80/library/nginx:v2").exec();
        InspectImageResponse response = dockerClient.inspectImageCmd(tagName).exec();
    }

}
