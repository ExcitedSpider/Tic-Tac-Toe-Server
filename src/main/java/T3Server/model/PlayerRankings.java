package T3Server.model;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class PlayerRankings {

    private Lock lock = new ReentrantLock();
    private final Map<String, Integer> userWinningNumber;
    private Map<String, Integer> userRankings;

    public PlayerRankings() {
        userWinningNumber = new HashMap<>();
        this.updateUserRankings();
    }

    private final static List<String> Csvheaders = Arrays.asList("Player", "Winning");

    public static void save(PlayerRankings rankings, String filepath) throws IOException {
        Writer writer = new FileWriter(filepath);
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);

        csvPrinter.printRecord(Csvheaders);
        for (Map.Entry<String, Integer> entry : new ArrayList<>(rankings.userWinningNumber.entrySet())) {
            List<? extends Serializable> serializables = Arrays.asList(entry.getKey(), entry.getValue());
            csvPrinter.printRecord(serializables);
        }

        csvPrinter.close();
        writer.close();
    }

    public static PlayerRankings load(String filepath) throws IOException {
        Reader in = new FileReader(filepath);

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(Csvheaders.get(0), Csvheaders.get(1))
                .setSkipHeaderRecord(true)
                .build();

        Iterable<CSVRecord> records = csvFormat.parse(in);

        var rankings = new HashMap<String, Integer>();

        for (var csvRecord :records) {
            rankings.put(csvRecord.get(0), Integer.parseInt(csvRecord.get(1)));
        }

        in.close();

        return new PlayerRankings(rankings);
    }

    public PlayerRankings(Map<String, Integer> userWinningNumber) {
        this.userWinningNumber = userWinningNumber;
        this.updateUserRankings();
    }

    // TODO: optimize performance
    private void updateUserRankings() {
        lock.lock();
        var entryList = new LinkedList<>(userWinningNumber.entrySet());
        entryList.sort((a, b) -> {
            if(a.getValue().equals(b.getValue())) {
                return b.getKey().compareTo(a.getKey());
            }
            return a.getValue().compareTo(b.getValue());
        });
        Collections.reverse(entryList);

        var rankings = new HashMap<String, Integer>();
        for (int i = 0; i < entryList.size(); i++) {
            rankings.put(entryList.get(i).getKey(), i);
        }
        this.userRankings = rankings;
        lock.unlock();
    }

    public void modifyUserMarks(String player, int Delta) {
        if(!userWinningNumber.containsKey(player)) {
            userWinningNumber.put(player, Delta);
        }else {
            userWinningNumber.put(
                    player,
                    userWinningNumber.get(player) + Delta
            );
        }
        this.updateUserRankings();
    }


    public Integer getUserRanking(String player) {
        if(userRankings.containsKey(player)) {
            return userRankings.get(player);
        }
        return null;
    }
}
