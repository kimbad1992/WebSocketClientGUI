package com.socketTest;

import com.google.gson.Gson;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WebSocketTestGUI {

    private static MyWebSocketClient client;
    private static JTextArea responseArea;
    private static Timer timer;
    private static boolean isSending = false;
    private static int repeatCount = 0; // 반복 횟수 카운트
    private static JTextField serverNameField; // serverName 입력 필드
    private static JTextField serviceLayerField; // serviceLayer 입력 필드

    public static void main(String[] args) {
        JFrame frame = new JFrame("WebSocket Test");
        frame.setSize(450, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        frame.add(panel);

        placeComponents(panel);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel urlLabel = new JLabel("URL:");
        urlLabel.setBounds(10, 20, 80, 25);
        panel.add(urlLabel);

        JTextField urlText = new JTextField(20);
        urlText.setBounds(100, 20, 300, 25);
        urlText.setText("http://3.37.10.250:8080/websocket/message");
        panel.add(urlText);

        JButton connectButton = new JButton("Connect");
        connectButton.setBounds(10, 50, 100, 25);
        panel.add(connectButton);

        serverNameField = new JTextField(20);
        serverNameField.setBounds(100, 80, 300, 25);
        serverNameField.setText("LEE_TEST");
        panel.add(serverNameField);

        JLabel serverNameLabel = new JLabel("Server Name:");
        serverNameLabel.setBounds(10, 80, 100, 25);
        panel.add(serverNameLabel);

        serviceLayerField = new JTextField(20);
        serviceLayerField.setBounds(100, 110, 300, 25);
        panel.add(serviceLayerField);

        JLabel serviceLayerLabel = new JLabel("Service Layer:");
        serviceLayerLabel.setBounds(10, 110, 100, 25);
        panel.add(serviceLayerLabel);

        // responseArea 초기화 및 배치
        responseArea = new JTextArea();
        responseArea.setBounds(10, 200, 360, 80); // 위치 및 크기 설정
        responseArea.setText("Reapeat Count : 0");
        panel.add(responseArea);

        JButton sendButton = new JButton("Send");
        sendButton.setBounds(10, 170, 100, 25);
        panel.add(sendButton);

        // Connect 버튼 ActionListener 수정
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (client == null || !client.isOpen()) {
                    try {
                        client = new MyWebSocketClient(new URI(urlText.getText()));
                        client.connect();
                        connectButton.setText("Disconnect");
                    } catch (URISyntaxException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    client.close();
                    connectButton.setText("Connect");
                    isSending = false;
                    sendButton.setText("Send");
                    if (timer != null) {
                        timer.stop();
                    }
                }
            }
        });


        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!isSending) {
                    isSending = true;
                    sendButton.setText("Sending...");

                    timer = new Timer(3000, new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            sendRandomMessage();
                        }
                    });
                    timer.start();
                } else {
                    isSending = false;
                    sendButton.setText("Send");
                    if (timer != null) {
                        timer.stop();
                    }
                }
            }
        });
    }

    private static void sendRandomMessage() {
        if (client != null && client.isOpen()) {
            Random rand = new Random();

            Map<String, String> messageMap = new HashMap<>();
            messageMap.put("serverName", serverNameField.getText());
            messageMap.put("serviceLayer", serviceLayerField.getText());
            messageMap.put("usememory", String.valueOf(rand.nextInt(100) + 1));
            messageMap.put("usedisk", String.valueOf(rand.nextInt(100) + 1));
            messageMap.put("cpu", String.valueOf(rand.nextDouble(100.0) + 1));
            messageMap.put("totalmemory", "32");

            String messageJson = new Gson().toJson(messageMap);

            client.send(messageJson);
            repeatCount++;
            responseArea.setText("Repeat Count: " + repeatCount);
        }
    }
}
