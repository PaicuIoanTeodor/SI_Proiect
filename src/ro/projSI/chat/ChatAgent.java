package ro.projSI.chat;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.ACLMessage;

import java.io.FileWriter;
import java.io.PrintWriter;

public class ChatAgent extends Agent {
    private ChatGUI gui;

    protected void setup() {
        // inregistrare in Directory Facilitator (DF)
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("chat-p2p");
        sd.setName(getLocalName());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (Exception e) { e.printStackTrace(); }

        gui = new ChatGUI(this);
        loadHistory();
        gui.setVisible(true);

        //Căutare periodică agenți în DF
        addBehaviour(new TickerBehaviour(this, 3000) {
            protected void onTick() {
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sdSearch = new ServiceDescription();
                sdSearch.setType("chat-p2p");
                template.addServices(sdSearch);
                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    String[] names = new String[result.length];
                    for (int i = 0; i < result.length; ++i) {
                        names[i] = result[i].getName().getLocalName();
                    }
                    gui.updateAgentList(names);
                } catch (Exception e) { e.printStackTrace(); }
            }
        });

        //Primire mesaje
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String content = msg.getContent();
                    String senderName = msg.getSender().getLocalName();

                    if (content.equalsIgnoreCase("SHUTDOWN_ALL")) {
                        doDelete();
                        return;
                    }

                    if (senderName.equals("AsistentSistem")) {
                        String currentTarget = (String) gui.getAgentList().getSelectedItem();
                        gui.addMessageToMap(currentTarget, content);
                    } else if (!senderName.equals(myAgent.getLocalName())) {
                        gui.addMessageToMap(senderName, senderName + ": " + content);
                        saveHistory(senderName + ": " + content);
                    }
                } else {
                    block();
                }
            }
        });
    }

    // modifica metoda sendMessage din ChatAgent.java
    public void sendMessage(String dest, String content) {
        if (content == null || content.trim().isEmpty()) return;

        if (content.startsWith("/")) {
            gui.addMessageToMap(dest, "Eu: " + content);

            // trimitere asistent
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.addReceiver(new jade.core.AID("AsistentSistem", jade.core.AID.ISLOCALNAME));
            msg.setContent(content);
            send(msg);

            return;
        }

        gui.addMessageToMap(dest, "Eu: " + content);

        // trimitere mesaj catre destinatar
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new jade.core.AID(dest, jade.core.AID.ISLOCALNAME));
        msg.setContent(content);
        send(msg);

        //salvarea persistenta in fisier
        saveHistory("Eu -> " + dest + ": " + content);
    }

    private void saveHistory(String line) {
        try (PrintWriter out = new PrintWriter(new FileWriter(getLocalName() + "_history.txt", true))) {
            out.println(line);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Adaugă în ChatAgent.java
    protected void takeDown() {
        try {
            DFService.deregister(this); // Dezînregistrare din DF
            if (gui != null) {
                gui.dispose(); // Închide fereastra
            }
            System.out.println("Agentul " + getLocalName() + " a fost oprit corect.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // În ChatAgent.java, metoda apelată de butonul de pe GUI
    public void broadcastShutdown() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("chat-p2p");
        template.addServices(sd);
        try {
            // Căutăm toți agenții înregistrări în DF
            DFAgentDescription[] result = DFService.search(this, template);
            ACLMessage shutdownMsg = new ACLMessage(ACLMessage.REQUEST); // Folosim REQUEST conform standardului
            shutdownMsg.setContent("SHUTDOWN_ALL"); // Semnal simplu, fără slash pentru sistem

            for (DFAgentDescription dfd : result) {
                shutdownMsg.addReceiver(dfd.getName());
            }
            send(shutdownMsg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadHistory() {
        java.io.File file = new java.io.File(getLocalName() + "_history.txt");
        if (!file.exists()) return; // Dacă nu există istoric, nu facem nimic

        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Presupunem formatul salvat: "Eu -> Maria: Salut" sau "Maria: Bună"
                if (line.contains("->")) {
                    // Mesaj trimis de mine: extragem destinatarul dintre "-> " și ":"
                    int start = line.indexOf("-> ") + 3;
                    int end = line.indexOf(":", start);
                    if (start > 2 && end > start) {
                        String remoteAgent = line.substring(start, end);
                        gui.addMessageToMap(remoteAgent, line);
                    }
                } else if (line.contains(":")) {
                    // Mesaj primit: extragem expeditorul de la început până la ":"
                    int end = line.indexOf(":");
                    if (end > 0) {
                        String remoteAgent = line.substring(0, end);
                        // Verificăm să nu fie "Eu" (în caz de formatări custom)
                        if (!remoteAgent.equals("Eu")) {
                            gui.addMessageToMap(remoteAgent, line);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Eroare la încărcarea istoricului: " + e.getMessage());
        }
    }


}

