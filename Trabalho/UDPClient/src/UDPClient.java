/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.rmi.UnknownHostException;
import udpclient.ClientPackage;

/**
 *
 * @author gustavo
 */
class UDPClient {

    public static void main(String args[]) throws Exception {
        try {
            String serverHostname = new String("127.0.0.1");
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName(serverHostname);
            System.out.println("Tentando conectar a " + IPAddress + ") via UDP porta 9876");

            String sentence = "";
            int sequenceNumber = 0, problems = 0; // problems pode contar os acks duplicados
            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[1024];

            while (!sentence.equals("close")) {

                System.out.print("Mensagem " + sequenceNumber + ": ");
                sentence = inFromUser.readLine();
                sendData = sentence.getBytes();

                // Contornando problema da leitura de bytes da mensagem do ACK                
                byte[] auxBytes = new byte[1024];
                ByteArrayInputStream baisAux = new ByteArrayInputStream(sendData);
                DataInputStream wAux = new DataInputStream(baisAux);
                wAux.read(auxBytes);
                sendData = auxBytes;
                sentence = new String(sendData);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DataOutputStream w2 = new DataOutputStream(baos);
                w2.writeInt(sequenceNumber);
                w2.write(sendData);
                w2.flush();
                sendData = baos.toByteArray();

                // Criação do pacote para uso da aplicacao
                ClientPackage clientPackage = new ClientPackage(sequenceNumber, sentence);

                System.out.println("Enviando " + sendData.length + " bytes para o servidor.");
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
                clientSocket.send(sendPacket);

                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                System.out.println("Esperando a confirmacao do recebimento");
                clientSocket.setSoTimeout(10000);

                try {
                    String CRCStatus;
                    clientSocket.receive(receivePacket); //String modifiedSentence = new String(receivePacket.getData());

                    // Lendo bytes retornados
                    ByteArrayInputStream bais = new ByteArrayInputStream(receivePacket.getData());
                    DataInputStream w = new DataInputStream(bais);
                    int ack = w.readInt();
                    byte[] messageBytes = new byte[1024];
                    w.read(messageBytes);
                    String receivedMessage = new String(messageBytes);

                    InetAddress returnIPAddress = receivePacket.getAddress();
                    int port = receivePacket.getPort();
                    System.out.println("Confirmacao de recebimento do servidor: " + returnIPAddress + ":" + port);
                    System.out.println("ACK: " + ack);

                    if (ack == sequenceNumber) {

                        if (!sentence.trim().equals(receivedMessage.trim())) {                          
                            CRCStatus = "BAD CHECKSUM - reenviando a partir de numero de sequencia: " + sequenceNumber;
                        } else {
                            CRCStatus = "CHECKSUM OK";
                            sequenceNumber++;
                        }

                        System.out.println("CHECKSUM: " + CRCStatus);
                        
                    } else {
                        problems++;
                        System.out.println("PROBLEMA DETECTADO: ACK de confirmacao " + sequenceNumber + " nao recebido, reenviando a partir de numero de sequencia " + sequenceNumber);
                    }

                } catch (SocketTimeoutException ste) {
                    System.out.println("Timeout: O pacote foi perdido");
                }
            }

            clientSocket.close();

        } catch (UnknownHostException ex) {
            System.err.println(ex);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}
