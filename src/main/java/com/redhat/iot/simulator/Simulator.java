package com.redhat.iot.simulator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.redhat.iot.simulator.data.Device;
import com.redhat.iot.simulator.data.Sensor;
import com.redhat.iot.simulator.device.DeviceSim;

public class Simulator {

	public static void main(String[] args) {
		
		/*ObjectMapper mapper = new ObjectMapper();
		try {
			
			Device devices = new Device();
			
			devices.setDevices(5);
			devices.setTimeoffset(false);
			
			Sensor led = new Sensor();
			led.setId(0);
			led.setName("LED");
			led.setMin(0.0f);
			led.setMax(5.0f);
			led.setOffset(0);
			led.setEmittime(100);
			
			Sensor heat = new Sensor();
			heat.setId(0);
			heat.setName("LED");
			heat.setMin(0.0f);
			heat.setMax(100.0f);
			heat.setOffset(0);
			heat.setEmittime(100);
			
			devices.getSensors().add(led);
			devices.getSensors().add(heat);
			
			mapper.writeValue(new File("d:/temp/config.json"), devices);
			Device devices = mapper.readValue("d:/temp/config.json", Device.class);
			System.out.println(devices);
			
			
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		Gson gson = new Gson();
		try {
			String filename;
			if(args.length == 1){
				filename = (String) args[0];
			}
			else{
				filename="d:/temp/config.json";
			}
			JsonReader reader = new JsonReader(new FileReader(filename));
			Device devices = gson.fromJson(reader, Device.class);
			System.out.println(devices);
			ExecutorService executor = Executors.newFixedThreadPool(devices.getDevices());
			long start = new Date().getTime();
			
			for (int x=0; x < devices.getDevices(); x++) {
				List<Sensor> sensors = Lists.newArrayList();
				for (Sensor s: devices.getSensors()) {
					Sensor sensor = new Sensor();
					sensor.setEmittime(s.getEmittime());
					sensor.setId(s.getId());
					sensor.setMean(s.getMean());
					sensor.setVariance(s.getVariance());
					sensors.add(s);
				}
				System.out.println("Creating Device: " + x);
				DeviceSim device = new DeviceSim(x, devices.getName(), sensors, devices.isTimeoffset(),devices.getRuntime());
				System.out.println("Starting Thread: " + x);
				executor.execute(device);
			}
			
			long dur = new Date().getTime() - start;
			System.out.println(dur);
			while (dur < devices.getRuntime()) {
				try {
					System.out.println("Sleeping in mainthread for 10 seconds");
					Thread.sleep(10000);
					dur = new Date().getTime() - start;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("Shutdown all Threads");
			executor.shutdownNow();		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
