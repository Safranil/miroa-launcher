package fr.safranil.minecraft.miroa;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * Query the server player number
 */
public class ServerQuery {
    public static int getPlayerCount() {
        try {
            Socket socket = new Socket();
            OutputStream os;
            DataOutputStream dos;
            InputStream in;
            InputStreamReader inr;

            InetSocketAddress sa = new InetSocketAddress(MiroaLauncher.SERVER_HOST, MiroaLauncher.SERVER_PORT);

            socket.setSoTimeout(5000);
            socket.connect();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
