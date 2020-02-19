package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author HuaDong
 * @date 2020/2/19 17:15
 */
public class UDPSearcher {
    public static void main(String[] args) throws IOException {
        System.out.println("UDPSearcher Started.");

        // 作为搜索方，让系统自动分配端口
        DatagramSocket ds = new DatagramSocket();

        // 构建一份请求数据
        String requestData = "hello world!";
        byte[] requestBuf = requestData.getBytes();
        // 发送数据给20000端口号的设备
        DatagramPacket requestPacket = new DatagramPacket(requestBuf, requestBuf.length, InetAddress.getLocalHost(), 20000);

        // 回送
        ds.send(requestPacket);

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

        // 完成
        System.out.println("UDPSearcher finished!");
        ds.close();
    }
}
