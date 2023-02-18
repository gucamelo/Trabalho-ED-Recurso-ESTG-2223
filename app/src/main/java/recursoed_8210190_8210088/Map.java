package recursoed_8210190_8210088;

import Exceptions.InvalidValue;
import Lists.ArrayList;
import Lists.LinearNode;
import Lists.Network;

public class Map {
    private Network<Local> Network;
    private PlayerList players;
    private int totalLocals;
    private ArrayList<ArrayList<Local>> giantsPath;
    private ArrayList<ArrayList<Local>> sparksPath;

    public Map() {
        this.Network = new Network<Local>();
        this.totalLocals = 0;
        this.players = new PlayerList();
        this.giantsPath = new ArrayList<ArrayList<Local>>();
        this.sparksPath = new ArrayList<ArrayList<Local>>();
    }

    public int getTotalLocals() {
        return totalLocals;
    }

    public void addLocal(Local local) {
        this.Network.addVertex(local);
        this.totalLocals++;
    }

    public void addConnection(Local local1, Local local2, double weight) {
        this.Network.addEdge(local1, local2, weight);
    }

    public void addPlayer(Player player) {
        this.players.addPlayer(player);
    }

    public ArrayList<Local> getGiantsPath(Local local) {
        int index = this.Network.indexOf(local);
        return giantsPath.get(index);
    }

    public ArrayList<Local> getSparksPath(Local local) {
        int index = this.Network.indexOf(local);
        return sparksPath.get(index);
    }

    public void addGiantsTunel(Local local1, Local local2) {
        int index1 = this.Network.indexOf(local1);
        int index2 = this.Network.indexOf(local2);
        giantsPath.get(index1).add(local2);
        giantsPath.get(index2).add(local1);
    }

    public void addSparksTunel(Local local1, Local local2) {
        int index1 = this.Network.indexOf(local1);
        int index2 = this.Network.indexOf(local2);
        sparksPath.get(index1).add(local2);
        sparksPath.get(index2).add(local1);
    }

    public void editLatLong(Local local, double latitude, double longitude) throws InvalidValue {
        if(latitude < -90 || latitude > 90)
            throw new InvalidValue("Latitude out of bounds");
        else
            local.setLatitude(latitude);
        if(longitude < -180 || longitude > 180)
            throw new InvalidValue("Longitude out of bounds");
        else
            local.setLongitude(longitude);
   }

    public void editEnergy(Local local, double energy) throws InvalidValue{
         if(energy >= 0)
              local.setEnergy(energy);
         else
              throw new InvalidValue("Energy cannot be negative");
    }

    public void editMaxEnergy(Portal portal, double maxEnergy) throws InvalidValue{
        if(maxEnergy >= 0)
            portal.setMaxEnergy(maxEnergy);
        else
            throw new InvalidValue("Max Energy cannot be negative");
    }

    public void editName(Portal portal, String name) {
        portal.setName(name);
    }

    public void editCooldown(Connector connector, int cooldown) throws InvalidValue{
        if(cooldown >= 0)
            connector.setCooldown(cooldown);
        else
            throw new InvalidValue("Cooldown cannot be negative");
    }

    public void removeLocal(Local local) {
        this.Network.removeVertex(local);
        this.totalLocals--;
    }

    public void removeConnection(Local local1, Local local2) {
        this.Network.removeEdge(local1, local2);
    }

    public ArrayList<Portal> getPortals() {
        ArrayList<Portal> portals = new ArrayList<Portal>();
        for (int i = 0; i < this.Network.getVertices().size(); i++) {
            if (this.Network.getVertices().get(i) instanceof Portal) {
                portals.add((Portal) this.Network.getVertices().get(i));
            }
        }
        return portals;
    }

    public ArrayList<Local> getLocals() {
        return this.Network.getVertices();
    }

    public ArrayList<Connector> getConnectors() {
        ArrayList<Connector> connectors = new ArrayList<Connector>();
        for (int i = 0; i < this.Network.getVertices().size(); i++) {
            if (this.Network.getVertices().get(i) instanceof Connector) {
                connectors.add((Connector) this.Network.getVertices().get(i));
            }
        }
        return connectors;
    }


    public void getConnectors25() {
        ArrayList<Connector> connectors = getConnectors();
        for (int i = 0; i < connectors.size()*0.25; i++) {
            int randomIndex = (int) (Math.random() * connectors.size());
            connectors.get(randomIndex).setMine(true);
        }  
    }

    public void playerSetLocal(Player player, Local local) {
        player.setLocal(local);
        if(local instanceof Connector) {
            Player otherPlayer = null;
            LinearNode<Player> node = players.getList().getHead();
            if(((Connector) local).isMine()) {
                player.removeEnergy((player.getEnergy()*0.5));
                ((Connector) local).setMine(false);
            }
            while(node != null) {
                if(node.getElement().getLocal() == local && node.getElement() != player) {
                    otherPlayer = node.getElement();
                }
                node = node.getNext();
            }
            LinearNode<TimeControl> node2 = ((Connector) local).getTimeControl().getHead();
            if(otherPlayer == null)
                return;
            if(otherPlayer.getEnergy() > player.getEnergy()) {
                while(node2 != null) {
                    if(node2.getElement().getPlayer().equals(player)) {
                        node2.getElement().setTime(System.currentTimeMillis());
                        break;
                    }
                    node2 = node2.getNext();
                }
            }
            else if(otherPlayer.getEnergy() < player.getEnergy()) {
                while(node2 != null) {
                    if(node2.getElement().getPlayer().equals(otherPlayer)) {
                        node2.getElement().setTime(System.currentTimeMillis());
                        break;
                    }
                    node2 = node2.getNext();
                }
            }
        }
    }

    public Local getLocalByID(int id) {
        for (int i = 0; i < this.Network.getVertices().size(); i++) {
            if (this.Network.getVertices().get(i).getId() == id) {
                return this.Network.getVertices().get(i);
            }
        }
        return null;
    }

    public ArrayList<Integer> nextLocation(Player player) {
        ArrayList<Integer> nextLocation = new ArrayList<Integer>();
        ArrayList<Local> neighbors = this.Network.getNeighbors(player.getLocal());
        for (int i = 0; i < neighbors.size(); i++) {
            nextLocation.add(neighbors.get(i).getId());
        }
        if(player.getTeam().equals("Giants")) {
            ArrayList<Local> giantsPath = getGiantsPath(player.getLocal());
            for(int i = 0; i < giantsPath.size(); i++) {
                if(!(nextLocation.contains(giantsPath.get(i).getId()))){
                    nextLocation.add(giantsPath.get(i).getId());
                }
            }
        }else if(player.getTeam().equals("Sparks")) {
            ArrayList<Local> sparksPath = getSparksPath(player.getLocal());
            for(int i = 0; i < sparksPath.size(); i++) {
                if(!(nextLocation.contains(sparksPath.get(i).getId()))){
                    nextLocation.add(sparksPath.get(i).getId());
                }
            }
        }
        return nextLocation;
    }
}
