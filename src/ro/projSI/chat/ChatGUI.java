package ro.projSI.chat;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ChatGUI extends JFrame {
    private JTextArea chatArea;
    private JTextField inputField;
    private JComboBox<String> agentList;
    private ChatAgent myAgent;
    private Map<String, StringBuilder> conversations = new HashMap<>();

    public ChatGUI(ChatAgent a) {
        myAgent = a;
        setTitle("Agent: " + a.getLocalName());
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        //Chat History
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBackground(new Color(245, 245, 245));
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        //selectarea destinatarului si butoanele de control
        JPanel topPanel = new JPanel(new BorderLayout());
        agentList = new JComboBox<>();
        agentList.addActionListener(e -> refreshChatArea());

//panel butoane
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 5, 0)); // 1 rând, 2 coloane, 5px spațiu între ele

//butonul de inchidere al utilizatorului curent
        JButton closeUserBtn = new JButton("Ieșire");
        closeUserBtn.setBackground(new Color(0, 149, 255));
        closeUserBtn.setForeground(Color.WHITE);
        closeUserBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Închideți doar agentul curent?", "Ieșire", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                myAgent.doDelete();
            }
        });

//butonul de inchidere a sistemului
        JButton shutdownBtn = new JButton("Închidere Sistem");
        shutdownBtn.setBackground(new Color(0, 149, 255));
        shutdownBtn.setForeground(Color.WHITE);
        shutdownBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Sigur doriți închiderea TUTUROR agenților?", "Confirmare Generală", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                myAgent.broadcastShutdown();
            }
        });

        buttonsPanel.add(closeUserBtn);
        buttonsPanel.add(shutdownBtn);

        topPanel.add(new JLabel(" Trimite către: "), BorderLayout.WEST);
        topPanel.add(agentList, BorderLayout.CENTER);
        topPanel.add(buttonsPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        //input mesaj si trimitere mesaj
        JPanel bottomPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        JButton sendBtn = new JButton("Trimite Mesaj");
        sendBtn.setPreferredSize(new Dimension(120, 30));

        sendBtn.addActionListener(e -> {
            String target = (String) agentList.getSelectedItem();
            String text = inputField.getText().trim();
            if (target != null && !text.isEmpty()) {
                //apelam agentul fara a scrie ceva
                myAgent.sendMessage(target, text);
                inputField.setText("");
            }
        });

        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendBtn, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null); // Centrează fereastra pe ecran
    }

    public void addMessageToMap(String remoteAgent, String message) {
        conversations.putIfAbsent(remoteAgent, new StringBuilder());
        conversations.get(remoteAgent).append(message).append("\n");
        if (remoteAgent.equals(agentList.getSelectedItem())) {
            refreshChatArea();
        }
    }

    private void refreshChatArea() {
        String selectedAgent = (String) agentList.getSelectedItem();
        if (selectedAgent != null && conversations.containsKey(selectedAgent)) {
            chatArea.setText(conversations.get(selectedAgent).toString());

            // fix la scroll cand se trimit comenzi cu /
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        } else {
            chatArea.setText("");
        }
    }

    public void updateAgentList(String[] agents) {
        String current = (String) agentList.getSelectedItem();
        agentList.removeAllItems();
        for (String ag : agents) {
            if (!ag.equals(myAgent.getLocalName())) {
                agentList.addItem(ag);
            }
        }
        if (current != null) agentList.setSelectedItem(current);
    }

    public JComboBox<String> getAgentList() {
        return agentList;
    }
}