/**
 *
 *  @author Basiak Patryk S30757
 *
 */

package zad1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ClientTask extends FutureTask<String> {
    private final Client client;
    private final List<String> list;
    private final boolean showRes;
    private ArrayList<String> replyList;

    public ClientTask(Client client0, List<String> list, boolean showRes) {
        super(() -> {
            client0.connect();
            client0.send("login " + client0.getName());
            List<String> replies = new ArrayList<>();
            for(String req : list) {
                String res = client0.send(req);
                if (showRes) System.out.println(res);
            }
            String clog = client0.send("bye and log transfer");
            replies.add(clog);
            return String.join(", ", replies);
        });
        this.client = client0;
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
