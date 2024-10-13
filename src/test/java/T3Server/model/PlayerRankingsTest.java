package T3Server.model;

import org.junit.jupiter.api.Test;

import java.io.IOException;

/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */class PlayerRankingsTest {
    @Test
    void increaseWiningRecord() {
        var rankings = new PlayerRankings();
        rankings.modifyUserMarks("Chew", 10);
        rankings.modifyUserMarks("QE", 15);
        rankings.modifyUserMarks("F",5);

        assert rankings.getUserRanking("QE") == 0;
        assert rankings.getUserRanking("Chew") == 1;
        assert rankings.getUserRanking("F") == 2;

    }

    @Test void lexicographic() {
        var rankings = new PlayerRankings();

        rankings.modifyUserMarks("Chew", 5);
        rankings.modifyUserMarks("QE", 5);
        rankings.modifyUserMarks("F",5);

        assert rankings.getUserRanking("Chew") == 0;
        assert rankings.getUserRanking("F") == 1;
        assert rankings.getUserRanking("QE") == 2;
    }
    @Test void saveThenLoad() throws IOException{
        var rankings = new PlayerRankings();
        rankings.modifyUserMarks("QE",5);
        rankings.modifyUserMarks("QE",5);
        rankings.modifyUserMarks("QE",5);
        rankings.modifyUserMarks("Chew",5);
        rankings.modifyUserMarks("Chew",5);
        rankings.modifyUserMarks("F",5);

        PlayerRankings.save(rankings, "./rankings.csv");
        var loadedRankings = PlayerRankings.load("./rankings.csv");

        assert loadedRankings.getUserRanking("QE") == 0;
        assert loadedRankings.getUserRanking("Chew") == 1;
        assert loadedRankings.getUserRanking("F") == 2;
    }
}