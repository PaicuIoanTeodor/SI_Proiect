package ro.projSI.chat;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import java.util.Date;

public class AssistantAgent extends Agent {
    protected void setup() {
        System.out.println("Asistentul [" + getLocalName() + "] este gata de comenzi.");

        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String content = msg.getContent().trim().toLowerCase();
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);

                    System.out.println("Asistentul a primit comanda: " + content); // Debug în consolă

                    if (content.equalsIgnoreCase("/help")) {
                        reply.setContent("[ASISTENT]: Comenzi: /help, /status, /ora, /shutdown_all");
                    }
                    else if (content.equalsIgnoreCase("/status")) {
                        reply.setContent("[ASISTENT]: Platforma JADE este stabilă.");
                    }
                    else if (content.equalsIgnoreCase("/ora")) {
                        reply.setContent("[ASISTENT]: " + new java.util.Date().toString());
                    }
                    else if (content.equals("/shutdown_all") || content.equals("shutdown_all")) {
                        System.out.println("Asistentul inițiază procedura de oprire globală...");
                        broadcastShutdownSignal();
                        return;
                    }
                    else {
                        reply.setContent("[ASISTENT]: Comandă necunoscută. Scrie /help.");
                    }
                    send(reply);
                } else {
                    block();
                }
            }
        });
    }

    private void broadcastShutdownSignal() {
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("chat-p2p");
        template.addServices(sd);
        try {
            DFAgentDescription[] result = DFService.search(this, template);
            ACLMessage shutdown = new ACLMessage(ACLMessage.REQUEST);
            shutdown.setContent("SHUTDOWN_ALL");
            for (DFAgentDescription dfd : result) {
                shutdown.addReceiver(dfd.getName());
            }
            send(shutdown);
            doDelete();
        } catch (Exception e) { e.printStackTrace(); }
    }
}