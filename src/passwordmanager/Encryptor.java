/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package passwordmanager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author mirko.ravot
 */
public class Encryptor {

    private static final String ALGORITHM = "RSA";
    private static SecretKeySpec secretKey;
    private static byte[] key;
    private final byte[] salt = new byte[] { 0x1d, 0x60, 0x43, 0x5f, 0x02, (byte) 0xe9, (byte) 0xe0, (byte) 0xae };
    private final String privateKeyFileName = "Private.key";
    private final String publicKeyFileName = "Public.key";
    private final String MYPBEALG = "PBEWithSHA1AndDESede";


    boolean fileExists;
    
    private String privateKeyFilePath; 
    private String publicKeyFilePath; 
    
    private static Encryptor internal;
    
    PrivateKey privateKey;
    PublicKey publicKey;
    Settings settings;
    
    private Encryptor() {
        settings = Settings.getSettings();
        this.setPrivateKeyFilePath(settings.getFileDir() + System.getProperty("file.separator") + this.privateKeyFileName);
        this.setPublicKeyFilePath(settings.getFileDir() + System.getProperty("file.separator") + this.publicKeyFileName);
        File privateKeyFile = new File(this.getPrivateKeyFilePath());
        this.fileExists = privateKeyFile.exists();
    }
    
    

    public String getPrivateKeyFilePath() {
        return privateKeyFilePath;
    }

    public void setPrivateKeyFilePath(String privateKeyFilePath) {
        this.privateKeyFilePath = privateKeyFilePath;
    }

    public String getPublicKeyFilePath() {
        return publicKeyFilePath;
    }

    public void setPublicKeyFilePath(String publicKeyFilePath) {
        this.publicKeyFilePath = publicKeyFilePath;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    

    
    
    
    public static Encryptor getEncryptor() {
        if (internal == null)
            internal = new Encryptor();
        return internal;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }
    
    
    public void decryptKey(String password) throws Exception {
        try {
            SecureRandom random = new SecureRandom();
            File file = new File(this.getPrivateKeyFilePath()); 
            byte[] fileContent = Files.readAllBytes(file.toPath());
            Base64.Encoder enc = Base64.getEncoder();
            int count = 20;// hash iteration count
            PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, count);
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
            SecretKeyFactory keyFac = SecretKeyFactory.getInstance(MYPBEALG);
            SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);
            Cipher pbeCipher = Cipher.getInstance(MYPBEALG);
            pbeCipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);
            byte[] keyBytes = pbeCipher.doFinal(fileContent);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
            this.setPrivateKey(kf.generatePrivate(spec));
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
        
    }
    
    

    public String encrypt(final String strToEncrypt, final String secret) throws Exception { 
        setKey(secret);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
    }

    public String decrypt(final String strToDecrypt, final String secret) throws Exception {
        setKey(secret);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));

    }

    public static void setKey(String myKey) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest sha = null;
        key = myKey.getBytes("UTF-8");
        sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16); 
        secretKey = new SecretKeySpec(key, "AES");
    }

}
