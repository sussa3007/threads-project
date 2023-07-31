package com.challenge.project.constants;

import lombok.Getter;

@Getter
public enum ErrorCode {
    BAD_REQUEST(400, "BAD REQUEST"),
    BAD_REQUEST_USERNAME(400, "BAD REQUEST USERNAME"),
    ACCESS_DENIED(403, "ACCESS DENIED"),
    NOT_FOUND(404, "NOT FOUND"),
    AWS_FILE_NOT_FOUND(404, "AWS File NOT FOUND"),
    FILE_NOT_NULL(404, "FILE NOT NULL"),
    FILE_CONVERT_ERROR(500, "MultipartFile -> File 전환 실패"),
    AWS_IO_ERROR(500, "AWS S3 I/O Error!"),
    AWS_S3_UPLOAD_ERROR(500, "AWS S3 Upload Error!"),
    AWS_S3_DOWNLOAD_ERROR(500, "AWS S3 Download Error!"),
    AWS_S3_DELETE_ERROR(500, "AWS S3 Delete Error!"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error" ),
    HTTP_REQUEST_IO_ERROR(500, "Generator Server Request I/O Exception" ),
    DATA_ACCESS_ERROR(500, "Data Access Error"),
    NOT_IMPLEMENTED(501,"NOT IMPLEMENTED");


    private final int status;


    private final String message;

    ErrorCode(int code, String message) {
        this.status = code;
        this.message = message;
    }
}
