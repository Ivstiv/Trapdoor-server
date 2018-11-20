package data;

import com.google.gson.JsonObject;
import communication.AbstractHandler;
import communication.Request;
import communication.RequestType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Channel {

    private final ChannelType type;
    private final String name, password;
    private final Set<AbstractHandler> clients = new HashSet<>();

    public Channel(ChannelType type, String name, String password) {
        this.type = type;
        this.name = name;
        this.password = password;
    }

    public void addClient(AbstractHandler client) {
        clients.add(client);
        broadcastPrint("&1&a[Server]&g"+client.getUsername()+" has joined!");
    }

    public void removeClient(String username) {
        for(AbstractHandler client : clients) {
            if(client.getUsername().equals(username)) {
                clients.remove(client);
                return;
            }
        }
        System.out.println("[WARNING]Trying to remove a non-existing handler from channel '"+name+"':"+username);
    }

    public void broadcastMsg(AbstractHandler client, String message) {
        JsonObject content = new JsonObject();
        content.addProperty("sender", client.getUsername());
        content.addProperty("message", message);
        Request req = new Request(RequestType.MSG, content);
        for(AbstractHandler c : clients) {
            if(!c.equals(client)) // send to everyone except to him
                c.sendRequest(req);
        }
    }

    public void broadcastPrint(String message) {
        JsonObject content = new JsonObject();
        content.addProperty("action", "print");
        content.addProperty("message", message);
        Request req = new Request(RequestType.ACTION, content);
        for(AbstractHandler client : clients) {
            client.sendRequest(req);
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

    public Set<AbstractHandler> getClients() {
        return Collections.unmodifiableSet(clients);
    }
}
