package de.jsoft.telnetlclient;


import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// A terminal displays text in a window, scrolling up as needed
class Terminal extends Canvas
{
    private int charWidth, charHeight;  // Font size
    private String[] text;  // text[0] is current line at bottom
    private final int margin=4;  // Space around window edge (pixels)
    private final int lines=25;  // Number of lines to save

    // Constructor, set initial size (in chars) and font
    Terminal()
    {
        charHeight=12;
        setFont(new Font("Monospaced", Font.PLAIN, charHeight));
        charWidth=getFontMetrics(getFont()).stringWidth(" ");
        text=new String[lines];
        for (int i=0; i<lines; ++i)
            text[i]="";
        setSize(80*charWidth+margin*2, 25*charHeight+margin*2);
        requestFocus();

        // Use mouse to grab focus
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                requestFocus();
            }
        });
    }

    // Print a character and save in text
    public void put(char c)
    {
        Graphics g=getGraphics();
        if (c=='\r') {  // Return
            for (int i=lines-1; i>0; --i)
                text[i]=text[i-1];
            text[0]="";
            update(g);  // Clear screen and paint
        }
        else if (c==8 || c==127 || c==247)  // Backspace, delete, telnet EC
        {
            int len=text[0].length();
            if (len>0) {
                --len;
                text[0]=text[0].substring(0, len);
                g.setColor(getBackground());
                g.fillRect(len*charWidth+margin, getSize().height-margin-charHeight,
                        (len+1)*charWidth+margin, getSize().height-margin);
            }
        }
        else if (c=='\t') {  // Tab column to next multiple of 8
            text[0]+="        ";
            text[0].substring(0, text[0].length()&-8);
        }
        else if (c>=32 && c<127) {  // Printable character
            g.drawString(""+c, margin+text[0].length()*charWidth,
                    getSize().height-margin);
            text[0]+=c;
        }
        g.dispose();
    }

    // Display the text
    public void paint(Graphics g) {
        int height=getSize().height;
        for (int i=0; i<lines; ++i)
            g.drawString(text[i], margin, height-margin-i*charHeight);
    }
}