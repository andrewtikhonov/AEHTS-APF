package processor.data;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 13/09/2012
 * Time: 12:33
 * To change this template use File | Settings | File Templates.
 */
public class StatusRecord {

    public StatusRecord(Integer priority, boolean showstopper){
        this.priority = priority;
        this.showstopper = showstopper;
    }

    public Integer priority;
    public boolean showstopper;
}
