package proyectohabitos.example.neita.habitos.Task;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//representa una fila de una tarea en los listview de la tareas
public class LstTask {
    private Integer idTask;
    private String name;
    private Long reminder;
    private ArrayList<Boolean> days;
    private Integer chrono;
    private boolean isDone;

    public LstTask(Integer idTask, String name, Long reminder, ArrayList<Boolean> days, Integer chrono, boolean isDone) {
        this.idTask = idTask;
        this.name = name;
        this.reminder = reminder;
        this.days = days;
        this.chrono = chrono;
        this.isDone = isDone;
    }

    public String getName() {
        return name;
    }

    public Integer getIdTask() {
        return idTask;
    }

    public Long getReminder() {
        return reminder;
    }

    public ArrayList<Boolean> getDays() {
        return days;
    }

    public Integer getChrono() {
        return chrono;
    }

    public boolean isDone() {
        return isDone;
    }

    public String getTextReminder() {
        SimpleDateFormat f = new SimpleDateFormat("hh:mm a");
        return (this.reminder == 0 ? "" : f.format(new Date(this.reminder)) + "");
    }

    public String getTextChrono() {
        return this.chrono == null ? "" : this.chrono / 60 + " h " + this.chrono % 60 + " m ";
    }
}
