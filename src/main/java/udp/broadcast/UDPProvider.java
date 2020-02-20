package udp.broadcast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.UUID;

/**
 * @author HuaDong
 * @date 2020/2/20 10:09
 */
public class UDPProvider {
    public static void main(String[] args) throws IOException {

        // 生成唯一标识，启动线程
        String sn = UUID.randomUUID().toString();
        Provider provider = new Provider(sn);
        provider.start();

        // 读取任意字符退出
        System.in.read();
        provider.exit();
    }

    private static class Provider extends Thread {

        private final String sn;
        private boolean done = false;
        private DatagramSocket ds = null;

        public Provider(String sn) {
            super();
            this.sn = sn;
        }

        @Override
        public void run() {
            super.run();

            System.out.println("UDPProvider Started.");

            try {
                // 作为接收者，指定一个端口号用于数据接收
                ds = new DatagramSocket(20000);
                while (!done) {
                    // 构建接收实体
                    final byte[] buf = new byte[512];
                    DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);

                    // 接收
                    ds.receive(receivePacket);

                    // 打印接收到的信息与发送者的信息
                    // 发送者的IP地址
                    String ip = receivePacket.getAddress().getHostAddress();
                    int port = receivePacket.getPort();
                    int dataLen = receivePacket.getLength();
                    String data = new String(receivePacket.getData(), 0, dataLen);
                    System.out.println("UDPProvider receive from ip:" + ip + "\tport:" + port + "\tdata:" + data);

                    // 解析端口号
                    int responsePort = MessageCreator.parsePort(data);
                    if (responsePort != -1) {
                        // 构建一份回送数据
                        String responseData = MessageCreator.buildWithSn(sn);
                        byte[] responseBuf = responseData.getBytes();
                        DatagramPacket responsePacket = new DatagramPacket(responseBuf, responseBuf.length, receivePacket.getAddress(), responsePort);

                        // 回送
                        ds.send(responsePacket);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                close();
            }

            // 完成
            System.out.println("UDPProvider finished!");
        }

        public void close() {
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }

        void exit() {
            done = true;
            close();
        }
    }
}
