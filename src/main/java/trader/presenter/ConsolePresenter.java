package trader.presenter;

import trader.responder.Response;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsolePresenter implements Presenter {

    private String lastMessage;

    public ConsolePresenter() {
        lastMessage = "";
    }

    @Override
    public void execute(Response response){
        printLastMessage(response.getBody().toString());
//        printLastMessage(String.format("%s %s", getHeader(), response.getBody().toString()));

    }

    private void printLastMessage(String response) {
        if(!response.equalsIgnoreCase(lastMessage)){
            lastMessage = response;
            System.out.println(String.format("%s %s", getHeader(), lastMessage));
        }
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
        printLastMessage(output.toString());

    }

    private void setParts(StringBuilder output, String arg) {
        if(isStringInteger(arg)){
            if(Integer.parseInt(arg) == 1)
                output.append("Full");
            else
                output.append("1/").append(arg);
        } else {
            output.append("[").append(arg).append("]");
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
