/**
 *
 *  @author Basiak Patryk S30757
 *
 */

package zad1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class ClientTask extends FutureTask<String> {
    private final Client client;
    private final List<String> list;
    private final boolean showRes;
    private ArrayList<String> replyList;

    public ClientTask(Client client, List<String> list, boolean showRes) {
        super(() -> {
            client.connect();
            client.send("login " + client.getName());
            List<String> replies = new ArrayList<>();
            for(String req : list) {
                String res = client.send(req);
                replies.add(res);
                if (showRes) System.out.println(res);
            }
            String clog = client.send("bye and log transfer");
            replies.add(clog);
            return String.join(", ", replies);
        });
        this.client = client;
        this.list = list;
        this.showRes = showRes;
        replyList = new ArrayList<>();
    }

    public static ClientTask create(Client c, List<String> reqList, boolean showRes) {
        return new ClientTask(c, reqList, showRes);
    }

    @Override
    protected void done() {
        super.done();
        try {
            replyList.add(get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
