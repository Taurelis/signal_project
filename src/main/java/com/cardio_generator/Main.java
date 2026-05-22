package com.cardio_generator;

import com.data_management.DataStorage;

import java.io.IOException;

/**
 * Entry point that lets you run either the HealthDataSimulator or the DataStorage main method.
 * Pass "DataStorage" as the first argument to run DataStorage, otherwise the simulator runs.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length > 0 && args[0].equals("DataStorage")) {
            DataStorage.main(new String[]{});
        } else {
            HealthDataSimulator.main(args);
        }
    }
}
