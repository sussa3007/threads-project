package com.challenge.project.file.service;

import com.challenge.project.threadsbestfollower.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final AwsService awsService;

    public List<UserResponseDto> imageLocalDownloader(List<UserResponseDto> list) {
        list.stream().parallel().forEach(dto -> {
            try {
                String profileUrl = dto.getProfileUrl();
                // if you want to get png or jpg ... you can do it
                URL url = new URL(profileUrl);
                int start = profileUrl.indexOf('?') - 3;
                String extension = profileUrl.substring(start, start+3 );

                URLConnection urlConn = url.openConnection();
                urlConn.setRequestProperty("Origin", "https://www.threads.net/");
                urlConn.connect();
                BufferedImage image = ImageIO.read(urlConn.getInputStream());
                UUID uuid = UUID.randomUUID();
                log.info("new File Object Path = {}", new File("").getAbsolutePath());
                File file = new File("image/"+ uuid + "."+extension);

                boolean write = ImageIO.write(image, extension, file);
                log.info("Image Download Result ={}", write);
                if (write) {
                    dto.setProfileUrl(awsService.putS3(file, file.getName()));
                }
                removeLocalFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return list;
    }

    public void localDirInitialize(File dir) {
        if (!dir.exists()) {
            boolean mkdir = dir.mkdir();
            if (mkdir) {
                log.info("Generate Dir Initialize Complete!");
            }
        } else {
            log.info("Generate Dir Exist!");
        }
    }

    public void deleteLocalDir(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                deleteLocalDir(file);
            }
        }
        log.info("Local Output File Delete Complete! = {}", dir.getPath());
        dir.delete();
    }



    public void removeLocalFile(File targetFile) {
        if(targetFile.delete()) {
            log.info("로컬 저장 파일 삭제 완료.");
        }else {
            log.info("로컬 저장 파일 삭제 실패.");
        }
    }

}
