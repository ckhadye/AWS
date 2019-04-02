package com.ck.aws.sts;

import java.util.Date;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.amazonaws.services.securitytoken.model.Credentials;

public class AssumeRoleExample {

	public static void main(String[] args) {
		
		String awsAccountNumber="470747977321";
		String roleName = "CustomS3AdminRole";
		
		String roleArn = "arn:aws:iam::"+awsAccountNumber+":role/"+roleName;
		String roleSessionName = "AssumeRoleSession1";
		
		AWSSecurityTokenServiceClientBuilder stsClient = AWSSecurityTokenServiceClientBuilder.standard();
		
		stsClient.setRegion("ap-south-1");
		BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIAJQ4SLHEDOCFSJBDA", "sYF2A2KEZAEvVOWd+fK98MBP25/bjzwo+2DXhyim");
		
		AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(awsCreds);
		stsClient.setCredentials(credentialsProvider);
		
		AWSSecurityTokenService stsService = stsClient.build();
		AssumeRoleRequest assumeRoleRequest = new AssumeRoleRequest();
		assumeRoleRequest.setRoleArn(roleArn);
		assumeRoleRequest.setRoleSessionName(roleSessionName);
		
		
//		BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
//				   session_creds.getAccessKeyId(),
//				   session_creds.getSecretAccessKey(),
//				   session_creds.getSessionToken());
//		
		AssumeRoleResult result = stsService.assumeRole(assumeRoleRequest);

		AWSCredentials cred;
		Credentials credentials = result.getCredentials();
		String accessKeyId = credentials.getAccessKeyId();
		String secretAccessKey = credentials.getSecretAccessKey();
		String sessionToken = credentials.getSessionToken();
		Date expiration = credentials.getExpiration();
		
		System.out.println("AccessKeyID:"+accessKeyId);
		System.out.println("SecretAccessKey:"+secretAccessKey);
		System.out.println("SessionToken:"+sessionToken);
		System.out.println("Expiration:"+expiration);
		
		System.out.println(result.getAssumedRoleUser().getAssumedRoleId());

		BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
				accessKeyId,
				secretAccessKey,
				sessionToken);

		AmazonS3 s3 = AmazonS3ClientBuilder.standard()
				                        .withCredentials(new AWSStaticCredentialsProvider(sessionCredentials))
				                        .withRegion(Regions.AP_SOUTH_1)
				                        .build();

		for (Bucket bucket : s3.listBuckets()) {
			System.out.println(bucket.getName());
//			System.out.println(bucket.getOwner());
		}		
	}
	
}
