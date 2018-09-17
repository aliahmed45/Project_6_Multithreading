/* MULTITHREADING <MyClass.java>
 * EE422C Project 6 submission by
 * Replace <...> with your actual data.
 * Ali Ahmed
 * ama4943
 * 15455
 * Slip days used: <0>
 * Spring 2018
 */

// Insert header here
package assignment6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.Thread;

public class BookingClient {
  /*
   * @param office maps box office id to number of customers in line
   * @param theater the theater where the show is playing
   */

    private Map<String, Integer> office; // = new HashMap<>();
    private Theater theater;

    public BookingClient(Map<String, Integer> office, Theater theaterA) {
        // TODO: Implement this constructor
        this.office = office;
        this.theater = theaterA;
    }

    /*
     * Starts the box office simulation by creating (and starting) threads
     * for each box office to sell tickets for the given theater
     *
     * @return list of threads used in the simulation,
     *         should have as many threads as there are box offices
     */
    public List<Thread> simulate() {
        //TODO: Implement this method
        List<Thread> threadList = new ArrayList<>();
        int threadCount = 0;
        for (String bxOffice : office.keySet()) {
            Theater.BoxOffice th1 = theater.new BoxOffice(bxOffice, office.get(bxOffice), threadCount);
            Thread th0 = new Thread(th1);
            threadList.add(th0);
            threadCount++;
        }

        for (Thread e : threadList) {
            e.start();
        }

        for (int i = 0; i < threadList.size(); i++) {
            try {
                threadList.get(i).join();
                //System.out.println("Joining Thread "+i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return threadList;
    }


    public static void main(String[] args) {
        Map<String, Integer> officeMain = new HashMap<>();
        officeMain.put("BX1", 400);
        officeMain.put("BX2", 600);
        /*
        officeMain.put("BX3",4);
        officeMain.put("BX4", 3);
        officeMain.put("BX5", 3);
        */
        //officeMain.put("BX6", 430);

        Theater theater = new Theater(500, 2, "Hello");

        //System.out.println(officeMain);
        BookingClient world = new BookingClient(officeMain, theater);
        world.simulate();


        //System.out.println("Finished");
    }
}
