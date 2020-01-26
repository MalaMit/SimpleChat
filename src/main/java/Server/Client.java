package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

public class Client implements Runnable {

    private String name;
    private String message;
    private DataOutputStream dos;
    private DataInputStream dis;

    public Client(String name, DataOutputStream dos, DataInputStream dis) {
        this.name = name;
        this.dos = dos;
        this.dis = dis;
    }

    @Override
    public void run() {
        try {
            while (true) {
                message = dis.readUTF();
                System.out.println(message);
                List<Client> entry = Server.getClients();
                for (Client cli : entry) {
                    DataOutputStream edos = cli.getDos();
                    edos.writeUTF(message);
                }
            }
        } catch (IOException E) {
            try {
                dis.close();
                dos.close();
                Server.setClients(Server.getClients().stream()
                        .filter(e -> {
                            if (!(e == this)) {
                                String exit_message = "{ \"name\" : \"" + "[ SERVER NOTICE ]" + "\", \"message\" : \"" + name + " Disconnected" + "\"}";
                                System.out.println(exit_message);
                                try {
                                    e.getDos().writeUTF(exit_message);
                                } catch (IOException err) {
                                    err.printStackTrace();
                                }
                            }
                            return !(e == this);
                        })
                        .collect(Collectors.toList()));

                System.out.println("[Current User : " + Server.getClients().size() + "]");

            } catch (IOException E2) {
                E2.printStackTrace();
            }
        }
    }

    public DataOutputStream getDos() {
        return dos;
    }
}
