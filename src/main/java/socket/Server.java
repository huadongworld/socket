package socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author HuaDong
 * @date 2020/2/19 15:59
 */
public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(2000);
        System.out.println("服务器准备就绪！");
        System.out.println("服务器信息：" + serverSocket.getInetAddress() + "Port：" + serverSocket.getLocalPort());

        // 等待客户端连接
        while (true) {
            Socket client = serverSocket.accept();
            // 客户端构建异步线程
            ClientHandler clientHandler = new ClientHandler(client);
            // 启动线程
            clientHandler.start();
        }
    }

    /**
     * 客户端的消息处理
     */
    private static class ClientHandler extends Thread {
        private Socket socket;
        private boolean flag = true;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("新客户端连接：" + socket.getInetAddress() + "Port：" + socket.getPort());

            try {

                // 得到打印流，用于数据输出，服务器回送数据
                PrintStream socketOutPut = new PrintStream(socket.getOutputStream());
                // 得到输入流，用于接收数据
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                do {
                    String str = socketInput.readLine();
                    if ("bye".equals(str)) {
                        flag = false;
                        // 回送
                        socketOutPut.println("bye");
                    } else {
                        System.out.println(str);
                        // 打印到屏幕，并回送数据长度
                        socketOutPut.println("回送：" + str.length());
                    }
                } while (flag);

                socketInput.close();
                socketOutPut.close();

            } catch (Exception e) {
                System.out.println("连接异常断开！");
            }finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("客户端已关闭！");
        }
    }
}
