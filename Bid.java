//Lola

import java.time.*;

public class Bid {
	
	//The amount of money that a user has placed on a bid 
    private int price;
	//the user that has bidded on the item
	private Auctioneer client; 
	//The time is registered when the Bid object is created
    private LocalDateTime timeBidIsPlaced;

    public Bid(int price, Auctioneer client){
        this.price = price;
        this.client = client;
        this.timeBidIsPlaced = LocalDateTime.now();
    }

    public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public Auctioneer getClient() {
		return client;
	}

	public void setClient(Auctioneer client) {
		this.client = client;
	}

	public String getTimeBidIsPlaced() {
		return timeBidIsPlaced.getHour() + ":" + timeBidIsPlaced.getMinute()+":"+timeBidIsPlaced.getSecond();
	}

	public void setTimeBidIsPlaced(LocalDateTime timeBidIsPlaced) {
		this.timeBidIsPlaced = timeBidIsPlaced;
	}

    public String toString() {

		return "price = " + price + "client = " + client + ", timeBidIsPlaced = " + timeBidIsPlaced;
	}
}
