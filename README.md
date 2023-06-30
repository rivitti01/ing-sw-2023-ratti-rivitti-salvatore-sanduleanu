# ing-sw-2023-Ratti-Rivitti-Salvatore-Sanduleanu
Prova finale di Ingegneria del Software Polimi AA 2022/2023

![MyShelfie](https://www.craniocreations.it/storage/media/products/54/112/My_Shelfie_box_ITA-ENG.png)

## SERVER

```
java -jar softeng-gc30-1.0-SNAPSHOT-jar-with-dependencies [ipAddress] [socketPort] [rmiPort]
```

## CLIENT

```
java -jar softeng-gc30-1.0-SNAPSHOT-jar-with-dependencies [G/T] [R/S] [ipAddress] [socketPort] [rmiPort]
```

### Jar
IL Jar del progetto può essere scaricato al seguente link: [Jar](https://github.com/rivitti01/ing-sw-2023-ratti-rivitti-salvatore-sanduleanu/releases/download/MyShelfie/softeng-gc30-1.0-SNAPSHOT-jar-with-dependencies.jar).




## Funzionalità
### Funzionalità Sviluppate
- Regole Complete
- CLI
- GUI
- Socket
- RMI
- 2 FA (Funzionalità Avanzate):
    - __Resilienza:__ I giocatori disconnessi a seguito della caduta della rete o del crash del client, possono ricollegarsi e continuare la partita. Mentre un giocatore non è collegato, il gioco continua saltando i turni di quel giocatore. Se rimane attivo un solo giocatore, il gioco viene sospeso fino a che non si ricollega almeno un altro giocatore oppure scade un timeout che decreta la vittoria dell'unico giocatore rimasto connesso.
    - __Chat:__ Client e server devono offrire la possibilità ai giocatori coinvolti in una partita di chattare tra di loro, inviando messaggi (testuali) indirizzati a tutti i giocatori della partita o a un singolo giocatore.

### Coverage report
Al seguente link è possibile consultare il report della coverage dei test effettuati con Junit: [Report](https://github.com/rivitti01/ing-sw-2023-ratti-rivitti-salvatore-sanduleanu/blob/main/documents/image.png)


## Componenti del gruppo
- [__Leonardo Ratti__](https://github.com/LRatti)
- [__Francesco Rivitti__](https://github.com/rivitti01)
- [__Alessandro Salvatore__](https://github.com/SAAL01)
- [__Denis Sanduleanu__](https://github.com/DenSandu)
