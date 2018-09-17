/* MULTITHREADING <MyClass.java>
 * EE422C Project 6 submission by
 * Replace <...> with your actual data.
 * Ali Ahmed
 * ama4943
 * 15455
 * Slip days used: <0>
 * Spring 2018
 */

package assignment6;// insert header here

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rows are set up
 * Calculate Seat Capacity?
 * What do I need to do?
 * (1) Set up theater
 * - Establish Name of Rows
 * - Create ArrayList of Seats?
 * -
 * - Next best available seat
 * <p>
 * (2) Run the threads
 */

public class Theater {

    private List<String> rowsList = new ArrayList<>();
    private List<Seat> seatList = Collections.synchronizedList(new ArrayList<>());
    private List<Ticket> ticketList = new ArrayList<>();
    private String show = new String();
    private int clientTot = 0;
    private Boolean flag = false;
    private Object flagLock = new Object();

//---------------------------------------------------------------------------------------------------------
    /**
     * Represents a seat in the theater
     * A1, A2, A3, ... B1, B2, B3 ...
     */
    class Seat {
        private String rowName;
        private int rowNum;
        private int seatNum;

        public Seat() {
            this.rowName = "A";
            this.rowNum = 0;
            this.seatNum = 0;
        }

        public Seat(int rowNum, int seatNum) {
            this.rowName = Theater.this.rowsList.get(rowNum);
            this.rowNum = rowNum;
            this.seatNum = seatNum;
        }

        /**
         * public Seat(String rowName, int rowNum, int seatNum) {
         * this.rowName = rowName;
         * this.rowNum = rowNum;
         * this.seatNum = seatNum;
         * }
         */

        public String getRowName() {
            return rowName;
        }

        public int getSeatNum() {
            return seatNum;
        }

        public int getRowNum() {
            return rowNum;
        }

        @Override
        public String toString() {
            // TODO: Implement this method to return the full Seat location ex: A1
            return (rowName + seatNum);
        }
    }
//---------------------------------------------------------------------------------------------------------

    /**
     * Represents a ticket purchased by a client
     */
    static class Ticket {
        private String show;
        private String boxOfficeId;
        private Seat seat;
        private int client;

        public Ticket(String show, String boxOfficeId, Seat seat, int client) {
            this.show = show;
            this.boxOfficeId = boxOfficeId;
            this.seat = seat;
            this.client = client;
        }

        public Seat getSeat() {
            return seat;
        }

        public String getShow() {
            return show;
        }

        public String getBoxOfficeId() {
            return boxOfficeId;
        }

        public int getClient() {
            return client;
        }

        @Override
        public String toString() {
            // TODO: Implement this method to return a string that resembles a ticket
            String ticket = new String();
            for (int i = 0; i < 31; i = i + 1) {
                ticket = ticket + '-';
            }
            ticket = ticket + '\n';

            int showOffset = 8 + show.length() + 1;
            ticket = ticket + "| Show: " + getShow();
            for (int i = 0; i < 31 - showOffset; i = i + 1) {
                ticket = ticket + ' ';
            }
            ticket = ticket + "|\n";

            int bxOffset = 17 + boxOfficeId.length() + 1;
            ticket = ticket + "| Box Office ID: " + getBoxOfficeId();
            for (int i = 0; i < 31 - bxOffset; i = i + 1) {
                ticket = ticket + ' ';
            }
            ticket = ticket + "|\n";

            int seatOffset = 8 + seat.toString().length() + 1;
            ticket = ticket + "| Seat: " + getSeat();
            for (int i = 0; i < 31 - seatOffset; i = i + 1) {
                ticket = ticket + ' ';
            }
            ticket = ticket + "|\n";
            Integer ina = client;
            //ina.toString()
            StringBuilder str = new StringBuilder(client);
            int clientOffset = 10 + ina.toString().length() + 1;
            ticket = ticket + "| Client: " + getClient();
            for (int i = 0; i < 31 - clientOffset; i = i + 1) {
                ticket = ticket + ' ';
            }
            ticket = ticket + "|\n";

            String bottomBorder = "";
            for (int i = 0; i < 31; i = i + 1) {
                bottomBorder = bottomBorder + "-";
            }
            ticket = ticket + bottomBorder;
            //ticket = ticket + "|\n";
            return ticket;
        }
    }
//---------------------------------------------------------------------------------------------------------

    /**
     * Implements a thread for each Box Office
     * - Stores tickets created by each Box Office in a ticket list
     */
    public class BoxOffice implements Runnable {
        private String boxID = new String();
        private int queue = 0;
        private int clientID = 0;
        private int threadID = 0;

        public BoxOffice(String boxID, int numCustomers, int threadID) {
            this.boxID = boxID;
            this.queue = numCustomers;
            clientID = Theater.this.clientTot;
            Theater.this.clientTot += numCustomers;
            this.threadID = threadID;
        }

        @Override
        public void run() {

            while (queue != 0 && !seatList.isEmpty()) {
/*
                if(seatList.isEmpty()){
                    if(flag == false) {
                        flag = true;
                        System.out.println("Sorry, we are sold out!");
                        //System.out.println("Thread: " + this.threadID + " Sorry, we are sold out!");
                    }
                    break;
                }
*/
                clientID += 1;
                //Seat seat = new Seat();
                synchronized (seatList) {
                    Seat seat = bestAvailableSeat();
                    if (seat == null) {
                        synchronized (flagLock) {
                            if (flag == false) {
                                flag = true;
                                System.out.println("Sorry, we are sold out!");
                            }
                        }
                        //System.out.println("Thread: " + this.threadID + " Sorry, we are sold out!");
                        break;
                    }
                    printTicket(boxID, seat, clientID);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //System.out.println("Thread:" + this.threadID + ", " + tick.getClient() + ":" + tick.getSeat());
                }
                queue = queue - 1;
            }

            if (seatList.isEmpty() && queue != 0) {
                synchronized (flagLock) {
                    if (flag == false) {
                        flag = true;
                        System.out.println("Sorry, we are sold out!");
                    }
                }
                //System.out.println("Thread: " + this.threadID + " Sorry, we are sold out!");
            }
        }
    }
//---------------------------------------------------------------------------------------------------------
    /**
     * class Sortbyroll implements Comparator<Ticket>
     * {
     * // Used for sorting in ascending order of
     * // roll number
     * public int compare(Ticket a, Ticket b)
     * {
     * Seat seatA = a.getSeat();
     * Seat seatB = b.getSeat();
     * <p>
     * seatA.getRowName().
     * return a.rollno - b.rollno;
     * }
     * }
     * <p>
     * class Sortbyname implements Comparator<Student>
     * {
     * // Used for sorting in ascending order of
     * // roll name
     * public int compare(Student a, Student b)
     * {
     * return a.name.compareTo(b.name);
     * }
     * }
     */
//---------------------------------------------------------------------------------------------------------
    public Theater(int numRows, int seatsPerRow, String show) {
        // TODO: Implement this constructor
        initRows(numRows);
        //System.out.println("Rows Initialized");
        initSeats(seatsPerRow);
        //System.out.println("Seats Initialized");
        this.show = show;
    }

    /**
     * Intializes the list of Rows with Row Names
     * <p>
     * Calls: changePrefix, incrementPrefix
     */
    private void initRows(int numRows) {
        rowsList = new ArrayList<>(numRows);
        int flag = 26;
        int counts = 1;
        String prefix = "";

        for (int i = 0; i < numRows; i = i + 1) {
            if (i == flag * counts) {
                prefix = changePrefix(prefix);
                counts = counts + 1;
            }
            int lastLetter = i % 26;
            Character cha = (char) (lastLetter + 65);
            String word = prefix + cha;
            rowsList.add(word);
        }
    }

    /**
     * Changes the prefix of the Row Name after
     * every 26 rows.
     * Two cases:
     * Case (1) - change a letter in the prefix
     * i.e. If the prefix is 'DE', DE->DF
     * Case (2) - add a letter to the prefix
     * i.e If the prefix is 'ZZ', ZZ->AAA
     *
     * @return Correct prefix for the current row
     */
    private String changePrefix(String prefix) {
        if (prefix.length() == 0) {
            prefix = "A";
        } else if (prefix.length() >= 1) {
            String Zs = "";
            for (int k = 0; k < prefix.length(); k = k + 1) {
                Zs = "Z" + Zs;
            }
            if (!prefix.equals(Zs)) {
                //Character cha = (char) (prefix.charAt(0) + 1);
                //prefix = cha.toString();
                prefix = incrementPrefix(prefix);
            } else {
                int formerLength = prefix.length();
                prefix = "";
                for (int i = 0; i <= formerLength; i++) {
                    prefix = 'A' + prefix;
                }
            }
        }
        return prefix;
    }

    /**
     * Increments the prefix of the Row Name after
     * every 26 rows.
     * Two cases:
     * Case (1) - simply increment last letter
     * i.e. If the prefix is 'ZY', ZY->ZZ
     * Case (2) - if last letter is Z, properly
     * update the prefix
     * i.e If the prefix is 'AZZ', AZZ->BAA
     *
     * @return Correct prefix for the current row
     */
    private String incrementPrefix(String prefix) {
        if (prefix == "AZ") {
            System.out.println("HOLD UP ERROR");
        }
        int length = prefix.length() - 1;
        int i = 0;
        if (prefix.charAt(length) == 'Z') {
            while (prefix.charAt(length - i) == 'Z') {
                i = i + 1;
            }
            char[] prefixArr = prefix.toCharArray();
            prefixArr[length - i] = (char) (prefix.charAt(length - i) + 1);
            for (int j = 1; j <= i; j = j + 1) {
                prefixArr[length - i + j] = 'A';
            }
            prefix = new String(prefixArr);
        } else {
            char[] prefixArr = prefix.toCharArray();
            prefixArr[length] = (char) (prefix.charAt(length) + 1);
            prefix = new String(prefixArr);
        }
        return prefix;
    }

    /**
     * Initialize the seats in each row
     */
    private void initSeats(int seatsPerRow) {
        //System.out.println(rowsList.size());
        for (int rowNum = 0; rowNum < rowsList.size(); rowNum = rowNum + 1) {
            for (int seatNum = 1; seatNum <= seatsPerRow; seatNum = seatNum + 1) {
                seatList.add(new Seat(rowNum, seatNum));
            }
        }
    }
//---------------------------------------------------------------------------------------------------------
    /**
     * Calculates the best seat not yet reserved
     *
     * @return the best seat or null if theater is full
     */
    public synchronized Seat bestAvailableSeat() {
        //TODO: Implement this method
        if (seatList.isEmpty()){
            synchronized (flagLock) {
                if (flag == false) {
                    flag = true;
                    System.out.println("Sorry, we are sold out!");
                }
            }
            return null;}
        Seat tmp = seatList.remove(0);
        return tmp;
    }

    /**
     * Prints a ticket for the client after they reserve a seat
     * Also prints the ticket to the console
     *
     * @param seat a particular seat in the theater
     * @return a ticket or null if a box office failed to reserve the seat
     */
    public synchronized Ticket printTicket(String boxOfficeId, Seat seat, int client) {
        //TODO: Implement this method
        Ticket tmp = new Ticket(show, boxOfficeId, seat, client);
        ticketList.add(tmp);
        System.out.println(tmp);
        return tmp;
    }

    /**
     * Lists all tickets sold for this theater in order of purchase
     *
     * @return list of tickets sold
     */
    public List<Ticket> getTransactionLog() {
        //System.out.println("Called Transaction Log");
        //TODO: Implement this method
        for (Ticket e : ticketList) {
            System.out.println(e);
        }
        return ticketList;
    }
//---------------------------------------------------------------------------------------------------------
}
