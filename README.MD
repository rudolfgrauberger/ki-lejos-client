# ki-lejos-client
Das ist die Client-Anwendung für unsere LeJOS-Anwendung. Der Client kommuniziert über den TCP-Port 6789 mit dem Teil der Anwendung auf dem LeJOS.  Der Teil der auf dem LeJOS läuft ist unter https://github.com/mbiesenb/ki-lejos zu finden.

# Downloaden und Einrichten

## 1. Repository clonen
```bash
git clone https://github.com/rudolfgrauberger/ki-lejos-client.git
```

## 2. Das ausgecheckte Verzeichnis in IntelliJ **Öffnen**

## 3. Ggf. Maven manuell synchroniseren

*Hinweis:* Es kann vorkommen, dass IntelliJ nicht direkt nach dem Öffnen damit anfängt die Maven-Abhängigkeiten zu aktualiseren, dann das Synchronisieren manuell anstoßen.

![Sync maven](doc/img/SyncMaven.PNG)

## 4. Konfiguration anlegen

Einmal zu der LeJOSProgram.java-Datei navigieren und wie im Screenshot einmal das Ausführen.

![Add Config](doc/img/AddConfig.PNG)

Anschließend erscheint automatisch die Konfiguration (vgl. 1) und man kann wie gewohnt über die Schaltflächen (vgl. 2) das Programm ausführen oder Debuggen.

![Configuration](doc/img/Config.PNG)