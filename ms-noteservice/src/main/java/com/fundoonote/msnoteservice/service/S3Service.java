package com.fundoonote.msnoteservice.service;

import java.io.IOException;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fundoonote.msnoteservice.exception.NSException;

@Component
public class S3Service {
	@Autowired
	private AmazonS3 s3Client;

	@Value("${aws.s3.bucket_name}")
	private String s3BucketName;

	@Value("${aws.s3.imageUrl}")
	private String s3ImageUrl;

	@Value("${aws.s3.region}")
	private String region;

	private final Logger logger = LoggerFactory.getLogger(S3Service.class);

	public String saveImageToS3(long id, MultipartFile file) throws NSException {
		String confidentialMarkerText = "CONFIDENTIAL";
		Marker confidentialMarker = MarkerFactory.getMarker(confidentialMarkerText);
		logger.debug(confidentialMarker, "saving to s3 bucket...", 1);

		// File fileToUpload = convertFromMultiPart(file);//store at disk
		String key = id + generateKey(file);

		logger.info("generated Key " + key);

		ObjectMetadata ob = new ObjectMetadata();
		// file.
		ob.setContentDisposition(file.getName());
		ob.setContentLength(file.getSize());
		ob.setContentType(file.getContentType());
		ob.setContentDisposition("inline");

		try 
		{
			s3Client.putObject(new PutObjectRequest(s3BucketName, key, file.getInputStream(), ob)
					.withCannedAcl(CannedAccessControlList.PublicRead));

		} 
		catch (SdkClientException | IOException e)
		{
			throw new NSException(108, new Object[] { "image uploading - " + e.getMessage() }, e);
		}
		String imageUrl = s3ImageUrl + s3BucketName + "/" + key;

		logger.debug("saved file into S3 bucket -", key);
		return imageUrl;
	}

	public void deleteFileFromS3(String key) 
	{
		try 
		{
			boolean exists = s3Client.doesObjectExist(s3BucketName, key);
			if (exists) 
			{
				s3Client.deleteObjects(new DeleteObjectsRequest(s3BucketName).withKeys(key));
			}
		} 
		catch (AmazonServiceException e) 
		{
			// The call was transmitted successfully, but Amazon S3 couldn't process
			// it, so it returned an error response.
			e.printStackTrace();
		} 
		catch (SdkClientException e) 
		{
			// Amazon S3 couldn't be contacted for a response, or the client
			// couldn't parse the response from Amazon S3.
			e.printStackTrace();
		}
	}

	private String generateKey(MultipartFile file) 
	{
		return "_" + Instant.now().getEpochSecond() + "_" + file.getOriginalFilename();
	}
}
