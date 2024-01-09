import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.*;

public class TLSServer {

    public static void main(String[] args) {
        int port = 9999; // Change this to your desired port number
        String keystoreFile = "server_keystore.jks"; // Change this to your keystore file
        String keystorePassword = "your_keystore_password"; // Change this to your keystore password

        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(keystoreFile), keystorePassword.toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keystorePassword.toCharArray());

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);

            System.out.println("Server started. Listening on port " + port);

            while (true) {
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                System.out.println("Client connected");

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String line = in.readLine();
                System.out.println("Received from client: " + line);

                in.close();
                clientSocket.close();
                break; // Terminate connection after receiving a line from the client
            }
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException e) {
            e.printStackTrace();
        }
    }
}
