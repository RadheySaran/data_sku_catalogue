package CREAT_UPC_NAME;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.testng.annotations.Test;

public class Nmae_UPC {
	
	
	@Test
	public void UPC() throws Throwable
	{
		 String key = "82525SCROLL82021500";
//	        Mac sha256_HMAC = Mac.getInstance("SHA256");
//	        sha256_HMAC.init(new SecretKeySpec(key.getBytes(), "SHA256"));
//	        byte[] result = sha256_HMAC.doFinal("SampleData".getBytes());
	        MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        byte[] hash = digest.digest(key.getBytes(StandardCharsets.UTF_8));
	        System.out.println(DatatypeConverter.printHexBinary(hash).toString().substring(0,6));
	}

}
