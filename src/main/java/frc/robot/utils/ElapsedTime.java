package frc.robot.utils;

/**
 * creates a timer class (basically)
 */
public final class ElapsedTime {
    private long time;

	public ElapsedTime() {
		time = 0;
		reset();
	}

	public void reset() {
		time = System.nanoTime();
	}

	public long getElapsedNano() {
		final long currTime = System.nanoTime();
		return currTime - time;
	}

	public long getElapsedMillis() {
		return getElapsedNano() / 1000000;
	}

	public double getElapsedSeconds() {
		return getElapsedNano() / 1000000000.0;
	}
}
