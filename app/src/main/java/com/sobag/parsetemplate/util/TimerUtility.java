package com.sobag.parsetemplate.util;

import android.os.Handler;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Stopwatch...
 */
public class TimerUtility
{
    // ------------------------------------------------------------------------
    // members
    // ------------------------------------------------------------------------

    private TimerTask timerTask;
    private int seconds = 0;

    // ------------------------------------------------------------------------
    // public usage
    // ------------------------------------------------------------------------

    public void startTimer(final TextView view)
    {
        final Handler handler = new Handler();
        Timer ourtimer = new Timer();
        timerTask = new TimerTask()
        {
            public void run() {
                handler.post(new Runnable()
                {
                    public void run()
                    {
                        int hr = seconds / 3600;
                        int rem = seconds % 3600;
                        int mn = rem / 60;
                        int sec = rem % 60;

                        StringBuffer timerString = new StringBuffer();

                        // hours
                        if (hr == 0)
                        {
                            timerString.append("00:");
                        } else if (hr < 10)
                        {
                            timerString.append("0" +hr +":");
                        }
                        else
                        {
                            timerString.append(hr+":");
                        }

                        // minutes
                        if (mn == 0)
                        {
                            timerString.append("00:");
                        } else if (mn < 10)
                        {
                            timerString.append("0" +mn+":");
                        }
                        else
                        {
                            timerString.append(mn+":");
                        }

                        // seconds
                        if (sec == 0)
                        {
                            timerString.append("00");
                        } else if (sec < 10)
                        {
                            timerString.append("0" +sec);
                        }
                        else
                        {
                            timerString.append(sec);
                        }

                        view.setText(timerString.toString());
                        seconds++;
                    }
                });
            }};


        ourtimer.schedule(timerTask, 0, 1000);
    }

    public void stopTimer()
    {
        timerTask.cancel();
        timerTask=null;
        seconds=0;
    }

    public void pauseTimer()
    {
        timerTask.cancel();
        timerTask=null;
    }

    // ------------------------------------------------------------------------
    // GETTER & SETTER
    // ------------------------------------------------------------------------

    public int getSeconds()
    {
        return seconds;
    }

    public void setSeconds(int seconds)
    {
        this.seconds = seconds;
    }
}
