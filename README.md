# SI_Proiect | P2P Chat Agents (JADE Framework)
1. Acest proiect implementează un sistem de comunicare de tip Peer-to-Peer (P2P) utilizând framework-ul JADE (Java Agent Development Framework). Sistemul permite comunicarea între agenți multipli, monitorizarea prin intermediul unui agent asistent.

- Fiecare agent utilizator (ChatAgent) își gestionează propria interfață grafică și istoric.
- Agenții se identifică dinamic folosind serviciul DF (Directory Facilitator), eliminând necesitatea unei liste statice de contacte.
- Un AssistantAgent "invizibil" interceptează comenzile de sistem (începând cu /) pentru a oferi suport (help, status, ora) și pentru a coordona oprirea sistemului.
- Toate mesajele de chat sunt salvate automat în fișiere text locale.
2. Instalare și cerințe pentru a rula aplicația
- Java JDK (minim versiunea 8).
- Biblioteca JADE: Fișierul jade.jar este inclus în proiect și doar trebuie adăugat la structura proiectului pentru a putea fi recunoscută (pentru Intellij: File->Project Structure->Libraries->New Project Library->Java->selectați librăria JADE din folder-ul lib) .
- IDE: Recomandat IntelliJ IDEA sau Eclipse (proiectul a fost făcut în Intellij).
3. Configurația Agenților
- 3 Agenți Chat: Andrei, Maria, Elena.
- 1 Agent Asistent: AsistentSistem.
4. Lansarea în Execuție
- Lansarea se face prin clasa principală MainLauncher.java.
