package com.redhat.iot.simulator.data;

import lombok.Data;

@Data
public class Sensor {
	Integer id;
	double mean;
	double variance;
	int emittime;
}
