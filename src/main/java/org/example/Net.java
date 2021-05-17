package org.example;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.PasswordAuthentication;

import java.io.*;
import java.net.*;
import java.util.Properties;

public class Net {
    public static void main(String[] args) {
        InetAddress currentIp;
        InetAddress epamIp;
        // setting an IP address as an array
        byte ip[] = {(byte) 217, (byte) 21, (byte) 43, (byte) 3};
        try {
            currentIp = InetAddress.getLocalHost();
            // output IP address of local computer
            System.out.println("current IP -> " + currentIp.getHostAddress());
            epamIp = InetAddress.getByName("epam.com");
            System.out.println("EPAM IP address -> " + epamIp.getHostAddress());
            InetAddress addr = InetAddress.getByAddress("bsu.by", ip);
            System.out.println(addr.getHostName() + "-> connection: " + addr.isReachable(1_000));
        } catch (UnknownHostException e) {
            System.err.println("address unavailable: " + e);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("I/O exception: " + e);
        }

        String urlName = "https://logging.apache.org/log4j/2.x/download.html";
        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(new URL(urlName).openStream()))) {
            URL url = new URL(urlName);
            System.out.println("protocol: " + url.getProtocol());
            System.out.println("host: " + url.getHost());
            System.out.println("port: " + url.getDefaultPort());
            System.out.println("file: " + url.getFile());
//            reader.lines().forEach(System.out::println); // reading content
        } catch (MalformedURLException e) {
            e.printStackTrace();// incorrectly set protocol, domain name or file path
        } catch (IOException e) {
            e.printStackTrace();
        }

        String urlName1 = "http://www.google.com";
        int timeout = 10_000_000;
        try {
            URL url = new URL(urlName1);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(timeout); //set timeout for connection connection.connect();
            System.out.println(urlName1 + "::content type:"+ connection.getContentType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/* tcp/ip Socket server and client one threaded */
class SimpleServerSocket {
    public static void main(String[] args) {
        System.out.println("server starts");
        try (ServerSocket serverSocket = new ServerSocket(2048);
            Socket socket = serverSocket.accept();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            System.out.println("Connection from " + socket + "!");
            writer.println("java tutorial"); // put string "java tutorial" into the buffer
            writer.flush(); // send the contents of the buffer to the client

            String message = reader.readLine();
            System.out.println("message received at Server: " + message + " at " + InetAddress.getLocalHost());

        } catch (IOException e) {
            System.err.println("IO error connection: " + e);
        }
        System.out.println("server is closed");
    }
}

class SimpleClientSocket {
    public static void main(String[] args) {
        try (Socket socket = new Socket(InetAddress.getLocalHost(), 2048);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            System.out.println("Client Connected!");
            String message = reader.readLine();
            System.out.println("message received at Client: " + message + " at " + InetAddress.getLocalHost());
            writer.println("Client uses java tutorial too"); // put string "java tutorial" into the buffer
            writer.flush(); // send the contents of the buffer to the client
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("client is closed");
    }
}
/* End tcp/ip socket server and client one threaded */


/* tcp/ip Socket server and client multithreaded */
class NetServerThreadMain {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8071);
            System.out.println(serverSocket.getInetAddress() + " server started");
            while (true) { //Чтобы процесс не завершался когда сокет закроется
                Socket socket = serverSocket.accept(); // waiting for a new client
                System.out.println(socket.getInetAddress().getHostName() + " connected");
                // create a separate stream for data exchange with the connected client
                ServerThread thread = new ServerThread(socket);
                thread.start();
                System.out.println(thread.getName() + ":  new ServerThread(socket) started");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ServerThread extends Thread {
    private PrintStream printStream; // send
    private BufferedReader reader; // receive
    private InetAddress address; // client address
    public ServerThread(Socket socket) {
        try {
            printStream = new PrintStream(socket.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        address = socket.getInetAddress();
    }
    public void run() {
        int counter = 0;
        String message;
        try {
            while ((message = reader.readLine()) != null) {
                if ("PING".equals(message)) {
                    printStream.println("PONG #" + ++counter);
                }
                System.out.println("PING-PONG #" + counter + " from " + address.getHostName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            disconnect();
            System.out.println(currentThread().getName() + " " + currentThread().getState());

        }
    }
    private void disconnect() {
        if (printStream != null) {
            printStream.close();
        } try {
            if (reader != null) {
                reader.close();
            }
            System.out.println(address.getHostName() + ": disconnected");
            Thread.currentThread().interrupt(); //? непонятно, поток исчез или нет
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class NetClientMain {
    final static int TIMEOUT_IN_MILLIS = 1_000;
    final static int MAX_PING = 10;
    final static String ORIGINAL_MESSAGE = "PING";
    public static void main(String[] args) {
        try (Socket socket = new Socket(InetAddress.getLocalHost(), 8071);
             PrintStream printStream = new PrintStream(socket.getOutputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            for (int i = 0; i < MAX_PING; i++) {
                printStream.println(ORIGINAL_MESSAGE);
                System.out.println(Thread.currentThread().getName() + ": " + reader.readLine());
                Thread.sleep(TIMEOUT_IN_MILLIS);
            }
        } catch (UnknownHostException e) {
            System.err.println("Connection refused:" + e); // not connect to the server
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
/* End tcp/ip socket server and client multithreaded */

/* UDP socket server and client */
class UDPRecipientMain {
    final static int WAIT_TIMEOUT = 60_000;
    public static void main(String[] args) {
        File file = new File("data/DatzikBass.LOFI-copy.mp3");
        System.out.println("receiving data ...");
        acceptFile(file, 8033, 1024);
        System.out.println("data reception completed");
    }
    private static void acceptFile(File file, int port, int pacSize) {
        byte data[] = new byte[pacSize];
        DatagramPacket packet = new DatagramPacket(data, data.length);
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            DatagramSocket datagramSocket = new DatagramSocket(port);
            /* setting the timeout: if within 60 seconds no packets were received, data reception ends */
            datagramSocket.setSoTimeout(WAIT_TIMEOUT);
            while (true) {
                datagramSocket.receive(packet);
                outputStream.write(data);
                outputStream.flush(); }
        } catch (SocketTimeoutException e) {
            System.err.println("Timed out, data reception is finished:" + e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class UDPSenderMain {
    public static void main(String[] args) {
        String fileName = "data/DatzikBass.LOFI.mp3";
        try (FileInputStream inputStream = new FileInputStream(new File(fileName))) {
            byte[] data = new byte[1024];
            DatagramSocket datagramSocket = new DatagramSocket();
            InetAddress address = InetAddress.getLocalHost();
            DatagramPacket packet;
            System.out.println("sending file...");
            while (inputStream.read(data) != -1) {
                packet = new DatagramPacket(data, data.length, address, 8033);
                datagramSocket.send(packet);// data sending
            }
            System.out.println("file sent successfully.");
        } catch (UnknownHostException e) {
            e.printStackTrace(); // invalid recipient address
        } catch (SocketException e) { // errors during data transfer
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
/* End UDP socket server and client */

/* Java Mail */
class MailMain {
    public static void main(String[] args) {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("data/mail.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(properties);
        String mailTo = "ft2-nospam@mail.ru";
        String subject = "Sample Mail";
        String body = "Hello java mail";
        MailSender sender = new MailSender(mailTo, subject, body, properties);
        sender.send();
    }
}
class MailSender {
    private MimeMessage message;
    private String sendToEmail;
    private String mailSubject;
    private String mailText;
    private Properties properties;
    public MailSender(String sendToEmail, String mailSubject, String mailText, Properties properties) {
        this.sendToEmail = sendToEmail;
        this.mailSubject = mailSubject;
        this.mailText = mailText;
        this.properties = properties;
    }

    public void send() {
        try {
            initMessage();
            Transport.send(message); // sending mail
        } catch (AddressException e) {
            System.err.println("Invalid address: " + sendToEmail + " " + e); // in log
        } catch (MessagingException e) {
            System.err.println("Error generating or sending message: " + e); // in log
        }
    }

    private void initMessage() throws MessagingException {
    // mail session object
    Session mailSession = SessionFactory.createSession(properties);
    mailSession.setDebug(true);
    message = new MimeMessage(mailSession); // create a mailing object
    // loading parameters into the mail message object
    message.setSubject(mailSubject);
    message.setContent(mailText, "text/html");
    message.setRecipient(Message.RecipientType.TO, new InternetAddress(sendToEmail));
    }
}

class SessionFactory {
    public static Session createSession(Properties configProperties) {
        String userName = configProperties.getProperty("mail.user.name");
        String userPassword = configProperties.getProperty("mail.user.password");
        return Session.getDefaultInstance(configProperties,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(userName, userPassword); }
            }
        );
    }
}
/* End of Java Mail */
