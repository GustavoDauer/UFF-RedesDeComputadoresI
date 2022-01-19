/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.*;
import java.util.Random;
import udpclient.ServerPackage;

/**
 *
 * @author gustavo
 */
class UDPServer {

    public static void main(String args[]) throws Exception {
        try {
            DatagramSocket serverSocket = new DatagramSocket(9876);

            byte[] receiveData = new byte[1024];
            byte[] sendData = new byte[1024];

            while (true) {
                
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                System.out.println("Esperando requisicoes do cliente");
                serverSocket.receive(receivePacket);

                ServerPackage serverPackage = new ServerPackage(receiveData);
                
                // Chance de simulacao de um pacote ruim
                Random rand = new Random();
                int chance = rand.nextInt(101);
                
                if(chance <= 10) {
                    serverPackage.generateDefect();
                }
                
                int sequenceNumber = serverPackage.getSequenceNumber(); 
                String sentence = new String(serverPackage.getData()); 
                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();

                System.out.println("Cliente: " + IPAddress + ":" + port);
                System.out.println("Numero de sequencia: " + sequenceNumber);
                System.out.println("Mensagem: " + sentence);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream w = new DataOutputStream(baos);
                w.writeInt(sequenceNumber);
                byte[] sentenceBytes = sentence.getBytes();
                w.write(sentenceBytes);
                w.flush();
                sendData = baos.toByteArray();
                
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                serverSocket.send(sendPacket);
            }

        } catch (SocketException ex) {
            System.out.println("Porta UDP 9876 esta ocupada");
            System.exit(1);
        }

    }
}
