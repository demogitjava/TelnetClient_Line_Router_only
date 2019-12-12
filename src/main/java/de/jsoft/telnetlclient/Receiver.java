package de.jsoft.telnetlclient;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// A Receiver thread waits for characters from an InputStream and
// sends them to a Terminal.  Also, negotiate terminal options.
class Receiver extends Thread {
    private InputStream in;
    private OutputStream out;
    private Terminal terminal;

    public Receiver(InputStream in, OutputStream out, Terminal terminal) {
        this.in=in;
        this.out=out;
        this.terminal=terminal;
        start();
    }

    // Read characters and send to terminal, negotiate no options
    public void run()
    {
        while (true)
        {
            try {
                int c=in.read();
                if (c<0) {  // EOF
                    System.out.println("Connection closed by remote host");
                    return;
                }
                else if (c==255) {  // Negotiate terminal options (RFC 854)
                    int c1=in.read();  // 253=do, 251=will
                    int c2=in.read();  // option
                    if (c1==253)  // do option, send "won't do option"
                        out.write(new byte[] {(byte)255, (byte)252, (byte)c2});
                    else if (c1==251) // will do option, send "don't do option"
                        out.write(new byte[] {(byte)255, (byte)254, (byte)c2});
                }
                else
                    terminal.put((char)c);
            }
            catch (IOException x) {
                System.out.println("Receiver: "+x);
            }
        }
    }
}
