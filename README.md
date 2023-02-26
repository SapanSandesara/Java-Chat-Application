# JavaSocketChatApplication

I made this application for one of assignments in master of software development program at Victoria University of Wellington.

The application is made using Java. I am using Amazon AWS RDS mySQL instance for database. Messages are sent encrypted using DES and a secure login is
implemented.

Application features:

- Secure Login: passwords are sent from client to server using SSL sockets. Passwords are stored encrypted in AWS RDS mySQL instance using DES key stored on the server side. Password is encrypted before uploading to database. Password is decrypted on server level after retrieving from the database and checking against user input when user logs in.
- Encryption for messages: I am using DES encryption to encrypt messages between clients.
- Cloud Database: mySQL database to store the users and messages is hosted on the cloud. I am using Amazon AWS RDS mySQL instance.
- Threaded Server and Client
- GUI

Video of running application:

https://youtu.be/oFvU8UzIeiA
