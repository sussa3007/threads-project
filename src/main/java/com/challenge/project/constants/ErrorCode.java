package com.challenge.project.constants;

import lombok.Getter;

@Getter
public enum ErrorCode {
    BAD_REQUEST(400, "BAD REQUEST", 24001),
    BAD_REQUEST_USERNAME(400, "BAD REQUEST USERNAME", 24002),
    ACCESS_DENIED(403, "ACCESS DENIED", 24003),
    ACCESS_DENIED_USER(403, "ACCESS DENIED USER", 24004),
    NOT_FOUND(404, "NOT FOUND", 24005),
    AWS_FILE_NOT_FOUND(404, "AWS File NOT FOUND", 24006),
    FILE_NOT_NULL(404, "FILE NOT NULL", 24007),
    ARGUMENT_MISMATCH_BAD_REQUEST(400, "ARGUMENT MISMATCH BAD REQUEST", 24008),






    FILE_CONVERT_ERROR(500, "MultipartFile -> File 전환 실패", 25001),
    AWS_IO_ERROR(500, "AWS S3 I/O Error!", 25002),
    AWS_S3_UPLOAD_ERROR(500, "AWS S3 Upload Error!", 25003),
    AWS_S3_DOWNLOAD_ERROR(500, "AWS S3 Download Error!", 25004),
    AWS_S3_DELETE_ERROR(500, "AWS S3 Delete Error!", 25005),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error", 25006 ),
    HTTP_REQUEST_IO_ERROR(500, "Generator Server Request I/O Exception", 25007 ),
    DATA_ACCESS_ERROR(500, "Data Access Error", 25008),
    NOT_IMPLEMENTED(501,"NOT IMPLEMENTED", 25009);


    private final int statusCode;


    private final String message;

    private final int errorCode;

    ErrorCode(int code, String message, int errorCode) {
        this.statusCode = code;
        this.message = message;
        this.errorCode = errorCode;
    }
}
