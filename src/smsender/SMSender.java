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
            System.out.println("\n" + response + "\n");   
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
            System.out.println("1 \t Send an SMS (random port)");
            System.out.println("2 \t Send an SMS (specify port)");
            System.out.println("3 \t Send a multi-sms (all ports)");
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
                    sendRSingle(s, sc);
                    break;

                case 2:
                    sendSingle(s);
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
        sc1.close();
        sc.close();
        s.close();
     }//end main
    
    private static void sendRSingle(Socket s, Scanner sc) throws IOException, InterruptedException{
        
        Scanner sc1 = new Scanner(new BufferedInputStream(System.in));
        sc1 = new Scanner(s.getInputStream());
        PrintStream p = new PrintStream(s.getOutputStream());
        
        System.out.print("Enter number to send to: ");
        String num = sc.next();
      
        p.println("{\"number\":\"" + num + "\", \"msg\":\"Random port test\", \"unicode\":\"5\"}"); 
        String response = sc1.nextLine();
        
        for (int i = 1; i < 3; i ++){
            response = sc1.nextLine();
            System.out.println("\n" + response + "\n");
            }
            System.out.println("Press the Enter key to continue...");
          
        try {
            System.in.read();
        }  
        catch(Exception e)
        {}//end try  
    }//end sendRSingle 
    
        
    
        private static void sendSingle(Socket s) throws IOException{
            PrintWriter p =  new PrintWriter(s.getOutputStream(), true);
            BufferedReader bufRd = new BufferedReader(new InputStreamReader(s.getInputStream()));
           
            p.println("{\"number\":\"07753895813\", \"msg\":\"Random port test\", \"unicode\":\"5\"}");
            String response = bufRd.readLine();
            for (int i = 1; i < 3; i++){
                response = bufRd.readLine();
                System.out.println(response + "\n");
            }
        }//end sendSingle
    
    private static void reserve(){
      System.out.println("This option is reserved for later");
      //return;  
    }
    
    private static void reserve2(){
      System.out.println("This option is reserved for later");
      //return;  
    }
}//end class
