package tombola.Cartella;

import tombola.Tombola;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.*;

public class Cartella extends Tombola implements ActionListener, KeyListener {
    private final String host = getHost();
    private final JButton[][] bottoniNum = new JButton[3][9];
    private JTextField nickTxt;
    private String easterEggTxt = "";
    private String nick = "";
    private JPanel panUnisciSessione;
    private final Color coloreBackground = getColoreBackground();
    private final Color coloreNumeriUsciti = new Color(45, 158, 41, 215);

    public Cartella() {
        super("Tombola - Cartella");

        controlloVersione(this);

        panUnisciSessione = new JPanel(new FlowLayout());
        JLabel nickLabel = new JLabel("Inserisci il nome della sessione: ");
        panUnisciSessione.add(nickLabel);
        nickTxt = new JTextField("", 20);
        panUnisciSessione.add(nickTxt);
        JButton unisciti = new JButton("Unisciti");
        unisciti.addActionListener(this);
        panUnisciSessione.add(unisciti);
        add(panUnisciSessione);
        pack();

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(!nick.equals("")) serverIO(null, host + "/tombola/tombola.php?func=delCartella&nick="+nick+"&id="+getPcID());
                System.exit(0);
            }
        });
    }

    private void controlloNCaselle() {
        String pcID = getPcID();
        int retVal = Integer.parseInt(serverIO(this, host+"/tombola/tombola.php?func=addCartella&nick="+nick+"&id="+pcID));
        if(retVal == -1) {
            JOptionPane.showMessageDialog(this, "Hai raggiunto il numero massimo di cartelle.");
            System.exit(0);
        }
    }

    private void aggiungiSouthNorth(Container c) {
        JPanel pSouth = new JPanel();
        pSouth.setBackground(coloreBackground);
        c.add(pSouth, BorderLayout.SOUTH);

        //JPanel pNorth = new JPanel(new FlowLayout(FlowLayout.LEFT));
        //pNorth.setBorder(BorderFactory.createEmptyBorder(0, pEast.getWidth(),0,0));
        JPanel pNorth = new JPanel();
        pNorth.setBackground(coloreBackground);
        JLabel nomeSessione = new JLabel("Nome sessione: "+nick);
        nomeSessione.setOpaque(true);
        nomeSessione.setBackground(Color.white);
        pNorth.add(nomeSessione);
        c.add(pNorth, BorderLayout.NORTH);
    }

    private void aggiungiCaselle(Container c) {
        JPanel pCaselle = new JPanel();
        pCaselle.setBackground(coloreBackground);
        pCaselle.setLayout(new GridLayout(3, 9, 3, 3));

        int caselleDaRiemp;
        Random rnd = new Random();
        ArrayList<Integer> numUsati = new ArrayList<>();

        for(int k = 0; k < 3; k++) {
            caselleDaRiemp = 5;

            for(int m = 0; m < 9; m++) {
                if((rnd.nextBoolean() && caselleDaRiemp > 0) || m+caselleDaRiemp+1 > 9) {

                    int numero = (m * 10) + rnd.nextInt(10);
                    if(m == 8 && rnd.nextBoolean() || numero == 0) numero++;

                    if(numUsati.contains(numero)) {
                        m--;
                        continue;
                    }

                    numUsati.add(numero);

                    bottoniNum[k][m] = new JButton(String.valueOf(numero));
                    caselleDaRiemp--;
                } else {
                    bottoniNum[k][m] = new JButton();
                    bottoniNum[k][m].setBackground(Color.WHITE);
                }

                bottoniNum[k][m].setEnabled(false);
                bottoniNum[k][m].setBackground(new Color(255, 255, 255));
                pCaselle.add(bottoniNum[k][m]);
            }
        }
        c.add(pCaselle, BorderLayout.CENTER);
    }

    public String getNumEstratti() {
        return serverIO(this, host+"/tombola/tombola.php?func=getNumeri&nick="+nick);
    }

    public void abilitaCaselle(String numEstratti) {
        if(numEstratti != null && numEstratti.length() > 0) {
            String[] split = numEstratti.substring(0, numEstratti.length()-1).split(",");

            for(int k = 0; k < 3; k++) {
                for(int m = 0; m < 9; m++) {
                    for(String s : split) {
                        if(bottoniNum[k][m].getText().equals(s)) {
                            bottoniNum[k][m].setEnabled(true);
                            bottoniNum[k][m].setBackground(coloreNumeriUsciti);
                        }
                    }
                }
            }
        }
    }

    public void controlloSessione() {
        int retVal = Integer.parseInt(serverIO(this, host+"/tombola/tombola.php?func=existSession&nick="+nick));
        if (retVal == -1) {
            JOptionPane.showMessageDialog(this, "La sessione non esiste più");
            System.exit(0);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(!nickTxt.getText().equals("")) {
            int retVal = Integer.parseInt(serverIO(this, host+"/tombola/tombola.php?func=existSession&nick="+nickTxt.getText()));
            if (retVal == -1) JOptionPane.showMessageDialog(this, "Sessione inesistente");
            if (retVal == 0) {
                nick = nickTxt.getText();
                JOptionPane.showMessageDialog(this, "Ti sei unito alla sessione!");
                remove(panUnisciSessione);
                controlloNCaselle();
                aggiungiWestEast(this);
                aggiungiSouthNorth(this);
                aggiungiCaselle(this);
                setBounds(30, 30, 600, 200);
                this.addKeyListener(this);

                ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
                Runnable eseguibile = () -> {
                    controlloSessione();
                    abilitaCaselle(getNumEstratti());
                };
                ScheduledFuture<?> scheduledFuture = ses.scheduleAtFixedRate(eseguibile, 0, 5, TimeUnit.SECONDS);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Inserisci il nome della sessione!");
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        easterEggTxt += e.getKeyChar();
        //System.out.println(easterEggTxt);
        //------------ Easter Egg GTA ----------------------------
        if(easterEggTxt.toLowerCase().contains("bandit")) {
            JOptionPane.showMessageDialog(this, "E' spawnata una BMX!");
            easterEggTxt = "";
        }
        else if(easterEggTxt.toLowerCase().contains("buzzof")) {
            JOptionPane.showMessageDialog(this, "E' spawnato un elicottero!");
            easterEggTxt = "";
        }
        else if(easterEggTxt.toLowerCase().contains("slowmo")) {
            JOptionPane.showMessageDialog(this, "Si sta rallentando tutto...");
            easterEggTxt = "";
        }
        else if(easterEggTxt.toLowerCase().contains("skyfall")) {
            JOptionPane.showMessageDialog(this, "Stai cadendo dal cielo!");
            easterEggTxt = "";
        }
        //---------------------------------------------------------
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}