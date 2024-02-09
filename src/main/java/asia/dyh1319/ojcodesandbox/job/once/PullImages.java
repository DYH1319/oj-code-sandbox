package asia.dyh1319.ojcodesandbox.job.once;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.core.DockerClientBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author DYH
 * @version 1.0
 * @since 2024/1/29 17:07
 */
@Component
public class PullImages implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        // 获取默认的Docker Client
        DockerClient dockerClient = DockerClientBuilder.getInstance().build();
        // 判断所需镜像是否存在
        List<Image> imageList = dockerClient.listImagesCmd().withShowAll(true).withImageNameFilter("openjdk:8-alpine").exec();
        if (imageList != null && imageList.size() != 0) {
            System.out.println("所需镜像已存在，无需重新拉取");
            return;
        }
        // 拉取所需镜像
        dockerClient.pullImageCmd("openjdk:8-alpine").exec(new PullImageResultCallback() {
            @Override
            public void onNext(PullResponseItem item) {
                super.onNext(item);
                System.out.println("正在拉取所需镜像：" + item.getStatus());
            }
        }).awaitCompletion();
        System.out.println("所需镜像拉取完成");
    }
}
