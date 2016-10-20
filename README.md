## Synopsis

This project allows a user to generate files that simulate IOT devices with one or more sensors. 

## Motivation

One of the issues with testing demoing, or building applications in the IOT space is the ability to have multiple devices and sensors. This application uses Gaussian random number generator to allow a user to generate CSV file(s) with numbers generated. 

## Overview

The application is a multi-threaded application using Java threads and Executor. The main application will start a thread for each device and in turn each device will create a thread for each sensor. 
The Device has a queue that will continue to loop until the runtime is reached or it is Interrupted. Once it is interrupted it will flush the files and close them. Each pass of the loop will check a BlockingQuueu for any state messages from a sensor. If a message is received then the Map of sensor values is updated and a newline of data is sent to the CSV file for the device.  
The Sensors will go to sleep for the emittime and when they wake up they will generate a new Gaussian randmo number, update its State and put a State message on the BlockingQueue.

## Installation

The application is a simple command line application that has the ability to read a JSON config file to emulate devices. 
A sample file looks like:

```javascript
{
	"sensors" : [{
			"id" : 0,
		  "mean" : 100.0,
			"variance" : 5.0,
			"emittime" : 1000
		}, {
			"id" : 1,
			"mean" : 250.0,
			"variance" : 25.0,
			"emittime" : 1500
		}
	],
	"devices" : 2,
	"timeoffset" : false,
  "name" : "Simulator", 
  "runtime" : 60000
}
```
some key values:
  sensor
    mean: the mean value for the sensor to be simulated
    variance: the maximum change allowed between sensor readings
    emittime:  rough time in milliseconds of when the sensor will create a new readings in milliseconds

  devices:
    timeoffset: boolean value to determine if the CSV files will include an offset time
    runtime: how long the application will run to generate the file(s) in milliseconds
    
## License

Apache License 2.0
