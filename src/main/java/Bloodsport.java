import org.apache.commons.io.FileUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyStore;
import java.util.Base64;

/**
 * Arguments
 * 0. input filename
 * 1. encrypt || decrypt
 * 2. keystore path
 * 3. keystore password
 * 4. key password
 * 5. output filename
 */
public class Bloodsport {

    private static final String initVector = "encryptionIntVec";

    private static final String KEY_ALIAS = "skraep_key";

    private static final String ALGORITHM = "AES";
    private static final String CIPHER_SPEC = "AES/CBC/PKCS5Padding";

    public static void main(String[] args) throws Exception {

        String inputFile = args[0];
        String mode = args[1];
        String keystorePath = args[2];
        String keystorePassword = args[3];
        String keyPassword = args[4];
        String outputPath = args[5];

        IvParameterSpec IV = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());

        Key key = keyStore.getKey(KEY_ALIAS, keyPassword.toCharArray());

        SecretKeySpec secretKeySpecification = new SecretKeySpec(key.getEncoded(), ALGORITHM);

        Cipher cipher = Cipher.getInstance(CIPHER_SPEC);

        byte[] encoded = Files.readAllBytes(Paths.get(inputFile));

        if (mode.equals("encrypt")) {
            encrypt(encoded, cipher, secretKeySpecification, IV, outputPath);
        } else if (mode.equals("decrypt")) {
            decrypt(inputFile, cipher, secretKeySpecification, IV, outputPath);
        } else {
            System.out.println("Erroneous input; 'encrypt' or 'decrypt'");
        }
    }

    private static void encrypt(byte[] encoded, Cipher cipher, SecretKeySpec secretKeySpecification,
                                IvParameterSpec IV, String outputPath) throws Exception {

        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpecification, IV);

        byte[] encryptedBytes = cipher.doFinal(encoded);
        String base64String = Base64.getEncoder().encodeToString(encryptedBytes);

        FileUtils.writeStringToFile(new File(outputPath), base64String, StandardCharsets.UTF_8);
    }

    private static void decrypt(String inputFilePath, Cipher cipher, SecretKeySpec secretKeySpecification,
                                IvParameterSpec IV, String outputPath) throws Exception{

        cipher.init(Cipher.DECRYPT_MODE, secretKeySpecification, IV);

        byte[] encoded = Files.readAllBytes(Paths.get(inputFilePath));
        String content = new String(encoded, StandardCharsets.UTF_8);

        byte[] decodedBytes = Base64.getDecoder().decode(content);

        String decryptedString = new String(cipher.doFinal(decodedBytes), StandardCharsets.UTF_8);

        FileUtils.writeStringToFile(new File(outputPath), decryptedString, StandardCharsets.UTF_8);
    }
}
