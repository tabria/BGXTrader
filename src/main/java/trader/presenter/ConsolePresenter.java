package trader.presenter;

import com.oanda.v20.ExecuteException;
import trader.responder.Response;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsolePresenter implements Presenter {


    public ConsolePresenter() {
    }

    @Override
    public void execute(Response response){
        System.out.println(String.format("%s %s", getHeader(), response.getBody().toString()));
    }

    @Override
    public void execute(String... args) {
        StringBuilder output = new StringBuilder();
        if(args.length>1){
            setParts(output, args[0]);
            for (int i = 1; i <args.length ; i++) {
                output.append(" ").append(args[i]);
            }
        } else{
            output.append(args[0]);
        }

        System.out.println(String.format("%s %s", getHeader(), output.toString()));
    }

    private void setParts(StringBuilder output, String arg) {
        if(isStringInteger(arg)){
            if(Integer.parseInt(arg) == 1)
                output.append("Full");
            else
                output.append("1/").append(arg);
        }
    }

    private boolean isStringInteger(String str) {
       try{
           int i = Integer.parseInt(str);
           return true;
       } catch (Exception e){
           return false;
       }
    }

    private String getHeader(){
        long timeMillis = System.currentTimeMillis();
        Date date = new Date(timeMillis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("---- EEE MMM dd yyyy HH:mm:ss ----");
        return String.format("[TRADER] %s", simpleDateFormat.format(date));
    }
}
