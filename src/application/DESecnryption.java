package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStore.SecretKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class DESecnryption {
	char[] password = "keystorePassword".toCharArray();
	ProtectionParameter protectionParameter = new KeyStore.PasswordProtection(password);
	
	//Got a rough idea for implementation from https://www.youtube.com/watch?v=qWKwuHgWwtk

	// I am using this constructor to instantiate the class to avoid creating a
	// JCEKS file to store keys.

	public DESecnryption() {

	}

	// I am using this constructor when I want create a JCEKS file for a user as
	// well.

	public DESecnryption(String username) {

		KeyGenerator keyGenerator;
		try {
			keyGenerator = KeyGenerator.getInstance("DES");

			keyGenerator.init(56);
			Key secretKey = keyGenerator.generateKey();

			KeyStore keyStore = KeyStore.getInstance("JCEKS");
			// char[] password = "keystorePassword".toCharArray();
			keyStore.load(null, password);

			// protectionParameter = new KeyStore.PasswordProtection(password);
			SecretKeyEntry secretKeyEntry = new SecretKeyEntry((SecretKey) secretKey);
			keyStore.setEntry(username, secretKeyEntry, protectionParameter);

			FileOutputStream keyStoreOutputStream = new FileOutputStream(username + ".jceks");
//	        OutputStreamWriter writer = new OutputStreamWriter(keyStoreOutputStream);

			keyStore.store(keyStoreOutputStream, password);
			keyStoreOutputStream.close();

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// I am using this method to load the key from the file and returning the key.
	public Key loadkey(String username) {

		FileInputStream keyStoreInputStream;

		try {
			keyStoreInputStream = new FileInputStream(username + ".jceks");
			KeyStore keystore = KeyStore.getInstance("JCEKS");
			keystore.load(keyStoreInputStream, password);
			keyStoreInputStream.close();

			KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keystore.getEntry(username,
					protectionParameter);
			Key secretKey = secretKeyEntry.getSecretKey();
			return secretKey;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableEntryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
}
