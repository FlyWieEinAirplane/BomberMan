package de.flywieeinairplane.bomberman.server;

import de.flywieeinairplane.bomberman.Bomb;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by flywieeinairplane on 25.03.17.
 */
public class DelayedBomb implements Delayed {
    private final long delayTime;
    private long expireTimeMillis;
    public final Bomb bomb;
    public int x;
    public int y;

    public DelayedBomb(int x, int y, long delayTime, Bomb bomb){
        this.x = x;
        this.y = y;
        this.delayTime=delayTime;
        this.expireTimeMillis = System.currentTimeMillis()+delayTime;
        this.bomb = bomb;
    }
    public DelayedBomb(float x, float y, long delayTime, Bomb bomb){
        this.x = (int)x;
        this.y = (int)y;
        this.delayTime=delayTime;
        this.expireTimeMillis = System.currentTimeMillis()+delayTime;
        this.bomb = bomb;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long delayMillis = expireTimeMillis-System.currentTimeMillis();
        return unit.convert(delayMillis,TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        long diffMillis = getDelay(TimeUnit.MILLISECONDS)-o.getDelay(TimeUnit.MILLISECONDS);
        diffMillis = Math.min(diffMillis,1);
        diffMillis = Math.max(diffMillis,-1);
        return (int) diffMillis;
    }

    public void explodeNow() {
        this.expireTimeMillis = System.currentTimeMillis();
    }
}
