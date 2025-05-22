To test mutual TLS (mTLS) in your current Spring Boot project, follow these detailed steps:

---

### 1. **Generate Certificates**
You need a server certificate and a client certificate signed by a common Certificate Authority (CA). Here's how to generate them using `keytool`:

#### a. Generate a CA certificate:
```bash
keytool -genkeypair -alias ca -keyalg RSA -keysize 2048 -dname "CN=Test CA, OU=Dev, O=Company, L=City, ST=State, C=US" -keypass capass -keystore ca.jks -storepass capass -ext bc:c
```

#### b. Export the CA certificate:
```bash
keytool -export -alias ca -file ca-cert.cer -keystore ca.jks -storepass capass
```

#### c. Generate a server certificate:
```bash
keytool -genkeypair -alias server -keyalg RSA -keysize 2048 -dname "CN=localhost, OU=Dev, O=Company, L=City, ST=State, C=US" -keypass serverpass -keystore server-keystore.jks -storepass serverpass
```

#### d. Sign the server certificate with the CA:
1. Create a certificate signing request (CSR):
   ```bash
   keytool -certreq -alias server -file server.csr -keystore server-keystore.jks -storepass serverpass
   ```

2. Sign the CSR with the CA:
   ```bash
   keytool -gencert -alias ca -infile server.csr -outfile server-cert.cer -keystore ca.jks -storepass capass
   ```

3. Import the CA certificate and the signed server certificate into the server keystore:
   ```bash
   keytool -import -trustcacerts -alias ca -file ca-cert.cer -keystore server-keystore.jks -storepass serverpass
   keytool -import -trustcacerts -alias server -file server-cert.cer -keystore server-keystore.jks -storepass serverpass
   ```

#### e. Generate a client certificate (repeat similar steps for the client):
1. Create a client keystore and generate a key pair:
   ```bash
   keytool -genkeypair -alias client -keyalg RSA -keysize 2048 -dname "CN=Client, OU=Dev, O=Company, L=City, ST=State, C=US" -keypass clientpass -keystore client-keystore.jks -storepass clientpass
   ```

2. Create a CSR and sign it with the CA:
   ```bash
   keytool -certreq -alias client -file client.csr -keystore client-keystore.jks -storepass clientpass
   keytool -gencert -alias ca -infile client.csr -outfile client-cert.cer -keystore ca.jks -storepass capass
   ```

3. Import the CA certificate and the signed client certificate into the client keystore:
   ```bash
   keytool -import -trustcacerts -alias ca -file ca-cert.cer -keystore client-keystore.jks -storepass clientpass
   keytool -import -trustcacerts -alias client -file client-cert.cer -keystore client-keystore.jks -storepass clientpass
   ```

4. Create a truststore for the server and client:
    - Server truststore:
      ```bash
      keytool -import -trustcacerts -alias ca -file ca-cert.cer -keystore server-truststore.jks -storepass trustpass
      ```
    - Client truststore:
      ```bash
      keytool -import -trustcacerts -alias ca -file ca-cert.cer -keystore client-truststore.jks -storepass trustpass
      ```
5. Verify: Ensure the CA certificate now includes the Basic Constraints extension and is marked as a CA. You can check this using:
      ```bash
      keytool -list -v -keystore ca.jks -storepass capass
      ```
---

### 2. **Configure Spring Boot for mTLS**
Update your `application.properties` or `application.yml` to enable mTLS:

#### `application.properties`:
```properties
server.port=8443
server.ssl.key-store=classpath:server-keystore.jks
server.ssl.key-store-password=serverpass
server.ssl.key-store-type=JKS
server.ssl.trust-store=classpath:server-truststore.jks
server.ssl.trust-store-password=trustpass
server.ssl.trust-store-type=JKS
server.ssl.client-auth=need
```

This configuration ensures:
- The server uses the `server-keystore.jks` for its private key and certificate.
- The server trusts client certificates signed by the CA in `server-truststore.jks`.
- `server.ssl.client-auth=need` enforces mutual authentication.

---

### 3. **Add Certificates to Resources**
Place the `server-keystore.jks` and `server-truststore.jks` files in the `src/main/resources` directory of your project.

---

### 4. **Update Dependencies**
Ensure your `pom.xml` includes the necessary dependencies:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

---

### 5. **Test the Setup**
#### a. Using `curl`:
```bash
curl -v --key client-key.pem --cert client-cert.pem --cacert ca-cert.cer https://localhost:8443
```

#### b. Using Postman:
- Import the client certificate (`client-cert.cer`) and private key (`client-key.pem`) into Postman.
- Make a request to `https://localhost:8443`.

#### c. Using a Java Client:
You can write a Java client that uses the `client-keystore.jks` and `client-truststore.jks` for authentication.

---