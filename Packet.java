//A. Ymeri, L. Davidovikj, L. Januzi, V. Januzi
import java.util.*;

public class Packet {

    static Map<String, Integer> packetSizes = new HashMap<String, Integer>();
    static {
        packetSizes.put("request", 1);
        packetSizes.put("name", 128);
        packetSizes.put("dscp", 512);
        packetSizes.put("price", 32);
        packetSizes.put("closeauction", 1);
        packetSizes.put("time", 16);
        packetSizes.put("id", 8);
    }
    //This array of characters holds the message that will be sent
    char[] message;
    //Holds the index where the next field will be added from.
    //This is updated whenever a new field is added on the message
    int latestIndex;

    //Ardit
    //Constructor that, when given a request type, creates the overall structure of the packet 
    //and fills it with empty characters. Size of the message depends on which fields it has.
    public Packet(int requestType) {
        
        //declare initial values
        int size = 0;
        latestIndex = 0;
        
        //A mapping of the different request types to the appropriate sizes
        switch (requestType) {
            case 1:
                size = packetSizes.get("name") + packetSizes.get("dscp") + packetSizes.get("price")
                        + packetSizes.get("closeauction") + packetSizes.get("time");
                break;
            // Case 2
            case 3:
                size = packetSizes.get("id");
                break;
            case 4:
                size = packetSizes.get("id") + packetSizes.get("price");
                break;
            case 5:
                size = packetSizes.get("id");
                break;
            case 6:
                size = packetSizes.get("id");
                break;
        }
        
        //Add the size of the packet, which is one character, to the size, for all the packet types.
        size += packetSizes.get("request");

        //Initialize the message array
        message = new char[size];
        //Fill the array with empty characters
        for (int i = 0; i < message.length; i++) {
            message[i] = ' ';
        }
        //Overwrite the first bit of the message by using the insert method defined below
        insert("request", String.valueOf(requestType));
    }

    //Lola
    /**Precondition: Type is a string referring to the name of a packet field (e.g. item name, item description).
     * The data parameter refers to the value of that field.
     * These strings can contain alphanumerical characters or can be empty. 
     * <br><br>
     * Postcondition: Returns false if length of the data is larger than the maximum for that field.
     * Returns True if the length of the data is smaller than the maximum, and adds the data to the message
     */
    public boolean insert(String type, String data) {
        if (data.length() > packetSizes.get(type)) {
            return false;
        } else {
            for (int i = latestIndex; i < latestIndex + data.length(); i++) {
                message[i] = data.charAt(i - latestIndex);
            }
            latestIndex += packetSizes.get(type);
            return true;
        }
    }
    
    //Vlera & Learta
    /** Precondition: Message is a string corresponding to the user request from the main menu <br><br>
     * Postcondition: Returns a hash map linking the key(field name) with a value (field value)
    */
    public static Map<String, String> parse(String message) {
        Map<String, String> map = new HashMap<String, String>();
        int index = 0;
        ArrayList<String> components = new ArrayList<String>();
        map.put("request", String.valueOf(message.charAt(0)));
        components.add("request");
        switch (message.charAt(0)) {
            case '1':
                components.add("name");
                components.add("dscp");
                components.add("closeauction");
                components.add("price");
                components.add("time");
                break;
            case '3':
                components.add("id");
                break;
            case '4':
                components.add("id");
                components.add("price");
                break;
            case '5':
                components.add("id");
                break;
            case '6':
                components.add("id");
                break;
        }
        for (String component : components) {
            map.put(component, message.substring(index, index + packetSizes.get(component)).trim());
            index += packetSizes.get(component);
        }
        return map;
    }

    //Learta
    public char[] getMessage() {
        return message;
    }
}