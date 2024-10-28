package server.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import server.server.ServerWindow;

public class ClientGUI extends JFrame {
    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;

    private JTextArea log;
    private final JPanel panelTop;
    private final JTextField tfIPAddress;
    private final JTextField tfPort;
    private final JTextField tfLogin;
    private final JPasswordField tfPassword;
    private final JButton btnLogin;
    private final JPanel panelBottom;
    private final JTextField tfMessage;
    private final JButton btnSend;
    private final ServerWindow serverWindow;
    private boolean isConnected = false;

    public ClientGUI(ServerWindow serverWindow) {
        this.serverWindow = serverWindow;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(WIDTH, HEIGHT);
        setTitle("Chat Client");

        // Верхняя панель
        panelTop = new JPanel(new GridLayout(2, 3));
        tfIPAddress = new JTextField("127.0.0.1");
        tfPort = new JTextField("8189");
        tfLogin = new JTextField("User");
        tfPassword = new JPasswordField("pass");
        btnLogin = new JButton("Login");

        panelTop.add(tfIPAddress);
        panelTop.add(tfPort);
        panelTop.add(new JPanel());
        panelTop.add(tfLogin);
        panelTop.add(tfPassword);
        panelTop.add(btnLogin);
        add(panelTop, BorderLayout.NORTH);

        // Центральная панель с логом
        log = new JTextArea();
        log.setEditable(false);
        JScrollPane scrollLog = new JScrollPane(log);
        add(scrollLog, BorderLayout.CENTER);

        // Нижняя панель
        panelBottom = new JPanel(new BorderLayout());
        tfMessage = new JTextField();
        btnSend = new JButton("Send");
        panelBottom.add(tfMessage, BorderLayout.CENTER);
        panelBottom.add(btnSend, BorderLayout.EAST);
        add(panelBottom, BorderLayout.SOUTH);

        // Обработчики событий
        btnLogin.addActionListener(e -> connectToServer());
        
        btnSend.addActionListener(e -> sendMessage());
        
        tfMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        setVisible(true);
    }

    private void connectToServer() {
        if (serverWindow.isServerWorking()) {
            isConnected = true;
            serverWindow.registerClient(this);
            btnLogin.setEnabled(false);
            tfLogin.setEditable(false);
            tfPassword.setEditable(false);
            tfIPAddress.setEditable(false);
            tfPort.setEditable(false);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Не удалось подключиться к серверу. Сервер не запущен.",
                "Ошибка подключения",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendMessage() {
        if (!isConnected) {
            JOptionPane.showMessageDialog(this,
                "Необходимо подключиться к серверу",
                "Ошибка отправки",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String message = tfMessage.getText().trim();
        if (!message.isEmpty()) {
            serverWindow.processMessage(message, this);
            tfMessage.setText("");
        }
    }

    public void receiveMessage(String message) {
        log.append(message + "\n");
        log.setCaretPosition(log.getDocument().getLength());
    }

    public String getLogin() {
        return tfLogin.getText();
    }
}