> client-ds general process

implement in ds-client.c [1]

- initialise
- sends HELO to server
- waits for OK (HELO LOOP)
- if recieve ok: send AUTH
- waits for OK (AUTH LOOP)
- if recieve ok: send REDY after reading ds-system.xml [2]
- recieve server requests and job schedules
-

> Footnotes

1. Where is an example of ds-client.c? Just use the server as an example???
2. What does ds-system.xml do?

> project notes

- RUN Wireshark and see what the existing client/server do!
- Write in java, compile like in workshop example!

> command for demos
template:
> ./demoS1.sh [Java specific argument...] [-n] your_client.class [your client specific argument...]
implementation:
> ./demoS1.sh -n Main.class
