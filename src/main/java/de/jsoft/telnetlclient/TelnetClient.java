package de.jsoft.telnetlclient;

// Main program
public class TelnetClient
{
    public static void main(String[] argv) {

        // Parse arguments: telnet hostname port
        String hostname="192.168.10.56";
        int port=23;

        TelnetWindow t1=new TelnetWindow(hostname, port);
    }
}
