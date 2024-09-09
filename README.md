# Merkle-Hellman Knapsack Cryptosystem

Overview

This project implements the Merkle-Hellman Knapsack Cryptosystem, which is an early public key cryptosystem based on the subset sum problem. The implementation includes key generation, encryption, and decryption using a superincreasing sequence of integers for the private key and modular arithmetic to generate the public key.

Project Structure

	•	SinglyLinkedList: A custom linked list implementation used to store the superincreasing sequence (private key) and public key values.
	•	MerkleHellmanKnapsack: The main class that handles key generation, encryption, and decryption.
	•	HashUtil: Utility for hash operations, if needed.

Key Features

	•	Key Generation: Generates a superincreasing sequence for the private key and a public key using modular arithmetic.
	•	Encryption: Encrypts a message by converting it into a binary string and using the public key to generate the ciphertext.
	•	Decryption: Decrypts the ciphertext using the private key and modular inverse to retrieve the original message.

Algorithm

The Merkle-Hellman Knapsack Cryptosystem is based on the subset sum problem, which is NP-complete. The encryption relies on converting the message to a binary format, multiplying it by the public key elements, and summing them to produce the ciphertext.

How It Works

Key Generation

	•	A superincreasing sequence is generated for the private key.
	•	The public key is generated using the formula:

```markdown
b_i = (r * w_i) % q
```

Where r and q are random coprime integers and w_i is each element in the superincreasing sequence.

Encryption

	1.	The plaintext is converted to its binary representation.
	2.	The ciphertext is computed by adding up the public key values corresponding to the binary 1 bits in the plaintext.

Decryption

	1.	The ciphertext is multiplied by the modular inverse of r modulo q.
	2.	The decrypted value is then used to solve the subset sum problem using the superincreasing sequence.

Code Example

Encrypting a Message

Here’s how encryption is done:

```markdown
public static BigInteger encrypt(String binaryString, SinglyLinkedList publicKey) {
    BigInteger cipherText = BigInteger.ZERO;

    for (int i = 0; i < binaryString.length(); i++) {
        if (binaryString.charAt(i) == '1') {
            cipherText = cipherText.add((BigInteger) publicKey.getObjectAt(i));
        }
    }

    return cipherText;
}
```

Decrypting a Message

Here’s how decryption works:

```markdown
public static String decrypt(BigInteger cipherText, BigInteger r, BigInteger q, SinglyLinkedList wList) {
    BigInteger rInverse = r.modInverse(q);  // Find modular inverse of r mod q
    BigInteger decryptedValue = cipherText.multiply(rInverse).mod(q);  // c' = (cipherText * rInverse) % q

    // Solve the subset-sum problem using the superincreasing sequence
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
```

Sample Execution
```markdown
Enter a string and I will encrypt it as a single large integer.
Welcome to Data Structures and Algorithms
Clear text:
Welcome to Data Structures and Algorithms
Number of clear text bytes = 41
Encrypted message: 31781707635014578526065699773962137393146911980721711052928064933...
Result of decryption: Welcome to Data Structures and Algorithms
```

To clone the repository, please run

```markdown
git clone https://github.com/makp-del/MerkleHellmanKnapsack.git
cd MerkleHellmanKnapsack
```

JUnit Testing

The project is tested using JUnit to ensure the correct implementation of encryption and decryption. Test cases include:

	•	Checking the correctness of the encryption process.
	•	Verifying that decryption retrieves the original message.
	•	Handling edge cases such as empty strings and very short messages.

Requirements

	•	Java 8 or higher
	•	Maven for managing dependencies

Dependencies

In pom.xml, add:

```markdown
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>1.7.30</version>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-simple</artifactId>
    <version>1.7.30</version>
</dependency>
```

How to Run

	1.	Clone the repository.
	2.	Navigate to the project folder.
	3.	Compile the project using Maven: mvn clean install
	4.	Run the project: java -jar target/MerkleHellmanKnapsack.jar

Conclusion

This project demonstrates a practical implementation of the Merkle-Hellman Knapsack Cryptosystem using Java and BigInteger. It supports encryption, decryption, and dynamic key generation with both private and public keys. The project is fully tested with JUnit and includes logging for better traceability.
