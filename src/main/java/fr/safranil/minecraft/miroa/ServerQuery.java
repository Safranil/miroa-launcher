package fr.safranil.minecraft.miroa;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Query the server player number
 */
class ServerQuery {
    private int count = 0;
    private int max = 0;

    private int readVarInt(DataInputStream in) throws IOException {
        int i = 0;
        int j = 0;
        while (true) {
            int k = in.readByte();
            i |= (k & 0x7F) << j++ * 7;
            if (j > 5) throw new RuntimeException("VarInt too big");
            if ((k & 0x80) != 128) break;
        }
        return i;
    }

    private void writeVarInt(DataOutputStream out, int paramInt) throws IOException {
        while (true) {
            if ((paramInt & 0xFFFFFF80) == 0) {
                out.writeByte(paramInt);
                return;
            }

            out.writeByte(paramInt & 0x7F | 0x80);
            paramInt >>>= 7;
        }
    }

    int getCount() {
        return count;
    }

    int getMax() {
        return max;
    }

    void getPlayerCount() {
        try {
            Socket socket = new Socket();
            OutputStream out;
            DataOutputStream dos;
            InputStream in;
            InputStreamReader inr;

            InetSocketAddress host = new InetSocketAddress(MiroaLauncher.SERVER_HOST, MiroaLauncher.SERVER_PORT);

            socket.setSoTimeout(5000);
            socket.connect(host, 5000);

            in = socket.getInputStream();
            out = socket.getOutputStream();
            inr = new InputStreamReader(in);
            dos = new DataOutputStream(out);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream handshake = new DataOutputStream(baos);

            handshake.write(0x00);
            writeVarInt(handshake, 4);
            writeVarInt(handshake, host.getHostString().length());
            handshake.writeBytes(host.getHostString());
            handshake.writeShort(host.getPort());
            writeVarInt(handshake, 1);
            writeVarInt(dos, baos.size());
            dos.write(baos.toByteArray());

            dos.writeByte(0x01);
            dos.writeByte(0x00);
            DataInputStream dis = new DataInputStream(in);
            readVarInt(dis);
            int id = readVarInt(dis);

            if (id == -1) {
                throw new IOException("Premature end of stream.");
            }

            if (id != 0x00) {
                throw new IOException("Invalid packetID");
            }
            int length = readVarInt(dis);

            if (length == -1) {
                throw new IOException("Premature end of stream.");
            }


            if (length == 0) {
                throw new IOException("Invalid string length.");
            }

            byte[] bytesIn = new byte[length];
            dis.readFully(bytesIn);
            String json = new String(bytesIn);

            MiroaLauncher.LOGGER.info(json);
            MiroaLauncher.LOGGER.info("Getting players count on the main server");
            JSONObject tmpJSON = (JSONObject) JSONValue.parse(json);
            if (tmpJSON.containsKey("players")) {
                tmpJSON = (JSONObject) tmpJSON.get("players");
                if (tmpJSON.containsKey("max")) {
                    max = (int) tmpJSON.get("max");
                }
                if (tmpJSON.containsKey("online")) {
                    count = (int) tmpJSON.get("online");
                }
            }
            MiroaLauncher.LOGGER.info("Found players count : " + count + "/" + max);

            dos.close();
            out.close();
            inr.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
