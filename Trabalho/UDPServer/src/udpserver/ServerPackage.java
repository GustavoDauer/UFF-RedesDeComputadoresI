/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package udpclient;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Random;

/**
 *
 * @author gustavo
 */
public class ServerPackage {

    int sequenceNumber;
    byte[] data;

    public ServerPackage(byte[] byteArray) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
        DataInputStream w = new DataInputStream(bais);
        this.sequenceNumber = w.readInt();
        this.data = new byte[1024];
        w.read(data);
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public byte[] getData() {
        return data;
    }

    public void generateDefect() {
        Random rand = new Random();
        int chance = rand.nextInt(10);
        
        if (chance <= 5) {
            String brokenMessage = "Mensagem corrompida";
            this.data = brokenMessage.getBytes();
        }
        else {
            int newSN = rand.nextInt(101);
            this.sequenceNumber = newSN;
        }
    }
}
