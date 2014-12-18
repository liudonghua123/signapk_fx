package com.liudonghua.apps.signapk_fx;

public class CertInfo {
	private String id;
	private String publicKeyFilePath;
	private String privateKeyFilePath;

	public CertInfo(String id, String publicKeyFilePath, String privateKeyFilePath) {
		this.id = id;
		this.publicKeyFilePath = publicKeyFilePath;
		this.privateKeyFilePath = privateKeyFilePath;
	}
	
	public String getId() {
		return id;
	}

	public String getPublicKeyFilePath() {
		return publicKeyFilePath;
	}

	public String getPrivateKeyFilePath() {
		return privateKeyFilePath;
	}

}
