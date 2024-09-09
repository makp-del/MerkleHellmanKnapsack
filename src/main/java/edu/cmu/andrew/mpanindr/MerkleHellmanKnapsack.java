package edu.cmu.andrew.mpanindr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

public class MerkleHellmanKnapsack {
    private static Logger logger = LoggerFactory.getLogger(MerkleHellmanKnapsack.class);
    private static final int BIT_LENGTH = 640;  // Maximum bit length for BigInteger

    public static void main(String[] args) {
        // Input string from the user
        Scanner sc = new Scanner(System.in);
        logger.info("Enter a string and I will encrypt it as a single large integer.");
        String inputString = sc.nextLine();

        if (inputString.length() > 80) {
            logger.info("Error: String entered is too long. Please enter a string with less than 80 characters.");
            return;
        }

        // Create the lists for private and public keys
        SinglyLinkedList wList = new SinglyLinkedList();  // For superincreasing sequence (private key)
        SinglyLinkedList bList = new SinglyLinkedList();  // For public key

        // Generate the Merkle-Hellman keys
        BigInteger r = new BigInteger(BIT_LENGTH, new Random()); // r is a random 640-bit number
        BigInteger q = new BigInteger(BIT_LENGTH, new Random()); // q is a random 640-bit number
        q = q.abs(); // Ensure q is positive
        r = r.abs(); // Ensure r is positive

        // Ensure that r and q are coprime
        while (!r.gcd(q).equals(BigInteger.ONE)) {
            r = new BigInteger(BIT_LENGTH, new Random());
            r = r.abs();
        }

        logger.info("Clear text:\n" + inputString);
        logger.info("Number of clear text bytes = " + inputString.getBytes().length);

        int numberOfBits = inputString.length() * 8;

        // Generate the superincreasing sequence w
        BigInteger sum = BigInteger.ZERO;
        for (int i = 0; i < numberOfBits; i++) {
            // Ensure that the next value is greater than the sum of all previous values
            BigInteger nextValue = sum.add(new BigInteger(BIT_LENGTH / numberOfBits, new Random())).add(BigInteger.ONE);
            wList.addAtEndNode(nextValue);
            sum = sum.add(nextValue);  // Update the running sum
        }

//        logger.info("W is : " + wList);

        // Generate the public key b using: b_i = (r * w_i) % q
        for (int i = 0; i < wList.countNodes(); i++) {
            BigInteger w_i = (BigInteger) wList.getObjectAt(i);
            BigInteger b_i = (r.multiply(w_i)).mod(q);
            bList.addAtEndNode(b_i);
        }

        // Convert string input to binary
        String binaryString = stringToBinary(inputString);

        // Encrypt the binary string
        BigInteger cipherText = encrypt(binaryString, bList);
        logger.info(inputString + " is encrypted as\n" + cipherText);

        // Decrypt the ciphertext
        String decryptedBinary = decrypt(cipherText, r, q, wList);

        // Convert binary string back to the original string
        String decryptedString = binaryToString(decryptedBinary);
        logger.info("Result of decryption: " + decryptedString);
    }

    // Convert string to binary representation
    public static String stringToBinary(String input) {
        StringBuilder binary = new StringBuilder();
        for (char character : input.toCharArray()) {
            binary.append(String.format("%8s", Integer.toBinaryString(character)).replaceAll(" ", "0"));
        }
        return binary.toString();
    }

    // Convert binary string back to original string
    public static String binaryToString(String binaryInput) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < binaryInput.length(); i += 8) {
            String byteString = binaryInput.substring(i, i + 8);
            int charCode = Integer.parseInt(byteString, 2);
            result.append((char) charCode);
        }
        return result.toString();
    }

    // Encryption method using the public key list
    public static BigInteger encrypt(String binaryString, SinglyLinkedList publicKey) {
        BigInteger cipherText = BigInteger.ZERO;

        for (int i = 0; i < binaryString.length(); i++) {
            if (binaryString.charAt(i) == '1') {
                cipherText = cipherText.add((BigInteger) publicKey.getObjectAt(i));
            }
        }

        return cipherText;
    }

    // Decryption method using private key and modular inverse
    public static String decrypt(BigInteger cipherText, BigInteger r, BigInteger q, SinglyLinkedList wList) {
        BigInteger rInverse = r.modInverse(q);  // Find modular inverse of r mod q
        BigInteger decryptedValue = cipherText.multiply(rInverse).mod(q);  // c' = (cipherText * rInverse) % q

        // Solve the subset-sum problem using the superincreasing sequence wList
        StringBuilder binaryResult = new StringBuilder();
        for (int i = wList.countNodes() - 1; i >= 0; i--) {
            BigInteger w_i = (BigInteger) wList.getObjectAt(i);
            if (w_i.compareTo(decryptedValue) <= 0) {
                binaryResult.insert(0, "1");
                decryptedValue = decryptedValue.subtract(w_i);
            } else {
                binaryResult.insert(0, "0");
            }
        }

        return binaryResult.toString();
    }
}