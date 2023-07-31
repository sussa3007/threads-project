package com.challenge.project.file.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.challenge.project.constants.ErrorCode;
import com.challenge.project.exception.ServiceLogicException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class AwsService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.dir}")
    private String dir;

    private final String SLASH_STRING = "/";


    private void verifyFileExistsByFullFileName(String fullFileName) {
        log.info("S3 VerifyFileExistsByFullFileName!");
        if (!amazonS3.doesObjectExist(bucket, fullFileName)) {
            throw new ServiceLogicException(ErrorCode.AWS_FILE_NOT_FOUND);
        }
    }

    public String putS3(
            File uploadFile,
            String filename
    ) {
        String path = dir + SLASH_STRING + filename;
        amazonS3.putObject(
                new PutObjectRequest(bucket, path, uploadFile)
        );
        log.info("Put S3 Object! = {}", filename);
        return amazonS3.getUrl(bucket, path).toString();
    }

    public S3Object getS3(
            String fileName
    ) {
        log.info("Get S3 Object! = {}", fileName);
        return amazonS3.getObject(
                bucket,fileName
        );
    }

    public void deleteS3(String fileName) {
        amazonS3.deleteObject(
                new DeleteObjectRequest(bucket, fileName)
        );
        log.info("Delete S3 Object! = {}", fileName);
    }

    public void deleteS3Dir() {
        ObjectListing objectListing = amazonS3.listObjects(bucket,dir);
        List<S3ObjectSummary> summaries = objectListing.getObjectSummaries();
        List<DeleteObjectsRequest.KeyVersion> keys = new ArrayList<>();
        for (S3ObjectSummary summary : summaries) {
            String key = summary.getKey();
            System.out.println("@@@@@@@@@@"+key);
            keys.add(new DeleteObjectsRequest.KeyVersion(key));
        }
        DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(bucket).withKeys(keys);

        amazonS3.deleteObjects(
                deleteRequest
        );
        log.info("Delete S3 Object! = {}", dir);
    }
}
