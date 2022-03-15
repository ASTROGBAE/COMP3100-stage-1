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
