package com.zbc.java.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * <h3>
 * <p>
 * create: 2020/9/24 <br/>
 * email: bingco.zn@gmail.com <br/>
 *
 * @author zhan_bingcong
 * @version 1.0
 * @since jdk8+
 */
public class EchoClient {

    public static void main(String[] args) throws IOException {
        int portNumber = 8080;
        Socket socket = new Socket("127.0.0.1", portNumber);
        Scanner in = new Scanner(System.in);
        PrintWriter out =
                new PrintWriter(socket.getOutputStream(), true);
        BufferedReader ain = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        String request;
        while ((request = in.nextLine()) != null) {
            if ("done".equals(request)) {
                break;
            }
            out.println(request);
            System.out.println("> " + ain.readLine());
        }
        socket.close();
    }
}
