//Ardit

import java.util.*;

public class Auction {

    //The item that is being sold 
    private Item auctionItem;
    //The user thast is selling an item
    private Auctioneer seller;
    private LinkedList<Auctioneer> registeredAuctioneers = new LinkedList<Auctioneer>();
    //List of bids made in an auction. The highest bid, which also represents the newest bid, is always the first
    //element of the list
    private LinkedList<Bid> bids = new LinkedList<Bid>();
    //Timer object that, when given a task and time in milliseconds, executes the routine for auction end
    Timer timer;
    //Represents the action that will be performed when the timer is finished.
    TimerTask task;

    public Auction(Item auctionItem, Auctioneer seller) {
        this.auctionItem = auctionItem;
        this.seller = seller;

        timer = new Timer();
        long delay = auctionItem.getTime();
        //If the auction time refreshes on new bids, have a countdown when the timer is finished
        if(auctionItem.getCloseAuctionType() == 1){
            task = new TimerTask() {
                int i = 1;
    
                public void run() {
                    //Count down the last 5 seconds
                    MuTCPEchoServer.sendCountdown(registeredAuctioneers, auctionItem.getAuctionid(), String.valueOf(i));
                    i++;
                    
                    //ensure only 5 secods are counted
                    if (i > 5){
                        //Timer is over, cancel the timer and end the auction
                        MuTCPEchoServer.endAuction(getHighestBid(), registeredAuctioneers, auctionItem.getAuctionid());
                        timer.cancel();
                    }
                }
            };
            timer.scheduleAtFixedRate(task,delay*1000 , 1000);
        }else{
            //if the client has a fixed rate, it must not refresh upon new bids.
            task = new TimerTask() {
                public void run() {
                    MuTCPEchoServer.endAuction(getHighestBid(), registeredAuctioneers, auctionItem.getAuctionid());
                }
            };
            //set the timer 
            timer.schedule(task, delay * 1000);
        }
    }

    public Auction() {

    }

    public void register(Auctioneer auctioneer) {
        registeredAuctioneers.add(auctioneer);
    }
    //Adds a bid to the list of bids and refreshes the timer if necessary
    public void addBid(Bid bid) {
        bids.add(0, bid);
        if (auctionItem.getCloseAuctionType() == 1)
            updateTimer();
    }

    //Return the List of users registered to the Auction
    public LinkedList<Auctioneer> getRegisteredUsers() {
        return registeredAuctioneers;
    }
    
    //Returns the highest bid of an auction, or null if there are no bids yet.
    public Bid getHighestBid() {
        try {
            return bids.getFirst();
        } catch (Exception e) {
            return null;
        }
    }
    
    //Check if any user is bidding in the auction
    public boolean hasBidders() {
        return !bids.isEmpty();
    }

    // Creates a new Timer and Timertask, which updates the current timer
    // This method is called if the auction type is 1
    private void updateTimer() {
        timer.cancel();
        timer = new Timer();
         task = new TimerTask() {
            int i = 1;

            public void run() {
                System.out.println(i);
                MuTCPEchoServer.sendCountdown(registeredAuctioneers, auctionItem.getAuctionid(), String.valueOf(i));
                i++;

                if (i > 5){
                    MuTCPEchoServer.endAuction(getHighestBid(), registeredAuctioneers, auctionItem.getAuctionid());
                    timer.cancel();
                }
            }
        };
        timer.scheduleAtFixedRate(task,auctionItem.getTime()*1000 , 1000);
    }

    public Item getItem() {
        return auctionItem;
    }

    public int getStartingPrice() {
        return auctionItem.getStartingPrice();
    }

    public String toString() {
        return "auctionItem " + auctionItem;
    }

    public Auctioneer getSeller() {
        return seller;
    }

    //Removes all bids by a specific auctioneer. Returns true if the number of deletions is greater than 0, false if not.
    public boolean removeAllBids(Auctioneer auctioneer) {
        int numberOfDeletions = bids.size();
        bids.removeIf(b -> (b.getClient() == auctioneer));
        numberOfDeletions -= bids.size();
        return numberOfDeletions > 0;
    }

    //Check if the user is registered in the auction
    public boolean isRegistered(Auctioneer auctioneer) {
        for (Auctioneer au : registeredAuctioneers) {
            if (auctioneer.getIpAddress().equals(au.getIpAddress()))
                return true;
        }
        return false;
    }

}