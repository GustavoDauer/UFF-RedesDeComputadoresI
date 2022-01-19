/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package udpclient;

/**
 *
 * @author gustavo
 */
public class ClientPackage {

    int sequenceNumber;
    String sentence;

    public ClientPackage(int sequenceNumber, String sentence) {
        this.sequenceNumber = sequenceNumber;
        this.sentence = sentence;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }
}
