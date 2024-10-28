package server.server;

import server.client.ClientGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class ServerWindow extends JFrame {
    private static final int POS_X = 500;
    private static final int POS_Y = 550;
    private static final int WIDTH = 400;
    private static final int HEIGHT = 300;
    private static final String LOG_FILE = "chat_history.txt";
    
    private boolean isServerWorking = false;
    private JButton startButton;
    private JButton stopButton;
    private JTextArea log;
    private JLabel statusLabel;
    private List<ClientGUI> clients;
    
    public ServerWindow() {
        clients = new ArrayList<>();
        setTitle("Управление сервером");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Инициализация компонентов
        log = new JTextArea();
        log.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(log);
        
        startButton = new JButton("Запустить сервер");
        stopButton = new JButton("Остановить сервер");
        statusLabel = new JLabel("Статус сервера: Остановлен");
        
        // Обработчики событий
        startButton.addActionListener(e -> {
            if (!isServerWorking) {
                isServerWorking = true;
                updateStatus();
                loadChatHistory(); // Загружаем историю при запуске сервера
            }
        });
        
        stopButton.addActionListener(e -> {
            if (isServerWorking) {
                isServerWorking = false;
                updateStatus();
            }
        });
        
        // Размещение компонентов
        setLayout(new BorderLayout());
        setBounds(POS_X, POS_Y, WIDTH, HEIGHT);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        setVisible(true);
    }
    
    public void registerClient(ClientGUI client) {
        clients.add(client);
    }
    
    public boolean isServerWorking() {
        return isServerWorking;
    }
    
    public void processMessage(String message, ClientGUI sender) {
        if (!isServerWorking) {
            return;
        }
        
        String formattedMessage = sender.getLogin() + ": " + message;
        log.append(formattedMessage + "\n");
        saveToFile(formattedMessage);
        
        // Отправка сообщения всем клиентам
        for (ClientGUI client : clients) {
            client.receiveMessage(formattedMessage);
        }
    }
    
    private void saveToFile(String message) {
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            writer.write(message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadChatHistory() {
        File file = new File(LOG_FILE);
        if (!file.exists()) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.append(line + "\n");
                // Отправляем историю всем подключенным клиентам
                for (ClientGUI client : clients) {
                    client.receiveMessage(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void updateStatus() {
        statusLabel.setText("Статус сервера: " + (isServerWorking ? "Работает" : "Остановлен"));
    }
}