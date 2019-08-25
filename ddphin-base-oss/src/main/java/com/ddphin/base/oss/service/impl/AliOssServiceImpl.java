package com.ddphin.base.oss.service.impl;

import com.aliyun.oss.OSSClient;
import com.ddphin.base.oss.configuration.AliOSSProperties;
import com.ddphin.base.oss.service.AliOssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

public class AliOssServiceImpl implements AliOssService {
	private AliOSSProperties aliOSSProperties;

	public AliOssServiceImpl(AliOSSProperties aliOSSProperties) {
		this.aliOSSProperties = aliOSSProperties;
	}

	@Override
	public String transferImg(String fileName, InputStream is) throws Exception {
		return this.transfer(aliOSSProperties.getImgBucket(), aliOSSProperties.getImgHome(), fileName, is, aliOSSProperties.getImgCdn());
	}

	@Override
	public String transferImg(String fileName, String filePath) throws Exception {
		return this.transfer(aliOSSProperties.getImgBucket(), aliOSSProperties.getImgHome(), fileName, filePath, aliOSSProperties.getImgCdn());
	}
	@Override
	public String transferOnlineImg(String fileName, String url) throws Exception {
		return this.transferOnline(aliOSSProperties.getImgBucket(), aliOSSProperties.getImgHome(), fileName, url, aliOSSProperties.getImgCdn());
	}

	private String transfer(String bucketName, String home, String fileName, String filePath, String cdn) throws Exception {
		return this.transfer(bucketName, home, fileName, new FileInputStream(filePath), cdn);
	}
	private String transferOnline(String bucketName, String home, String fileName, String url, String cdn) throws Exception {
		return this.transfer(bucketName, home, fileName, new URL(url).openStream(), cdn);
	}
	private String transfer(String bucketName, String home, String fileName, InputStream is, String cdn) throws Exception {
		OSSClient ossClient = new OSSClient(aliOSSProperties.getEndpoint(), aliOSSProperties.getAppKeyId(), aliOSSProperties.getAppKeySecret());

		ossClient.putObject(bucketName, home.concat("/").concat(fileName), is);
		is.close();
		ossClient.shutdown();

		String URL = this.generateUrl(bucketName, home, fileName, cdn);
		return URL;
	}

	private String generateUrl(String bucketName, String home, String fileName, String cdn) {
		if (null != cdn && !cdn.isEmpty()) {
			return String.format("https://%s/%s/%s", cdn, home, fileName);
		}
		else {
			return String.format("https://%s.%s/%s/%s", bucketName, aliOSSProperties.getEndpoint(), home, fileName);
		}
	}
}
