package com.gmall.manage;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ManageWebApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Test
	public void uploadFile() throws IOException, MyException {
		// read config file info
		String file = this.getClass().getResource("/tracker.conf").getFile();
		ClientGlobal.init(file);
		// create tracker
		TrackerClient trackerClient = new TrackerClient();
		TrackerServer trackerServer = trackerClient.getTrackerServer();
		// assign a storage
		StorageClient storageClient = new StorageClient(trackerServer);
		// upload image file to storage & return storage address
		String[] upload_file = storageClient.upload_file("/Users/lifangning/Desktop/Linux.jpg", "jpg", null);
		for (int i = 0; i < upload_file.length; i++) {
			String s = upload_file[i];
			System.out.println("s = "+ s);
		}
	}
}


