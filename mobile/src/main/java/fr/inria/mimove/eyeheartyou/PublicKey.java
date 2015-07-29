package fr.inria.mimove.eyeheartyou;

/**
 * Created by rob on 7/15/15.
 */
import java.math.*;
import java.io.*;

public class PublicKey implements Serializable {

    // k1 is the security parameter. It is the number of bits in n.
    public int k1;

    // n=pq is a product of two large primes (such N is known as RSA modulous.
    public BigInteger n, modulous;

    private static final long serialVersionUID = -4979802656002515205L;

    private void readObject(ObjectInputStream aInputStream)
            throws ClassNotFoundException, IOException {
        // always perform the default de-serialization first
        aInputStream.defaultReadObject();
    }

    private void writeObject(ObjectOutputStream aOutputStream)
            throws IOException {
        // perform the default serialization for all non-transient, non-static
        // fields
        aOutputStream.defaultWriteObject();
    }

    public String toString() {
        return "k1" + k1 + "n" + n + "m" + modulous;
    }
    public int getK(){
        return k1;
    }
    public BigInteger getN(){
        return n;
    }
    public BigInteger getMod(){
        return modulous;
    }

    public void setK(int key){
        this.k1=key;
    }
    public void setN(BigInteger pkn){
        //BigInteger pkn = new BigInteger(str);
        this.n=pkn;
    }

    public void setMod(BigInteger pkMod){
        //BigInteger pkMod = new BigInteger(str);
        this.modulous = pkMod;
    }


}