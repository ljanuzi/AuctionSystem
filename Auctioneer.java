//Vlere
import java.util.*;

public class Auctioneer {

    private String ipAddress;
    // List of Auctions the user is registered on
    private LinkedList<Auction> registeredAuctions;
    // List of Auctions the user is bidding on
    private LinkedList<Auction> bidList;

    public Auctioneer(String ipAddress) {
        this.ipAddress = ipAddress;
        registeredAuctions = new LinkedList<Auction>();
        bidList = new LinkedList<Auction>();
    }

    public String getIpAddress() {
        return ipAddress;
    }

    // Adds an auction to the list of auctions the auctioneer is registered in
    public void register(Auction auction) {
        registeredAuctions.add(auction);
    }

    // Adds an auction to the list of bids of that auctioneer
    public void bid(Auction auction) {
        bidList.add(auction);
    }

    // Returns a list of the auctions the user is registered in
    public LinkedList<Auction> getRegisteredAuctions() {
        return registeredAuctions;
    }

    // Returns a list of the auctions the user has bid in
    public LinkedList<Auction> getBidList() {
        return bidList;
    }
}
