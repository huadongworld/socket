package udp.broadcast;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author HuaDong
 * @date 2020/2/19 17:15
 */
public class UDPSearcher {

    private static final int LISTEN_PORT = 30000;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("UDPSearcher Started.");

        Listener listener = listen();
        sendBroadCast();

        System.in.read();

        List<Device> devices = listener.getDevicesAndClose();
        devices.forEach(device -> {
            System.out.println("Device：" + device.toString());
        });

        System.out.println("UDPSearcher Finished.");
    }

    private static Listener listen() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Listener listener = new Listener(LISTEN_PORT, countDownLatch);
        listener.start();

        countDownLatch.await();

        return listener;
    }

    private static void sendBroadCast() throws IOException {
        System.out.println("UDPSearcher sendBroadCast Started.");

        // 作为搜索方，让系统自动分配端口
        DatagramSocket ds = new DatagramSocket();

        // 构建一份请求数据
        String requestData = MessageCreator.buildWithPort(LISTEN_PORT);
        byte[] requestBuf = requestData.getBytes();
        // 20000端口，广播地址
        DatagramPacket requestPacket = new DatagramPacket(requestBuf, requestBuf.length, InetAddress.getByName("255.255.255.255"), 20000);

        // 发送
        ds.send(requestPacket);

        // 完成
        System.out.println("UDPSearcher sendBroadCast finished!");
    }

    private static class Device {
        int port;
        String ip;
        String sn;

        @Override
        public String toString() {
            return "Device{" +
                    "port=" + port +
                    ", ip='" + ip + '\'' +
                    ", sn='" + sn + '\'' +
                    '}';
        }

        public Device(int port, String ip, String sn) {
            this.port = port;
            this.ip = ip;
            this.sn = sn;
        }
    }

    private static class Listener extends Thread {

        private final int listenPort;
        private final CountDownLatch countDownLatch;
        private final List<Device> devices = new ArrayList<>();
        private boolean done = false;
        private DatagramSocket ds = null;

        public Listener(int listenPort, CountDownLatch countDownLatch) {
            super();
            this.listenPort = listenPort;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            super.run();

            countDownLatch.countDown();
            try {

                ds = new DatagramSocket(listenPort);

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
                    System.out.println("UDPSearcher receive from ip:" + ip + "\tport:" + port + "\tdata:" + data);

                    String sn = MessageCreator.parseSn(data);
                    if (sn != null) {
                        Device device = new Device(port, ip, sn);
                        devices.add(device);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                close();
            }

            // 完成
            System.out.println("UDPProvider listener finished!");
        }

        public void close() {
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }

        List<Device> getDevicesAndClose() {
            done = true;
            close();
            return devices;
        }

        void exit() {
            done = true;
            close();
        }
    }
}
