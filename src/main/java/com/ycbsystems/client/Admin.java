/*
*****************************************************************************
Copyright (c) 2004
Yuri Cuervo
All Rights Reserved

Programmer: Yuri Cuervo
Created: Feb 8, 2004
*****************************************************************************
*/
package com.ycbsystems.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Vector;


public class Admin
        extends JApplet
{
    private static boolean bApplet = true;

    private Connection connDB;

    private static JTextField txtDomain;
    private JTextField txtDSN = new JTextField(10);

    private final String CONNECT = "Connect";
    @SuppressWarnings("FieldCanBeLocal")
    private final String DISCONNECT = "Disconnect";
    private JButton btnConnect = new JButton(CONNECT);

    private JLabel lblStatus = new JLabel("Disconnected");

    private JTextArea txtSQL = new JTextArea();

    private JButton btnExec = new JButton("Execute");

    //applet variables
    private JComboBox<String> cmbLang = new JComboBox<>(new String[]{"ASP", "PHP"});
    private String sHost;

    private static Font fntMono = new Font("Monospaced", Font.PLAIN, 11);
    private boolean bLoggedIn = false;


    public Admin(String sDSN)
    {
        this();
        connect(sDSN);
    }


    public Admin(int w, int h)
    {
        this();
        setSize(w, h);
    }


    public Admin()
    {
        // first
        Container cont = getContentPane();

        cont.setLayout(new BorderLayout());

        GridBagLayout gbTop = new GridBagLayout();
        JPanel pnlTop = new JPanel(gbTop);
        if (!bApplet)
        {
            gbAdd(gbTop, pnlTop, new JLabel("Domain:", JLabel.RIGHT), 0, 0, 1, 1, 1, 0, 11, 'H', 2, 2, 2, 2, 0, 0);
            gbAdd(gbTop, pnlTop, txtDomain, 1, 0, 2, 1, 1, 0, 11, 'H', 2, 2, 2, 2, 0, 0);
        }

        gbAdd(gbTop, pnlTop, new JLabel("DSN:", JLabel.RIGHT), 0, 1, 1, 1, 1, 0, 11, 'H', 2, 2, 2, 2, 0, 0);
        gbAdd(gbTop, pnlTop, txtDSN, 1, 1, 1, 1, 1, 0, 11, 'H', 2, 2, 2, 2, 0, 0);
        gbAdd(gbTop, pnlTop, cmbLang, 2, 1, 1, 1, 1, 0, 11, 'H', 2, 2, 2, 2, 0, 0);

        JPanel pnlButtons = new JPanel(new GridLayout(0, 2));
        pnlButtons.add(btnConnect);
        pnlButtons.add(btnExec);
        gbAdd(gbTop, pnlTop, pnlButtons, 0, 2, 3, 1, 1, 0, 11, 'H', 2, 2, 2, 2, 0, 0);


        cont.add(pnlTop, BorderLayout.NORTH);
        cont.add(new JScrollPane(txtSQL), BorderLayout.CENTER);
        cont.add(lblStatus, BorderLayout.SOUTH);

        txtDSN.setFont(fntMono);
        txtSQL.setFont(fntMono);

        addListeners();
    }


    private void connect(String sDSN)
    {
        if (!bLoggedIn)
        {
            JPasswordField txtPwd = new JPasswordField();
            JOptionPane.showMessageDialog(this,
                                          txtPwd,
                                          "Password",
                                          JOptionPane.QUESTION_MESSAGE);

            char cPwd[] = txtPwd.getPassword();

            if (cPwd != null)
            {
                String sInput = String.valueOf(cPwd);

                //this is a simple app just to play with... not worth dealing with real authentication
                if (sInput.equalsIgnoreCase("MyHardCodedPa$$word"))
                {
                    bLoggedIn = true;
                }
                else
                {
                    txtSQL.setText("Invalid Password. Access Denied!");
                    txtSQL.setEnabled(false);
                    return;
                }
            }
        }
        try
        {
            if (txtDomain != null)
            {
                sHost = txtDomain.getText().trim();
            }

            int iLen = sHost.length();
            if (sHost.endsWith("/"))
            {
                sHost = sHost.substring(0, iLen - 2);
            }

            if (!sHost.toLowerCase().endsWith("httpjdbc"))
            {
                sHost += "/httpjdbc";
            }
            String sURL = "HOST=" + sHost + ";" +
                          "DSN=" + sDSN + ";" +
                          "LANG=" + cmbLang.getSelectedItem();
            Class.forName("com.ycbsystems.httpjdbc.HttpJdbcDriver");
            connDB = DriverManager.getConnection(sURL);

            lblStatus.setText("Connected To: " + sDSN);
            btnConnect.setText(DISCONNECT);

            txtSQL.setEnabled(true);
            if (txtSQL.getText().equals("Invalid Password. Access Denied!"))
            {
                txtSQL.setText("");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }


    private void disconnect()
    {
        try
        {
            connDB.close();
            connDB = null;
            lblStatus.setText("Disconnected");
            btnConnect.setText(CONNECT);
            txtSQL.setEnabled(false);
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
        }
    }


    public void init()
    {
        //second only if applet
        int iW = 640;
        int iH = 480;

        String sParam = getParameter("width");
        if (sParam != null)
        {
            iW = Integer.parseInt(sParam);
        }

        sParam = getParameter("height");
        if (sParam != null)
        {
            iH = Integer.parseInt(sParam);
        }

        setSize(iW, iH);

        sParam = getParameter("host");
        if (sParam != null)
        {
            sHost = sParam;
        }

        if (sHost == null)
        {
            URL urlDocBase = getDocumentBase();

            if (urlDocBase != null)
            {
                sHost = urlDocBase.getProtocol() + "://" + urlDocBase.getHost();
            }
        }
    }


    private void addListeners()
    {
        btnConnect.addActionListener(e -> {
            if (btnConnect.getText().equals(CONNECT))
            {
                connect(txtDSN.getText());
            }
            else
            {
                disconnect();
            }
        });
        btnExec.addActionListener(e -> {
            LoadThread loader = new LoadThread(txtSQL.getText());
            new Thread(loader).start();
        });
        txtSQL.addKeyListener(new KeyAdapter()
        {
            public void keyPressed(KeyEvent e)
            {
                if (e.getModifiers() == KeyEvent.SHIFT_MASK &&
                    e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    btnExec.doClick();
                }
            }
        });
    }


    private Frame getMyFrame(Component cmp)
    {
        if (cmp == null)
        {
            return new Frame();
        }
        else if (cmp instanceof Frame)
        {
            return (Frame) cmp;
        }
        else
        {
            return getMyFrame(cmp.getParent());
        }
    }


    @SuppressWarnings("SameParameterValue")
    private static void gbAdd(GridBagLayout layout,
                              Container cont,
                              Component comp,
                              int x, int y,
                              int w, int h,
                              double wx, double wy,
                              int anchor, char fill,
                              int top, int left, int bottom, int right,
                              int padx, int pady)
    {
        GridBagConstraints cons = new GridBagConstraints();
        cons.gridx = x;
        cons.gridy = y;
        cons.gridwidth = w;
        cons.gridheight = h;
        cons.weightx = wx;
        cons.weighty = wy;
        switch (anchor)
        {
            case 0:
                cons.anchor = GridBagConstraints.CENTER;
            case 12:
                cons.anchor = GridBagConstraints.NORTH;
            case 1:
                cons.anchor = GridBagConstraints.NORTHEAST;
            case 3:
                cons.anchor = GridBagConstraints.EAST;
            case 5:
                cons.anchor = GridBagConstraints.SOUTHEAST;
            case 6:
                cons.anchor = GridBagConstraints.SOUTH;
            case 7:
                cons.anchor = GridBagConstraints.SOUTHWEST;
            case 9:
                cons.anchor = GridBagConstraints.WEST;
            case 11:
                cons.anchor = GridBagConstraints.NORTHWEST;
        }
        if (fill == 'N')
        {
            cons.fill = GridBagConstraints.NONE;
        }
        else if (fill == 'B')
        {
            cons.fill = GridBagConstraints.BOTH;
        }
        else if (fill == 'H')
        {
            cons.fill = GridBagConstraints.HORIZONTAL;
        }
        else if (fill == 'V')
        {
            cons.fill = GridBagConstraints.VERTICAL;
        }

        cons.insets = new Insets(top, left, bottom, right);
        cons.ipadx = padx;
        cons.ipady = pady;

        layout.addLayoutComponent(comp, cons);
        cont.add(comp);
    }


    private class LoadThread
            implements Runnable
    {
        String sSQL;


        LoadThread(String sSQL)
        {
            this.sSQL = sSQL;
        }


        public void run()
        {
            try
            {
                sSQL = sSQL.trim();

                if (sSQL.toUpperCase().indexOf("SELECT") == 0)
                {
                    ResultSet rs = connDB.createStatement().executeQuery(sSQL);

                    Vector<String> vctCols = new Vector<>();
                    Vector<Vector<Object>> vctRows = new Vector<>();
                    if (rs != null)
                    {
                        ResultSetMetaData md = rs.getMetaData();

                        int iCols = md.getColumnCount();

                        for (int i = 0; i < iCols; i++)
                        {
                            vctCols.add(md.getColumnName(i + 1));
                        }

                        while (rs.next())
                        {
                            Vector<Object> vctRow = new Vector<>();
                            for (int i = 0; i < iCols; i++)
                            {
                                Object objColValue = rs.getObject(i + 1);
                                vctRow.add(objColValue);
                            }
                            vctRows.add(vctRow);
                        }
                    }
                    else
                    {
                        vctCols.add("NO DATA FOUND");
                    }

                    JTable tblData = new JTable(vctRows, vctCols);

                    JDialog dlg = new JDialog(getMyFrame(Admin.this));
                    Container cont = dlg.getContentPane();
                    cont.setLayout(new BorderLayout());
                    cont.add(new JScrollPane(tblData), BorderLayout.CENTER);

                    dlg.setTitle("Result");
                    dlg.setSize(640, 480);
                    dlg.setLocation(200, 200);

                    dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    dlg.setVisible(true);
                }
                else
                {
                    int iRet = connDB.createStatement().executeUpdate(sSQL);

                    JOptionPane.showMessageDialog(Admin.this,
                                                  iRet + " record" + (iRet == 1 ? " was" : "s where") + " inserted, updated or deleted.");
                }
            }
            catch (Throwable t)
            {
                t.printStackTrace(System.err);
            }
        }
    }


    public static void main(String[] args)
    {
        bApplet = false;

        txtDomain = new JTextField("http://www.domain-name.com/httpjdbc");

        Admin admin = new Admin();

        JFrame fra = new JFrame("HttpJdbc Admin");

        Container cont = fra.getContentPane();

        cont.setLayout(new BorderLayout());

        cont.add(admin, BorderLayout.CENTER);

        fra.setSize(800, 600);

        fra.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        fra.setVisible(true);

        if (args.length == 1)
        {
            txtDomain.setText(args[0]);
        }

    }
}
