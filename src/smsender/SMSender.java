package smsender;

import java.io.*;
import java.net.Socket;
import java.util.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/************************************************************************
 * Author Dave.Graham Jan 2018 SMS Sender Utility for Hypermedia gateways
 ************************************************************************/
public class SMSender {

    public static void main(String[] args) throws IOException, InterruptedException {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();

        Scanner sc = new Scanner(System.in);

        System.out.println("\t -----------------------------");
        System.out.println("\t Dave's SMS Sender Application");
        System.out.println("\t -----------------------------" + "\n");

        System.out.println("\t Starting gateway connection: " + "\n");

        System.out.print("\t Enter gateway IP address: ");
        String address = sc.next();

        System.out.print("\t Enter gateways sms server password: ");
        String pass = sc.next();

        System.out.print("\t Enter the mobile number we're going to be sending to: ");
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

        Thread.sleep(1500);

        while (true) {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            //p.println("{\"method\":\"authentication\",\"server_password\":\"" + pass + "\"}");
            System.out.println("\t Dave's SMS Sender Application");
            System.out.println("\t -----------------------------");
            System.out.println("\t      Select an Option");
            System.out.println("\t -----------------------------");
            System.out.println("0 \t ***** Exit *****");
            System.out.println("");
            System.out.println("1 \t Send an SMS (random port)");
            System.out.println("2 \t Send an SMS (specify port)");
            System.out.println("3 \t Send an SMS out of all ports of specified card");
            System.out.println("4 \t Send an SMS out of all ports of all cards (6u units only)");
            System.out.println("5 \t Queue Operations");
            System.out.println("6 \t Pause Server");
            System.out.println("7 \t Start Server" + "\n");
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
                    queue(s, sc);
                    break;

                case 6:
                    pauseServer(s);
                    break;

                case 7:
                    runServer(s);
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

    private static void sendRSingle(Socket s, String num) throws IOException, InterruptedException {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();

        PrintWriter p = new PrintWriter(s.getOutputStream(), true);
        BufferedReader bufRd = new BufferedReader(new InputStreamReader(s.getInputStream()));

        p.println("{\"number\":\"" + num + "\", \"msg\":\"Random port test\", \"unicode\":\"5\"}");

        String response = bufRd.readLine();
        Object obj = JSONValue.parse(response);
        JSONObject jsonObject = (JSONObject) obj;

        String part2 = (String) jsonObject.get("reply");

        System.out.println(" Status: " + part2);

        response = bufRd.readLine();
        obj = JSONValue.parse(response);
        jsonObject = (JSONObject) obj;

        String part1 = (String) jsonObject.get("number");
        part2 = (String) jsonObject.get("reply");
        String part3 = (String) jsonObject.get("send_to_sim");

        System.out.println(" Send to Sim: " + part3 + " Number: " + part1 + " Status: " + part2 + "\n");

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
        Object obj = JSONValue.parse(response);
        JSONObject jsonObject = (JSONObject) obj;

        String part1 = (String) jsonObject.get("send_to_sim");
        String part2 = (String) jsonObject.get("reply");

        System.out.println("Send to sim: " + part1 + " Status: " + part2 + "\n");

        response = bufRd.readLine();
        obj = JSONValue.parse(response);
        jsonObject = (JSONObject) obj;

        part1 = (String) jsonObject.get("number");
        part2 = (String) jsonObject.get("reply");

        System.out.println("Number: " + part1 + " Status: " + part2 + "\n");

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

        for (int i = 21; i <= send; i++) {
            for (int port = 1; port < 5; port++) {
                p.println("{\"number\": \"" + num + "\",\"msg\":\"" + i + " # " + port + "\",\"unicode\":\"2\",\"send_to_sim\":\"" + i + "#" + port + "\"}");
                String response = bufRd.readLine();
                Object obj = JSONValue.parse(response);
                JSONObject jsonObject = (JSONObject) obj;

                String part1 = (String) jsonObject.get("send_to_sim");
                String part2 = (String) jsonObject.get("reply");

                System.out.println("Send to sim: " + part1 + " Status: " + part2 + "\n");
            }// end secondary for loop for port number            
        }// end main for loop for card address
        for (int i = 21; i <= send; i++) {
            for (int port = 1; port < 5; port++) {
                String response = bufRd.readLine();
                Object obj = JSONValue.parse(response);
                JSONObject jsonObject = (JSONObject) obj;

                String part1 = (String) jsonObject.get("send_to_sim");
                String part2 = (String) jsonObject.get("reply");

                System.out.println("Send to sim: " + part1 + " Status: " + part2 + "\n");
            }
        }// end loops to collect server responses
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

        for (int port = 1; port < 5; port++) {
            p.println("{\"number\": \"" + num + "\",\"msg\":\"" + card + " # " + port + "\",\"unicode\":\"2\",\"send_to_sim\":\"" + card + "#" + port + "\"}");
            String response = bufRd.readLine();
            Object obj = JSONValue.parse(response);
            JSONObject jsonObject = (JSONObject) obj;

            String part1 = (String) jsonObject.get("send_to_sim");
            String part2 = (String) jsonObject.get("reply");

            System.out.println("Send to sim: " + part1 + " Status: " + part2 + "\n");
        }
        for (int i = 1; i < 5; i++) {
            String response = bufRd.readLine();
            Object obj = JSONValue.parse(response);
            JSONObject jsonObject = (JSONObject) obj;

            String part1 = (String) jsonObject.get("send_to_sim");
            String part2 = (String) jsonObject.get("reply");

            System.out.println("Send to sim: " + part1 + " Status: " + part2);
        }
        System.out.println("");
        System.out.println("Hit Enter to continue...");
        try {
            System.in.read();
        } catch (Exception e) {

        }

    }//end reserved2 method

    private static void queue(Socket s, Scanner sc) throws IOException, InterruptedException {

        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();

        PrintWriter p = new PrintWriter(s.getOutputStream(), true);
        BufferedReader bufRd = new BufferedReader(new InputStreamReader(s.getInputStream()));
        while (true) {
            System.out.println("Select an option: ");
            System.out.print("");
            System.out.println("1. Query General Queue");
            System.out.println("2. Query Master Queue" + "\n");
            System.out.println("3. Flush General Queue");
            System.out.println("4. Flush Master Queue" + "\n");
            System.out.println("5. Return to Main Menu");
            System.out.println("");
            System.out.print("Option: ");

            int option = sc.nextInt();
            System.out.println("");

            if (option == 5) {
                break;
            }

            switch (option) {

                case 1:
                    p.println("{\"method\":\"get_q_size\"}");
                    String response = bufRd.readLine();
                    Object obj = JSONValue.parse(response);
                    JSONObject jsonObject = (JSONObject) obj;

                    long gen = (long) jsonObject.get("total_len");
                    System.out.println("*** Number of messages in general queue: " + gen + "\n");
                    break;

                case 2:
                    p.println("{\"method\":\"get_q_size\",\"queue_type\":\"master\"}");
                    response = bufRd.readLine();
                    obj = JSONValue.parse(response);
                    jsonObject = (JSONObject) obj;

                    long mast = (long) jsonObject.get("total_len");
                    System.out.println("*** Number of messages in master queue: " + mast + "\n");
                    break;

                case 3:
                    p.println("{\"method\":\"delete_queue\"}");
                    response = bufRd.readLine();
                    obj = JSONValue.parse(response);
                    jsonObject = (JSONObject) obj;

                    String reply = (String) jsonObject.get("reply");
                    System.out.println("Reply: " + reply + "\n");
                    break;

                case 4:
                    p.println("{ \"method\":\"delete_queue\",\"queue_type\":\"master\" }");
                    response = bufRd.readLine();
                    obj = JSONValue.parse(response);
                    jsonObject = (JSONObject) obj;

                    reply = (String) jsonObject.get("reply");
                    System.out.println("Reply: " + reply + "\n");
                    break;

                default:
                    System.err.println("You must select an option from 1 to 5 ! \n");
                    break;
            }//end switch
        }//end infinite loop
    }// end queue operations
    
    private static void pauseServer(Socket s) throws IOException, InterruptedException{
        
        PrintWriter p = new PrintWriter(s.getOutputStream(), true);
        BufferedReader bufRd = new BufferedReader(new InputStreamReader(s.getInputStream()));
        
        p.println("{ \"method\":\"pause_server\" }");
        
        String response = bufRd.readLine();
        Object obj = JSONValue.parse(response);
        JSONObject jsonObject = (JSONObject) obj;
        
        String reply = (String) jsonObject.get("reply");
        System.out.println("Reply: " + reply + "\n");
        
        Thread.sleep(1500);
        
    }//end pause server method
    
    private static void runServer(Socket s) throws IOException, InterruptedException{
        
        PrintWriter p = new PrintWriter(s.getOutputStream(), true);
        BufferedReader bufRd = new BufferedReader(new InputStreamReader(s.getInputStream()));
        
        p.println("{ \"method\":\"run_server\" }");
        
        String response = bufRd.readLine();
        Object obj = JSONValue.parse(response);
        JSONObject jsonObject = (JSONObject) obj;
        
        String reply = (String) jsonObject.get("reply");
        System.out.println("Reply: " + reply + "\n");
        
        Thread.sleep(1500);
        
     }//end run server method
}//end class
