package smsender;

import java.io.*;
import java.net.Socket;
import java.util.*;

import org.json.simple.JSONObject;  
import org.json.simple.JSONValue;



/**
 * Author Dave.Graham Jan 2018
 * SMS Sender Utility for Hypermedia gateways
 */
public class SMSender {

    public static void main(String[] args) throws IOException, InterruptedException {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();

        Scanner sc = new Scanner(System.in);
        
        System.out.println("Starting gateway connection: " + "\n");

        System.out.print("Enter gateway IP address: ");
        String address = sc.next();

        System.out.print("Enter gateways sms server password: ");
        String pass = sc.next();
        
        System.out.print("Enter the mobile number we're going to be sending to: ");
        String num = sc.next();

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

        while (true) {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            p.println("{\"method\":\"authentication\",\"server_password\":\"" + pass + "\"}");
            System.out.println("\t Dave's SMS Sender Application");
            System.out.println("\t -----------------------------");
            System.out.println("\t      Select an Option");
            System.out.println("\t -----------------------------");
            System.out.println("0 \t ***** Exit *****");
            System.out.println("");
            System.out.println("1 \t Send an SMS (random port)");
            System.out.println("2 \t Send an SMS (specify port)");
            System.out.println("3 \t Send an SMS out of all ports of specified card");
            System.out.println("4 \t Send an SMS out of all ports (6u units only)");
            System.out.println("5 \t Parsing JSON test");
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
                    sendRSingle(s, num);
                    break;

                case 2:
                    sendSingle(s, sc, num);
                    break;

                case 3:
                    allCard(s, sc, num);
                    break;

                case 4:
                    allPorts(s, sc, num);
                    break;
                    
                case 5:
                    jsonTest(s, sc, num);

                default:
                    System.err.println("You must select an option from 0 to 4 ! \n");
                    break;
            } // end switch
        }// end infinite loop
        sc1.close();
        sc.close();
        s.close();
    }//end main

    private static void sendRSingle(Socket s, String num) throws IOException, InterruptedException {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();

        PrintWriter p = new PrintWriter(s.getOutputStream(), true);
        BufferedReader bufRd = new BufferedReader(new InputStreamReader(s.getInputStream()));

        p.println("{\"number\":\"" + num + "\", \"msg\":\"Random port test\", \"unicode\":\"5\"}");
        String response = bufRd.readLine();

        for (int i = 1; i < 3; i++) {
            response = bufRd.readLine();
            System.out.println("\n" + response + "\n");
        }
        System.out.println("Hit Enter to continue...");
        try {
            System.in.read();
        } catch (Exception e) {
        }//end try for catching enter key press
    }//end sendRSingle method

    private static void sendSingle(Socket s, Scanner sc, String num) throws IOException, InterruptedException {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        
        PrintWriter p = new PrintWriter(s.getOutputStream(), true);
        BufferedReader bufRd = new BufferedReader(new InputStreamReader(s.getInputStream()));

        System.out.print("Enter card address and port in format CC#P: ");
        String port = sc.next();

        System.out.println("");

        p.println("{\"number\": \"" + num + "\",\"msg\":\"" + port + "\",\"unicode\":\"2\",\"send_to_sim\":\"" + port + "\"}");
        
        String response = bufRd.readLine();
        response = bufRd.readLine();
        System.out.println(response);

        response = bufRd.readLine();
        System.out.println(response);
        
        System.out.print("Hit enter to continue");
        
        try {
            System.in.read();
        } catch (Exception e) {
            
        }

    }//end sendSingle

    private static void allPorts(Socket s, Scanner sc, String num) throws IOException, InterruptedException {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        
        System.out.print("Enter number of GSM cards installed: ");
        int cards = sc.nextInt();

        PrintWriter p = new PrintWriter(s.getOutputStream(), true);
        BufferedReader bufRd = new BufferedReader(new InputStreamReader(s.getInputStream()));
        
        int send = cards + 20;
            String response = bufRd.readLine();
            response = null;
        
        for(int i = 21; i <= send; i++){
            for(int port = 1; port < 5; port ++){
                p.println("{\"number\": \"" + num + "\",\"msg\":\"" + i + " # " + port + "\",\"unicode\":\"2\",\"send_to_sim\":\"" + i + "#" + port + "\"}");
                response = bufRd.readLine();
                System.out.println(response);

            }// end secondary for loop for port number            
        }// end main for loop for card address
        System.out.println("");
        for(int i = 21; i <= send; i++){
            for(int port = 1; port < 5; port ++){
            response = bufRd.readLine();
            System.out.println(response); 
            }
        }// end loops to collect server responses
        System.out.println("");
        System.out.println("Hit Enter to continue...");
        try {
            System.in.read();
        } catch (Exception e) {
            
        }
    }// end allPorts method

    private static void allCard(Socket s, Scanner sc, String num) throws IOException {
        
        PrintWriter p = new PrintWriter(s.getOutputStream(), true);
        BufferedReader bufRd = new BufferedReader(new InputStreamReader(s.getInputStream()));
        
        System.out.print("Enter card address for example 21: ");
        int card = sc.nextInt();
        
        String response = bufRd.readLine();
        
        for(int port = 1; port < 5; port ++){
            p.println("{\"number\": \"" + num + "\",\"msg\":\"" + card + " # " + port + "\",\"unicode\":\"2\",\"send_to_sim\":\"" + card + "#" + port + "\"}");
            response = bufRd.readLine();
            System.out.println(response.substring (1, 23));
         }
            for(int i = 1; i < 5; i ++){
                response = bufRd.readLine();
                System.out.println(response);
            }
        System.out.println("");
        System.out.println("Hit Enter to continue...");
        try {
            System.in.read();
        } catch (Exception e) {
            
        }
        
    }//end reserved2 method
    
    private static void jsonTest(Socket s, Scanner sc, String num) throws IOException{
        
        PrintWriter p = new PrintWriter(s.getOutputStream(), true);
        BufferedReader bufRd = new BufferedReader(new InputStreamReader(s.getInputStream()));

        System.out.print("Enter card address and port in format CC#P: ");
        String port = sc.next();

        System.out.println("");

        p.println("{\"number\": \"" + num + "\",\"msg\":\"" + port + "\",\"unicode\":\"2\",\"send_to_sim\":\"" + port + "\"}");
        
        String response = bufRd.readLine();
        response = bufRd.readLine();
        Object obj = JSONValue.parse(response);
        JSONObject jsonObject = (JSONObject) obj;
        
        String part1 = (String) jsonObject.get("send_to_sim");
        String part2 = (String) jsonObject.get("reply");
        
        System.out.println("Send to sim: " + part1 + " Status: " + part2);
        
        response = bufRd.readLine();
        obj = JSONValue.parse(response);
        jsonObject = (JSONObject) obj;
        
        part1 = (String) jsonObject.get("number");
        part2 = (String) jsonObject.get("reply");
        
        System.out.println("Number: " + part1 + " Status: " + part2);     
        
        System.out.print("Hit enter to continue");
        
        try {
            System.in.read();
        } catch (Exception e) {
            
        }
    }// end jsonTest
}//end class
