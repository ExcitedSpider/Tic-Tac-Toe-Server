package T3Server;

import T3Server.Logger.Logger;
import T3Server.controller.GamesManager;
import T3Server.controller.PlayersManager;
import T3Server.controller.TimedCleaner;
import T3Server.model.PlayerRankings;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class T3RemoteServer {

    static String RANKING_FILE = "./rankings.csv";

    public static PlayerRankings loadOrCreateRankings() {
        File file = new File(RANKING_FILE);
        if(file.exists()) {
            try {
                var ranking = PlayerRankings.load(RANKING_FILE);
                Logger.getInstance().logInfo("Load ranking from storage");
                return ranking;
            } catch (IOException e) {
                Logger.getInstance().logErr("Ranking file not found. Create a new one");
                return new PlayerRankings();
            }
        }else {
            Logger.getInstance().logErr("Ranking file not found. Create a new one");
            return new PlayerRankings();
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("""
                    Invalid arguments: lack of basic arguments;
                    Example:
                    java -jar server.jar 127.0.0.1 8899
                    """);
            return;
        }
        var ip = args[0];
        var port = args[1];
        try {
            var playersManager = new PlayersManager();
            var gamesManager = new GamesManager();
            TimedCleaner timedCleaner = new TimedCleaner(gamesManager, playersManager);
            timedCleaner.start();

            var rankings = loadOrCreateRankings();

            ServerRemoteImplement server = new ServerRemoteImplement(
                    playersManager,
                    gamesManager,
                    rankings
            );

            Registry registry;

            if(ip.equals("127.0.0.1") || ip.equals("localhost")) {
                registry = LocateRegistry.createRegistry(Integer.parseInt(port));
            } else {
                // handle remote registry
                // if not found, let the server crashes as it should
                registry = LocateRegistry.getRegistry(ip, Integer.parseInt(port));
            }

            registry.rebind("Game", server);
            Logger.getInstance().logInfo("Game Controller Bound!");
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    PlayerRankings.save(rankings, RANKING_FILE);
                    Logger.getInstance().logInfo("Save ranking file to " + RANKING_FILE);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));
        } catch (RemoteException e) {
            Logger.getInstance().logErr("Launch Server Failed:" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
