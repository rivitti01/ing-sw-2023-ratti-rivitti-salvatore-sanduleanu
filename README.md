# ing-sw-2023-Ratti-Rivitti-Salvatore-Sanduleanu
Prova finale di Ingegneria del Software Polimi AA 2022/2023

![MyShelfie](https://www.craniocreations.it/storage/media/products/54/112/My_Shelfie_box_ITA-ENG.png)

## SERVER

```
java -jar softeng-gc30-1.0-SNAPSHOT-jar-with-dependencies [ipAddress] [socketPort] [rmiPort]
```
Esempio
```
java -jar softeng-gc30-1.0-SNAPSHOT-jar-with-dependencies 192.168.1.50 2000 2099
```


## CLIENT

```
java -jar softeng-gc30-1.0-SNAPSHOT-jar-with-dependencies [G/T] [R/S] [ipAddress] [socketPort] [rmiPort]
```

Esempio con interfaccia grafica e protocollo socket
```
java -jar softeng-gc30-1.0-SNAPSHOT-jar-with-dependencies G S 192.168.1.50 2000 2099
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

## NOTA
My Shelfie è un gioco da tavolo sviluppato ed edito da Cranio Creations Srl. I contenuti grafici di questo progetto riconducibili al prodotto editoriale da tavolo sono utilizzati previa approvazione di Cranio Creations Srl a solo scopo didattico. È vietata la distribuzione, la copia o la riproduzione dei contenuti e immagini in qualsiasi forma al di fuori del progetto, così come la redistribuzione e la pubblicazione dei contenuti e immagini a fini diversi da quello sopracitato. È inoltre vietato l'utilizzo commerciale di suddetti contenuti.

## Componenti del gruppo
- [__Leonardo Ratti__](https://github.com/LRatti)
- [__Francesco Rivitti__](https://github.com/rivitti01)
- [__Alessandro Salvatore__](https://github.com/SAAL01)
- [__Denis Sanduleanu__](https://github.com/DenSandu)
