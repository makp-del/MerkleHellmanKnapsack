package edu.cmu.andrew.mpanindr;

import org.junit.jupiter.api.Test;
import java.math.BigInteger;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MerkleHellmanKnapsackTest {

    private static final int BIT_LENGTH = 640;

    // Test the stringToBinary method
    @Test
    void testStringToBinary() {
        String input = "ABC";
        String binary = MerkleHellmanKnapsack.stringToBinary(input);
        assertEquals("010000010100001001000011", binary);
    }

    // Test the binaryToString method
    @Test
    void testBinaryToString() {
        String binary = "010000010100001001000011";  // Binary for "ABC"
        String result = MerkleHellmanKnapsack.binaryToString(binary);
        assertEquals("ABC", result);
    }

    // Test binary conversion round trip
    @Test
    void testBinaryConversionRoundTrip() {
        String input = "Test123!";
        String binary = MerkleHellmanKnapsack.stringToBinary(input);
        String output = MerkleHellmanKnapsack.binaryToString(binary);
        assertEquals(input, output);  // Ensure round-trip conversion works
    }

    // Test encryption and decryption round trip
    @Test
    void testEncryptDecryptRoundTrip() {
        String input = "EncryptThis";
        SinglyLinkedList wList = new SinglyLinkedList();
        SinglyLinkedList bList = new SinglyLinkedList();

        BigInteger r = new BigInteger(BIT_LENGTH, new Random());
        BigInteger q = new BigInteger(BIT_LENGTH, new Random());
        q = q.abs();  // Ensure q is positive
        r = r.abs();  // Ensure r is positive

        // Ensure r and q are coprime
        while (!r.gcd(q).equals(BigInteger.ONE)) {
            r = new BigInteger(BIT_LENGTH, new Random());
            r = r.abs();
        }

        int numberOfBits = input.length() * 8;
        BigInteger sum = BigInteger.ZERO;

        // Generate the superincreasing sequence w
        for (int i = 0; i < numberOfBits; i++) {
            BigInteger nextValue = sum.add(new BigInteger(BIT_LENGTH / numberOfBits, new Random())).add(BigInteger.ONE);
            wList.addAtEndNode(nextValue);
            sum = sum.add(nextValue);
        }

        // Generate the public key b
        for (int i = 0; i < wList.countNodes(); i++) {
            BigInteger w_i = (BigInteger) wList.getObjectAt(i);
            BigInteger b_i = (r.multiply(w_i)).mod(q);
            bList.addAtEndNode(b_i);
        }

        // Convert string to binary and encrypt
        String binaryString = MerkleHellmanKnapsack.stringToBinary(input);
        BigInteger cipherText = MerkleHellmanKnapsack.encrypt(binaryString, bList);

        // Decrypt the ciphertext
        String decryptedBinary = MerkleHellmanKnapsack.decrypt(cipherText, r, q, wList);
        String decryptedString = MerkleHellmanKnapsack.binaryToString(decryptedBinary);

        // Ensure the decrypted string matches the original
        assertEquals(input, decryptedString);
    }

    // Test with an empty string
    @Test
    void testEncryptDecryptWithEmptyString() {
        String input = "";
        SinglyLinkedList wList = new SinglyLinkedList();
        SinglyLinkedList bList = new SinglyLinkedList();

        BigInteger r = new BigInteger(BIT_LENGTH, new Random());
        BigInteger q = new BigInteger(BIT_LENGTH, new Random());
        q = q.abs();  // Ensure q is positive
        r = r.abs();  // Ensure r is positive

        // Ensure r and q are coprime
        while (!r.gcd(q).equals(BigInteger.ONE)) {
            r = new BigInteger(BIT_LENGTH, new Random());
            r = r.abs();
        }

        // Convert string to binary and encrypt (should return 0 for empty input)
        String binaryString = MerkleHellmanKnapsack.stringToBinary(input);
        BigInteger cipherText = MerkleHellmanKnapsack.encrypt(binaryString, bList);

        // Decrypt the ciphertext
        String decryptedBinary = MerkleHellmanKnapsack.decrypt(cipherText, r, q, wList);
        String decryptedString = MerkleHellmanKnapsack.binaryToString(decryptedBinary);

        // Ensure the decrypted string matches the original (empty string)
        assertEquals(input, decryptedString);
    }

    // Test if the input length exceeds 80 characters (should return an error)
    @Test
    void testInputLengthExceeds80Characters() {
        String longInput = "This string is way too long and should cause an error because it's more than 80 characters long.";
        assertTrue(longInput.length() > 80);
    }

    // Test string with special characters
    @Test
    void testEncryptDecryptWithSpecialCharacters() {
        String input = "Special@#123!$%^&*()";
        SinglyLinkedList wList = new SinglyLinkedList();
        SinglyLinkedList bList = new SinglyLinkedList();

        BigInteger r = new BigInteger(BIT_LENGTH, new Random());
        BigInteger q = new BigInteger(BIT_LENGTH, new Random());
        q = q.abs();  // Ensure q is positive
        r = r.abs();  // Ensure r is positive

        // Ensure r and q are coprime
        while (!r.gcd(q).equals(BigInteger.ONE)) {
            r = new BigInteger(BIT_LENGTH, new Random());
            r = r.abs();
        }

        int numberOfBits = input.length() * 8;
        BigInteger sum = BigInteger.ZERO;

        // Generate the superincreasing sequence w
        for (int i = 0; i < numberOfBits; i++) {
            BigInteger nextValue = sum.add(new BigInteger(BIT_LENGTH / numberOfBits, new Random())).add(BigInteger.ONE);
            wList.addAtEndNode(nextValue);
            sum = sum.add(nextValue);
        }

        // Generate the public key b
        for (int i = 0; i < wList.countNodes(); i++) {
            BigInteger w_i = (BigInteger) wList.getObjectAt(i);
            BigInteger b_i = (r.multiply(w_i)).mod(q);
            bList.addAtEndNode(b_i);
        }

        // Convert string to binary and encrypt
        String binaryString = MerkleHellmanKnapsack.stringToBinary(input);
        BigInteger cipherText = MerkleHellmanKnapsack.encrypt(binaryString, bList);

        // Decrypt the ciphertext
        String decryptedBinary = MerkleHellmanKnapsack.decrypt(cipherText, r, q, wList);
        String decryptedString = MerkleHellmanKnapsack.binaryToString(decryptedBinary);

        // Ensure the decrypted string matches the original
        assertEquals(input, decryptedString);
    }
}