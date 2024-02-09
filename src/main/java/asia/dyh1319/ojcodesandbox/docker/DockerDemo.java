package asia.dyh1319.ojcodesandbox.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.core.DockerClientBuilder;

/**
 * @author DYH
 * @version 1.0
 * @since 2024/1/28 21:51
 */
public class DockerDemo {
    public static void main(String[] args) throws Exception {
        // 获取默认的Docker Client
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        
        // 1. Ping
        dockerClient.pingCmd().exec();
        
        // 2. 拉取镜像
        PullImageCmd pullImageCmd = dockerClient.pullImageCmd("openjdk:8-alpine");
        PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
            @Override
            public void onNext(PullResponseItem item) {
                System.out.println("下载镜像中：" + item);
                super.onNext(item);
            }
        };
        pullImageCmd.exec(pullImageResultCallback).awaitCompletion();
        System.out.println("下载镜像完成");
        
        // 3. 查看镜像
        for (Image image : dockerClient.listImagesCmd().exec()) {
            System.out.println(image);
        }
        
        // 4. 创建容器
        CreateContainerResponse createContainerResponse = dockerClient
            .createContainerCmd("openjdk:8-alpine")
            .withName("oj-code-sandbox")
            .withCmd("echo", "Hello Docker")
            .exec();
        System.out.println(createContainerResponse);
        
        // 5. 查看容器
        for (Container container : dockerClient.listContainersCmd().withShowAll(true).exec()) {
            System.out.println(container);
        }
        
        // 6. 启动容器
        dockerClient.startContainerCmd(createContainerResponse.getId()).exec();
        
        // 7. 删除容器
        dockerClient.removeContainerCmd(createContainerResponse.getId()).withForce(true).exec();
        
        // 8. 删除镜像
        dockerClient.removeImageCmd("openjdk:8-alpine").exec();
    }
}
