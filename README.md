# Eriantys

![ci badge](https://github.com/alexbradd/ingsw2022-AM36/actions/workflows/java-ci.yaml/badge.svg)

<img
  src="https://www.craniocreations.it/wp-content/uploads/2021/06/Eriantys_scatolaFrontombra-600x600.png"
  width="320px"
  height="320px"
  align="left" />

Eriantys is a board game created by Cranio Creations. This is a Java
implementation of the game, created as the final test of the "Software
Engineering" course held at Politecnico di Milano (2021/2022).

- Assigned professor: Alessandro Margara
- Final score: 30L

## Project specification

The project consists of an implementation of a distributed system made of a
single server and multiple clients that can participate in matches between each
other. The server and clients will communicate using sockets and the TCP-IP
protocol. The client will provide both a CLI and GUI interface.

The final project will include:

1. The initial UML diagram;
2. The final UML diagram, generated from the code;
3. Rule conforming implementation of the specifications;
4. Documentations of network protocol used;
5. Peer review documents;
6. Source code of client and server applications;
7. Source code of unit tests;

## Implemented functionalities

| Functionality  |                          Status                           |
|:---------------|:---------------------------------------------------------:|
| Basic rules    | [🟢](https://github.com/alexbradd/ingsw2022-AM36/pull/32) |
| Complete rules | [🟢](https://github.com/alexbradd/ingsw2022-AM36/pull/32) |
| Socket         | [🟢](https://github.com/alexbradd/ingsw2022-AM36/pull/45) |
| GUI            | [🟢](https://github.com/alexbradd/ingsw2022-AM36/pull/50) |
| CLI            | [🟢](https://github.com/alexbradd/ingsw2022-AM36/pull/46) |
| Multiple games | [🟢](https://github.com/alexbradd/ingsw2022-AM36/pull/45) |
| Persistence    | [🟢](https://github.com/alexbradd/ingsw2022-AM36/pull/53) |
| Resilience     |                            🔴                             |
| All characters | [🟢](https://github.com/alexbradd/ingsw2022-AM36/pull/33) |
| 4 player mode  |                            🔴                             |

Legend:

- 🔴: Not yet implemented/not planned
- 🟡: Being worked on atm
- 🟢: Implemented

## Coverage report

Coverage report as reported by our IDE (Intellij IDEA):

| Class% | Method% | Line% |
|:------:|:-------:|:-----:|
|  98%   |   90%   |  89   |

## Command line options documentation

The provided executable will accept the following command line options:

- `--server`: runs the executable in server mode (if omitted, the executable 
  will run in this mode)
- `--client-cli`: runs the executable in textual mode
- `--client-gui`: runs the executable in graphical mode
- `--port [PORT]`: listen/connect to this port (default is *9999*)
- `--address [IP]`: used only in client mode, connect to this address (default
  is *localhost*)
- `--persistence-store [PATH]`: use this path to store persistence files
  (default is *./eryantis-store*)
- `--no-persistence`: disable persistence
- `--no-ping`: disable server pinging during a game (see protocol documentation)
- `--max-ping [MS]`: used in server mode, set the maximum time in milliseconds
  to wait for clients to respond to PING messages (see protocol documentation)
  (default is *1000*, choose a value between 100 and 3000) 
- `--client-socket-timeout [MS]`: used in client mode, set the socket timeout
  (default is *10000*)
- `--connectivity-check-interval [MS]`: used in client mode, set the amount of
  milliseconds between each HEARTBEAT message (default is *5000*)
- `--verbose`: use more verbose output

## Note about the documentation

We have included in the project's deliverables both the documentation as
presented in the various laboratory meetings and the updated the same documents
updated with later changes (peer-reviews or other changes made during
development). The updated documentation is located in `docs/updated` (source)
and `deliverables/updated`.

## The team

- [Alexandru Gabriel Bradatan](https://github.com/alexbradd)
- [Leonardo Bianconi](https://github.com/leo-bianconi)
- [Mattia Busso](https://github.com/mattia-busso)

## Software used

- Draw.io: UML diagrams
- Intellij IDEA Ultimate: Java IDE
- Maven: build system

## Copyright and licenses

Graphic assets have been provided by Cranio Creations SRL. Eriantys is a
Cranio Creations SRL product, all rights belong to their respective owners.

The code is licensed under the [MIT license](https://github.com/alexbradd/ingsw2022-AM36/blob/main/LICENSE).
You may not use this software except in compliance with the license.
