package com.gmall.manage.controller;


import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.io.IOException;


@RestController
public class FileController {

    @Value("${fileServer.url}")
    String fileServerUrl;


    @PostMapping("fileUpload")
    public String fileUpload(@RequestParam("file")MultipartFile file) throws IOException, MyException {
        // read tracker.conf file
        String configPath = this.getClass().getResource("/tracker.conf").getFile();
        ClientGlobal.init(configPath);
        // create tracker
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getTrackerServer();
        // assign a storage
        StorageClient storageClient = new StorageClient(trackerServer);
        // upload image file to storage
        // obtain file extension name, eg: jpg
        String originalFileName = file.getOriginalFilename();
        String extName = StringUtils.substringAfter(originalFileName, ".");
        String[] uploadFileRes = storageClient.upload_file(file.getBytes(), extName, null);
        // construct file storage path
        String groupName = uploadFileRes[0];
        String pathName = uploadFileRes[1];
        String fileUrl=fileServerUrl+"/"+groupName+"/"+pathName;

        return fileUrl;
    }
}
