package proyectohabitos.example.neita.habitos.Task;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/*
Clase que representa a las filas de la lista de tareas
 */
public class LstTask {
    private Integer idTask;
    private String name;
    private Long reminder;
    private ArrayList<String> days;
    private Integer chrono;
    private boolean isDone;

    public LstTask(Integer idTask, String name, Long reminder, ArrayList<String> days, Integer chrono, boolean isDone) {
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

    public ArrayList<String> getDays() {
        return days;
    }

    public Integer getChrono() {
        return chrono;
    }

    public boolean isDone() {
        return isDone;
    }

    public String getTextDays() {
        String text = "";
        for (int i = 0; i < days.size(); i++) {
            text += days.get(i);
            if (i != days.size() - 1) {
                text += ", ";
            }
        }
        return text;
    }

    public String getTextReminder() {
        SimpleDateFormat f = new SimpleDateFormat("hh:mm a");
        return (this.reminder == 0 ? "" : f.format(new Date(this.reminder)) + "");
    }

    public String getTextChrono() {
        return this.chrono == null ? "" : this.chrono / 60 + " h " + this.chrono % 60 + " m ";
    }
}
