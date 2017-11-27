package smsender;
import java.io.*;
import java.net.Socket;
import java.util.*;


/**
 *
 * @author Dave.Graham
 */
public class SMSender {

    public static void main(String[] args) throws IOException, InterruptedException {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        
        Scanner sc = new Scanner(System.in);
        
        System.out.print("Enter gateway IP address: ");
        String address = sc.next();
        
        System.out.print("Enter gateways sms server password: ");
        String pass = sc.next();
        
        //open a new socket to gateway with IP address from scanner
        Socket s = new Socket(address, 63333);
        
        //get response from gateway and inject into scanner using socket (s)
        Scanner sc1 = new Scanner(s.getInputStream());
        //open output stream to send commands to gateway
        PrintStream p = new PrintStream(s.getOutputStream()); 

        //send authentication string including IP and password
        p.println("{\"method\":\"authentication\",\"server_password\":\"" + pass + "\"}");        
            String response = sc1.nextLine();
            System.out.println(response.substring(32, 45) + "\n");
            
        p.println("");
            response = sc1.nextLine();
            System.out.println(response);
            
       
        sc.close();
        s.close();
        
        
    }//end main
   
}//end class
