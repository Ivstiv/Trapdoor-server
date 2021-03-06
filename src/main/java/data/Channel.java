package data;

import com.google.gson.JsonObject;
import communication.ConnectionHandler;
import communication.Request;
import communication.RequestType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Channel {

    private final ChannelType type;
    private final String name, password;
    private final Set<ConnectionHandler> clients = ConcurrentHashMap.newKeySet();

    public Channel(ChannelType type, String name, String password) {
        this.type = type;
        this.name = name;
        this.password = password;
    }

    public void addClient(ConnectionHandler client) {
        clients.add(client);
    }

    public void removeClient(String username) {
        for(ConnectionHandler client : clients) {
            if(client.getClientData().getUsername().equals(username)) {
                clients.remove(client);
                return;
            }
        }
        System.out.println("[WARNING]Trying to remove a non-existing handler from channel '"+name+"':"+username);
    }

    public void broadcastMsg(ConnectionHandler client, String message) {
        JsonObject content = new JsonObject();
        content.addProperty("sender", client.getClientData().getUsername());
        content.addProperty("message", message);
        Request req = new Request(RequestType.MSG, content);

        String senderUsername = client.getClientData().getUsername();
        for(ConnectionHandler c : clients) {
            if(!c.getClientData().getBlockedUsernames().contains(senderUsername)) // send if the sender is not blocked
                    c.sendRequest(req);
        }
    }

    public void broadcastPrint(String message) {
        for(ConnectionHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void broadcastPrefixedPrint(String message) {
        for(ConnectionHandler client : clients) {
            client.sendPrefixedMessage(message);
        }
    }

    public ChannelType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public Set<ConnectionHandler> getClients() {
        return Collections.unmodifiableSet(clients);
    }
}
