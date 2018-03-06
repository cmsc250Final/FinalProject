/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package physicsdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author gabrielhartmark
 */
public class TestClient implements simulation.Constants{ //This class is only used to test the server's responses
    public static void main(String[] args) {
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            socket = new Socket("143.44.72.171",3200);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            out.println(GET_COLOR);
            out.flush();
            int color = Integer.parseInt(in.readLine());
            System.out.println(color);
            out.println(WAIT);
            out.flush();
            String s = in.readLine();
            System.out.println(s);
            while(true) {
            out.println(GET_INFO);
            out.flush();
                for(int i=0;i<8;i++)
                        System.out.println(in.readLine());
            try { Thread.sleep(1000*30);} catch(Exception e){}
          //  for(int i=0; i<8; i++) {
         //   System.out.println(in.readLine());
         //   } 
    
        }
        } catch(UnknownHostException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
