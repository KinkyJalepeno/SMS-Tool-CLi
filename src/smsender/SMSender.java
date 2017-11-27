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
            System.out.println("\n" + response.substring(32, 45) + "\n");
            
        p.println("");
            response = sc1.nextLine();
            System.out.println("\n" + response);
            
            Thread.sleep(1000);
        
        while(true) {
            p.println("{\"method\":\"authentication\",\"server_password\":\"" + pass + "\"}");
            System.out.println("\t Dave's SMS Sender Application");
            System.out.println("\t -----------------------------");
            System.out.println("\t      Select an Option");
            System.out.println("\t -----------------------------");
            System.out.println("0 \t Exit");
            System.out.println("1 \t Send an SMS");
            System.out.println("2 \t Send a multi-port SMS");
            System.out.println("3 \t Reserved");
            System.out.println("4 \t Reserved");
            System.out.print("Option: ");
            
            // start scanner to receive option
            int option = sc.nextInt();

            // break out of infinite loop on selecting 0
            if (option == 0) {
                System.out.println("You chose option " + option + " to exit the program \n");
                break;
            }// end if for break
            
        // Start switch to check option and call matching method
            switch (option) {

                case 1:
                    sendSingle();
                    break;

                case 2:
                    sendMulti();
                    break;

                case 3:
                    reserve();
                    break;

                case 4:
                    reserve2();
                    break;

                default:
                    System.err.println("You must select an option from 0 to 4 ! \n");
                    break;
            } // end switch
        }// end infinite loop
        sc.close();
        s.close();
     }//end main
    
    private static void sendSingle(){
      System.out.println("You chose to send a message");
      //return;
    }
    
    private static void sendMulti(){
      System.out.println("You chose to send a multi-message");
      //return;  
    }
    
    private static void reserve(){
      System.out.println("This option is reserved for later");
      //return;  
    }
    
    private static void reserve2(){
      System.out.println("This option is reserved for later");
      //return;  
    }
}//end class
