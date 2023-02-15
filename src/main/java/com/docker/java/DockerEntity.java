package com.docker.java;

import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;
import lombok.Data;

import java.util.List;

/**
 * @author wushaopei
 * @create 2023-02-01 23:42
 */

@Data
public class  DockerEntity{

    // 容器名
    private String containerName;

    // 镜像名
    private String imageName;

    // 服务器路径
    private String serverPath;

    // 容器路径
    private String volumePath;

    // 映射端口： 每个String为 key-value结构，比如： 8080:8080 ，代表服务器的8080映射容器的8080
    private List<String> bindingPorts;


}
