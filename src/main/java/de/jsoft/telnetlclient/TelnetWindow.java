package de.jsoft.telnetlclient;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

// TelnetWindow.  Send keyboard input from a terminal to a remote socket,
// and start a Receiver to receive characters from the socket and print
// them on the terminal.
class TelnetWindow extends Frame {
    Terminal terminal;
    InputStream in;
    OutputStream out;

    // Constructor, no other methods
    TelnetWindow(String hostname, int port) {
        super("telnet "+hostname+" "+port);  // Set title

        // Set up the window
        add(terminal=new Terminal());

        // Handle window close
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
                try {
                    out.close();
                }
                catch (IOException x) {
                    System.out.println("Closing connection: "+x);
                }
            }
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });

        // Handle keys
        terminal.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char k=e.getKeyChar();
                try {

                    out.write((int)k);
                    if (k=='\r') {
                        out.write('\n');  // Convert CR to CR-LF
                        out.flush();
                    }

                }
                catch (IOException x) {
                    System.out.println("Send: "+x);
                }
            }
        });

        try {

            // Open a connection
            System.out.println("Opening connection to "+hostname+" on port "+port);
            Socket socket=new Socket(hostname, port);
            InetAddress addr=socket.getInetAddress();
            System.out.println("Connected to "+addr.getHostAddress());
            in=socket.getInputStream();
            out=socket.getOutputStream();

            // Show window
            pack();
            setVisible(true);

            // Start the Receiver
            new Receiver(in, out, terminal);
            System.out.println("Ready");
        }
        catch (UnknownHostException x) {
            System.out.println("Unknown host: "+hostname+" "+x);
            System.exit(1);
        }
        catch (IOException x) {
            System.out.println(x);
            System.exit(1);
        }
    }
}
