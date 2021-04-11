package tombola.Tabellone;

import tombola.Tombola;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class Tabellone extends Tombola implements ActionListener {
    private ArrayList<Integer> numEstrarre = new ArrayList<>();
    private String host = getHost();
    private final JButton[] bottoniNum = new JButton[90];
    private String numEstratti = "0,";
    private final String[] listaCaselle = new String[100];
    private JButton ultimoBottone;
    private String nick = "";
    private final Color coloreNumeriOff = Color.WHITE;
    private final Color coloreNumeroAttuale = new Color(59, 187, 12);
    private final Color coloreNumeriPassati = new Color(238, 169, 67);
    private final Color coloreBackground = getColoreBackground();
    private JTextField nickTxt;
    private JPanel creaSessione;

    public Tabellone() {
        super("Tombola - Tabellone");

        controlloVersione(this);

        creaSessione = new JPanel(new FlowLayout());
        JLabel nickLabel = new JLabel("Crea una sessione: ");
        creaSessione.add(nickLabel);
        nickTxt = new JTextField("", 20);
        creaSessione.add(nickTxt);
        JButton crea = new JButton("Crea sessione");
        crea.addActionListener(this);
        creaSessione.add(crea);
        add(creaSessione);
        pack();

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            if(nick.equals("")) {
                System.exit(0);
            } else {
                serverIO(null, host + "/tombola/tombola.php?func=delSession&nick=" + nick);
                System.exit(0);
            }
            }
        });
    }

    private void riempiNumEstrarre() {
        for(int k = 0; k < 90; k++) {
            numEstrarre.add(k);
        }
    }
    private void riempiListaCaselle() {
        for(int k = 1; k < 101; k++) {
            listaCaselle[k-1] = String.valueOf(k);
        }
    }

    private void aggiungiTabellone(Container c) {
        JPanel pTabellone = new JPanel();
        pTabellone.setBackground(coloreBackground);
        pTabellone.setLayout(new GridLayout(9, 10, 3, 3));

        for(int k = 0; k < 90; k++) {
            bottoniNum[k] = new JButton(String.valueOf(k+1));
            bottoniNum[k].setOpaque(true);
            bottoniNum[k].setEnabled(false);
            bottoniNum[k].setBackground(coloreNumeriOff);
            bottoniNum[k].setForeground(Color.BLACK);
            pTabellone.add(bottoniNum[k]);
        }

        c.add(pTabellone, BorderLayout.CENTER);
    }

    private void aggiungiSouthNorth(Container c) {
        JPanel pNorth = new JPanel();
        pNorth.setBackground(coloreBackground);
        c.add(pNorth, BorderLayout.NORTH);

        JPanel pSouth = new JPanel();
        pSouth.setBackground(coloreBackground);

        pSouth.add(new JLabel("Il nome della sessione è: "+ nick + "  |  "));
        JButton jb = new JButton("Estrai numero");
        jb.addActionListener(this);
        pSouth.add(jb);

        pSouth.add(new JLabel("  |  I giocatori possono aprire massimo "));
        JComboBox nCaselle = new JComboBox(listaCaselle);
        pSouth.add(nCaselle);
        pSouth.add(new JLabel(" cartelle"));

        nCaselle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if(!serverIO(c, host+"/tombola/tombola.php?func=setNCartelle&nick="+nick+"&n="+nCaselle.getSelectedItem()).equals("0")) showFinestra("e");
            }
        });

        c.add(pSouth, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String bPremuto = ((JButton)e.getSource()).getText();

        switch(bPremuto) {
            case "Estrai numero": {
                if (numEstrarre.size() == 0) return;

                Random rnd = new Random();
                int indice = rnd.nextInt(numEstrarre.size());

                int numero = numEstrarre.get(indice);
                numEstratti += (numero + 1) + ",";
                numEstrarre.remove(indice);

                if (ultimoBottone != null) {
                    ultimoBottone.setBackground(coloreNumeriPassati);
                }

                bottoniNum[numero].setEnabled(true);
                bottoniNum[numero].setBackground(coloreNumeroAttuale);
                ultimoBottone = bottoniNum[numero];
                if(!serverIO(this, host+"/tombola/tombola.php?func=setNumeri&nick="+nick+"&numeri="+numEstratti).equals("0")) showFinestra("e");
                break;
            }
            case "Crea sessione": {
                if(!nickTxt.getText().equals("")) {
                    nick = nickTxt.getText();
                    int retVal = Integer.parseInt(serverIO(this, host+"/tombola/tombola.php?func=newSession&nick="+nick));
                    if (retVal == -1) showFinestra("Sessione già esistente, inserisci un altro nick");
                    else if (retVal == -2) showFinestra("Impossibile connettersi al server");
                    else if (retVal == 0) {
                        showFinestra("Sessione creata con successo! Il nome della sessione è: " + nick);
                        remove(creaSessione);

                        riempiListaCaselle();
                        riempiNumEstrarre();
                        aggiungiWestEast(this);
                        aggiungiTabellone(this);
                        aggiungiSouthNorth(this);
                        setBounds(30, 30, 800, 600);
                    }
                } else {
                    showFinestra("Inserisci il nome della sessione!");
                }
                break;
            }
        }
    }
}