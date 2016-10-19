package com.redhat.iot.simulator.device;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.redhat.iot.simulator.data.Sensor;
import com.redhat.iot.simulator.state.SensorState;

import lombok.Data;

@Data
public class DeviceSim implements Runnable{
	Integer id;
	String name;
	List<Sensor> sensors = Lists.newArrayList();
	List<SensorSim> sensorSim = Lists.newArrayList();
	Map<Integer, Double> sensorValue = Maps.newConcurrentMap();
	
	FileWriter fw = null;
	BlockingQueue<SensorState> queue = null;
	ExecutorService executor = null;
	
	long start = new Date().getTime();
	boolean offset;
	
	long endtime;

	public DeviceSim(int id, String name, List<Sensor> sensors, boolean offset, long endtime){
			this.id = new Integer(id);
			this.sensors = sensors;
			this.queue = new ArrayBlockingQueue<SensorState>(10);
			sensorValue = sensors.stream().collect(Collectors.toConcurrentMap(Sensor::getId, Sensor::getMean));
			this.offset = offset;
			this.endtime = endtime;
			
			sensors.forEach( s-> {
				
				/*
				sim.setEmittime(s.getEmittime());
				sim.setMean(s.getMean());
				sim.setVariance(s.getVariance());
				
				
				sim.setState(state);
				*/
				
				SensorState state = new SensorState();
				state.setCurrent(s.getMean());
				state.setId(s.getId());
				SensorSim sim = new SensorSim(s.getEmittime(), s.getMean(), s.getVariance(), state, queue, s.getId(), id);
				sensorSim.add(sim);
				
			});
			
			try {
				fw = new FileWriter(name+id+".csv");
				writeHeaders(fw, offset, sensors);
				writeLine(fw, offset, getOffsetTime(start), sensors, sensorValue);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			this.executor = Executors.newFixedThreadPool(sensors.size());
	}
	
	
	public void run() {
		
		if (null != fw && null != queue) {
			sensorSim.forEach(s -> {
				System.out.println("\tStarting sensor");
				executor.execute(s);
			});
			while (getOffsetTime(start) < endtime) {
				try {
					System.out.println("\tChecking for sensor data");
					SensorState state = queue.take();
					if (sensorValue.containsKey(state.getId())) {
						System.out.println("\tUpdateing sensor data");
						sensorValue.put(state.getId(), state.getCurrent());
						writeLine(fw, offset, getOffsetTime(start), sensors, sensorValue);
					}
				} catch (InterruptedException e) {
					System.out.println("\t Thread Interuppted Device:" + id );
					try {
						fw.flush();
						fw.close();
						executor.shutdownNow();
					} catch (IOException io) {
						// TODO Auto-generated catch block
						io.printStackTrace();
					}
					//e.printStackTrace();
				} finally {
				}
			}
		}
	}
	
	private void writeHeaders(FileWriter fw, boolean offset, List<Sensor> sensors ){
		try {
			System.out.println("\tWriting Headers");
			if (offset) {			
					fw.append("offset");	
			}
			int cnt = 0;
			for (Sensor s: sensors) {
				String val= "";
				if (offset) {
					val = ",sensor" + s.getId();
				} else if (cnt > 0){
					val = ",sensor" + s.getId();
				} else {
					val = "sensor" + s.getId();
				}
				fw.append(val);
				cnt++;
			}
			fw.append("\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void writeLine(FileWriter fw, boolean offset, long off, List<Sensor> sensors, Map<Integer, Double> sensorValue) {
		try {
			System.out.println("\tWriting Output");
			if (offset) {
				
					fw.append(String.valueOf(off));
			} 
			int cnt = 0; 
			for (Sensor s : sensors) {
				String val= "";
				String data = new DecimalFormat("#.##").format(sensorValue.get(s.getId()));
				if (offset) {
					val = "," + data;
				} else if (cnt > 0){
					val = "," + data;
				} else {
					val = data;
				}
				fw.append(val);
				cnt++;
			}
			fw.append("\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private long getOffsetTime (long start) {
		return new Date().getTime() - start;
	}
	
}
