package trader.presenter;

import trader.responder.Response;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsolePresenter implements Presenter {

    private PrintWriter printWriter;

    public ConsolePresenter() {
        this.printWriter = new PrintWriter(System.out);
    }

    @Override
    public void execute(Response response){
        String text = "ds";
        printWriter.println(getHeader()+text);
        printWriter.close();
    }

    private String getHeader(){
        long timeMillis = System.currentTimeMillis();
        Date date = new Date(timeMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(" --- EEE MMM dd yyyy HH:mm:ss --- ");
        return String.format("[TRADER] %s ", simpleDateFormat.format(date));
    }
}
