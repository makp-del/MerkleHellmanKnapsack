package edu.cmu.andrew.mpanindr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

/**
 * The MerkleHellmanKnapsack class implements the Merkle-Hellman Knapsack crypto-system, which is a public-key encryption algorithm.
 * This class allows encryption of a string as a single large integer using a super-increasing sequence (private key) and
 * a public key derived from modular arithmetic. It also supports decryption of the resulting ciphertext to recover the
 * original message.
 * <p>
 * The class demonstrates key generation, encryption, decryption, and binary-string conversion in a cryptographic setting.
 * It uses the SinglyLinkedList data structure to store both private and public keys.
 * <p>
 * The encryption and decryption are based on the Merkle-Hellman Knapsack algorithm, a variation of the subset-sum problem.
 * The public key is derived from a super-increasing sequence (private key) through modular arithmetic. Decryption requires
 * solving the subset-sum problem using the private key and the modular inverse of the key parameters.
 * <p>
 * Features:
 * - Generate a super-increasing sequence for private key and public key using modular arithmetic.
 * - Encrypt a message as a large integer by converting the input string into binary and performing subset-sum.
 * - Decrypt the ciphertext by using modular inverse and solving the subset-sum problem to retrieve the original message.
 * - Converts strings to their binary representation and back.
 * <p>
 * Usage:
 * - Input a string (less than 80 characters) and encrypt it.
 * - Decrypt the resulting ciphertext to verify correctness.
 * <p>
 * Dependencies:
 * - SinglyLinkedList: A custom linked list implementation for storing keys.
 * - HashUtil: Utility class for hashing, if needed (imported in other parts of the code).
 * <p>
 * Class Fields:
 * - BIT_LENGTH: A constant specifying the bit length for key generation (640-bit).
 *
 * @author Manjunath K P
 * @version 1.0
 * @since 2024-09-07
 */
public class MerkleHellmanKnapsack {
    private static final Logger logger = LoggerFactory.getLogger(MerkleHellmanKnapsack.class);
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
        SinglyLinkedList wList = new SinglyLinkedList();  // For super-increasing sequence (private key)
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

        // Generate the super-increasing sequence w
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

    /**
     * Converts a given string to its binary representation. Each character in the string is converted into an 8-bit binary
     * string, and all such binary strings are concatenated into a single output string.
     *
     * @param input The input string to be converted to binary representation.
     * @return A string containing the binary representation of the input string. Each character is represented as an 8-bit binary string.
     * @pre-condition The input string must be a valid non-null string.
     * @post-condition The method returns a binary string where each character in the input string is converted to its 8-bit binary representation.
     * @time-complexity O(n) - Where n is the number of characters in the input string. Each character is processed individually and converted
     * to an 8-bit binary representation.
     */
    public static String stringToBinary(String input) {
        StringBuilder binary = new StringBuilder();
        for (char character : input.toCharArray()) {
            binary.append(String.format("%8s", Integer.toBinaryString(character)).replaceAll(" ", "0"));
        }
        return binary.toString();
    }

    /**
     * Converts a binary string back to its original string representation. The binary string should be composed of
     * 8-bit binary sequences where each sequence corresponds to a character.
     *
     * @param binaryInput The binary string to be converted back to the original string.
     * @return The original string that the binary input represents, where each 8-bit segment is converted to a character.
     * @pre-condition The input binary string must be valid and composed of 8-bit binary sequences. Its length must be a multiple of 8.
     * @post-condition The method returns the original string corresponding to the input binary string. If the binary string length is not
     * a multiple of 8, the method may throw an exception or result in an incomplete conversion.
     * @time-complexity O(n / 8) ~ O(n) - Where n is the length of the binary input string. The method processes each 8-bit segment in constant time,
     * iterating over all such segments.
     */
    public static String binaryToString(String binaryInput) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < binaryInput.length(); i += 8) {
            String byteString = binaryInput.substring(i, i + 8);
            int charCode = Integer.parseInt(byteString, 2);
            result.append((char) charCode);
        }
        return result.toString();
    }

    /**
     * Encrypts a binary string using a public key list, where each '1' in the binary string corresponds to a
     * value from the public key list that is added to the resulting ciphertext.
     *
     * @param binaryString The binary string to be encrypted, typically a binary representation of the original message.
     * @param publicKey    A SinglyLinkedList containing BigInteger values representing the public key.
     *                     Each index in the public key corresponds to a position in the binary string.
     * @return A BigInteger representing the ciphertext, which is the sum of the public key values corresponding to '1's in the binary string.
     * @pre-condition The binaryString must have a length that is less than or equal to the number of elements in the publicKey list.
     * Each element in the publicKey list must be a BigInteger.
     * @post-condition The method returns a BigInteger representing the encrypted message.
     * The original binary string and public key remain unchanged.
     * @time-complexity O(n) - Where n is the length of the binary string. The method processes each character in the binary string,
     * and performs constant-time addition for each '1' found in the string.
     */
    public static BigInteger encrypt(String binaryString, SinglyLinkedList publicKey) {
        BigInteger cipherText = BigInteger.ZERO;

        for (int i = 0; i < binaryString.length(); i++) {
            if (binaryString.charAt(i) == '1') {
                cipherText = cipherText.add((BigInteger) publicKey.getObjectAt(i));
            }
        }

        return cipherText;
    }

    /**
     * Decrypts the given ciphertext using the private key and the modular inverse of r mod q.
     * The decryption process involves solving the subset-sum problem using the super-increasing sequence wList.
     *
     * @param cipherText The ciphertext as a BigInteger that was generated during encryption.
     * @param r          The private key component used in encryption. It is a coprime integer with respect to q.
     * @param q          The modulus used during encryption. It is larger than the sum of all elements in wList.
     * @param wList      A SinglyLinkedList containing BigInteger values representing the super-increasing sequence used as the private key.
     * @return A binary string representing the original message before encryption.
     * @pre-condition - cipherText must be a valid BigInteger that was generated using the encrypt() method.
     * - r and q must be valid BigIntegers, where r is coprime to q and q is greater than the sum of elements in wList.
     * - wList must be a super-increasing sequence with all elements being BigIntegers.
     * @post-condition The method returns a binary string that corresponds to the original message before encryption.
     * The original ciphertext, r, q, and wList remain unchanged.
     * @time-complexity O(n) - Where n is the number of nodes in wList. The method iterates through the elements of wList once
     * to solve the subset-sum problem.
     */
    public static String decrypt(BigInteger cipherText, BigInteger r, BigInteger q, SinglyLinkedList wList) {
        BigInteger rInverse = r.modInverse(q);  // Find modular inverse of r mod q
        BigInteger decryptedValue = cipherText.multiply(rInverse).mod(q);  // c' = (cipherText * rInverse) % q

        // Solve the subset-sum problem using the super-increasing sequence wList
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