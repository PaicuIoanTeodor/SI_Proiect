package ro.projSI.chat;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;

public class MainLauncher {
    public static void main(String[] args) {
        Runtime rt = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.GUI, "true");
        AgentContainer mainContainer = rt.createMainContainer(profile);

        try {
            // lansăm 3 agenți de tip utilizator
            mainContainer.createNewAgent("Andrei", "ro.projSI.chat.ChatAgent", null).start();
            mainContainer.createNewAgent("Maria", "ro.projSI.chat.ChatAgent", null).start();
            mainContainer.createNewAgent("Elena", "ro.projSI.chat.ChatAgent", null).start();

            // agentul asistent (fără GUI)
            mainContainer.createNewAgent("AsistentSistem", "ro.projSI.chat.AssistantAgent", null).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}