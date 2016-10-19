package com.redhat.iot.simulator.device;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

import com.redhat.iot.simulator.state.SensorState;

import lombok.Data;

@Data
public class SensorSim implements Runnable{
	private SensorState state;
	private int emittime;
	private Double mean;
	private Double variance;
	private BlockingQueue<SensorState> queue;
	private Random fRandom = new Random();
	private int id;
	private int device;
	
	
	public SensorSim(int emittime, Double mean, Double variance,
					 SensorState state,BlockingQueue<SensorState> queue, int id, int device) {
		this.emittime = emittime;
		this.mean = mean;
		this.variance = variance;
		this.queue = queue;
		this.state = state;
		this.id = id;
		this.device = device;
	}
	
	@Override
	public void run() {
		boolean interrupt = false;
		while (!interrupt) {
			System.out.println("\t\t" + this.toString());
			try {
				System.out.println("\t\t Sleeping Sim");
				Thread.sleep(emittime);
				System.out.println("\t\t Getting new value and putting on queue");
				state.setCurrent(getGaussian(mean, variance));
				queue.put(state);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println("\t\t Thread Interuppted Device:" + device + " sensor: " + id );
				interrupt = true;
			}
		}
	}
	
	private double getGaussian(double aMean, double aVariance){
	    return aMean + fRandom.nextGaussian() * aVariance;
	}
}
