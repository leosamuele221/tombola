package tombola;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

import static javax.swing.JOptionPane.YES_OPTION;

public class Tombola extends JFrame {
    private final double VERSIONE = 4.1;
    private final String HOST = "http://serviziapp.altervista.org";
    private final Color coloreBackground = new Color(158, 43, 43);

    public Tombola(String titolo) {
        super(titolo);
    }

    protected void aggiungiWestEast(Container c) {
        JPanel pEast = new JPanel();
        pEast.setBackground(coloreBackground);
        c.add(pEast, BorderLayout.EAST);

        JPanel pWest = new JPanel();
        pWest.setBackground(coloreBackground);
        c.add(pWest, BorderLayout.WEST);
    }

    protected String getPcID() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            return sb.toString();
        } catch (Exception ex) {
            System.out.println("getMacAdd: "+ex);
            return null;
        }
    }

    protected void controlloVersione(Container c) {
        int retVal = Integer.parseInt(serverIO(c, HOST+"/tombola/tombola.php?func=checkVer&ver="+VERSIONE));

        if(retVal == -1) {
            int risp = JOptionPane.showConfirmDialog(c, "E' disponibile una nuova versione del gioco! Vuoi aggiornala?", "Aggiornamento", JOptionPane.YES_NO_OPTION);
            if((risp == YES_OPTION) && (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))) {
                try {
                    Desktop.getDesktop().browse(new URI(HOST + "/tombola/jar/tombola.zip"));
                    System.exit(0);
                } catch (Exception ex){
                    System.out.println("controlloVersione(): "+ex);
                }
            }
        }
    }

    protected String serverIO(Container c, String url){
        //System.out.println(url);
        String linea = "-2";

        try {
            URLConnection urlConnection = new URL(url).openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(5000);

            try (InputStream is = urlConnection.getInputStream(); BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                linea = br.readLine();
            } catch (IOException ex) {
                System.out.println("lettura serverIO: " + ex);
            }

            if(linea.equals("1")) {
                JOptionPane.showMessageDialog(c, "Errore del server");
                System.exit(-2);
                return linea;
            } else {
                return linea;
            }

        } catch (Exception ex) {
            System.out.println("serverIO(): " + ex);
            return linea;
        }
    }

    protected String getHost() {
        return HOST;
    }

    protected Color getColoreBackground() {
        return coloreBackground;
    }

    protected void showFinestra(String txt) {
        if(txt.equals("e")) JOptionPane.showMessageDialog(this, "Errore del server.");
        else JOptionPane.showMessageDialog(this, txt);
    }
}