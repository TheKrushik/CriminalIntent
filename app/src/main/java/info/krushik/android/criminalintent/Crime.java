package info.krushik.android.criminalintent;


import java.sql.Time;
import java.util.Date;
import java.util.UUID;

public class Crime {

    private UUID mId;
    private String mTitle;
    private Date mDate; //дата преступления
    private Date mTime; // время преступления
    private boolean mSolved; //было ли преступление раскрыто

    public Crime() {
// Генерирование уникального идентификатора
        this(UUID.randomUUID()); // возвращать объект Crime с соответствующим значением UUID
//        mId = UUID.randomUUID(); ->db
//        mDate = new Date();
//        mTime = new Date();
    }

    public Crime(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public Date getTime() {
        return mTime;
    }

    public void setTime(Date time) {
        mTime = time;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }
}
