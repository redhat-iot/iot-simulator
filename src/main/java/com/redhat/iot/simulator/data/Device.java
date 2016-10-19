package com.redhat.iot.simulator.data;

import java.util.List;

import com.google.common.collect.Lists;

import lombok.Data;
@Data
public class Device {
	List<Sensor> sensors = Lists.newArrayList();
	int devices;
	boolean timeoffset;
	String name;
	long runtime;
}
