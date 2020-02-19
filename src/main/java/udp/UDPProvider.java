package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * @author HuaDong
 * @date 2020/2/19 17:03
 */
public class UDPProvider {

    public static void main(String[] args) throws IOException {
        System.out.println("UDPProvider Started.");

        // 作为接收者，指定一个端口号用于数据接收
        DatagramSocket ds = new DatagramSocket(20000);

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

        // 构建一份回送数据
        String responseData = "Receive data with length:" + dataLen;
        byte[] responseBuf = responseData.getBytes();
        DatagramPacket responsePacket = new DatagramPacket(responseBuf, responseBuf.length, receivePacket.getAddress(), receivePacket.getPort());

        // 回送
        ds.send(responsePacket);

        // 完成
        System.out.println("UDPProvider finished!");
        ds.close();
    }
}
