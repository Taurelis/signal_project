# Code Coverage Report

## Screenshot

See the attached screenshot of the JaCoCo report (target/site/jacoco/index.html).

## What was tested

I wrote tests for the four main classes I implemented this week:

- `AlertGenerator` (in `com.alerts`) – tested all 7 alert types including edge cases like only one condition being low, small changes that shouldn't trigger anything, etc.
- `DataStorage` – tested adding records, getting them back, filtering by time range, and what happens with an unknown patient ID
- `Patient` – tested getRecords with different time ranges, boundary timestamps, empty case
- `FileDataReader` – tested reading normal records, saturation with a % sign, alert triggered/resolved values, malformed lines being skipped, and multiple patients in one file

## What wasn't tested and why

The classes in the `com.cardio_generator` package don't have unit tests. This includes things like `HealthDataSimulator`, `BloodSaturationDataGenerator`, `BloodPressureDataGenerator`, `ECGDataGenerator`, `FileOutputStrategy`, `TcpOutputStrategy` and a few others.

The reason I didn't test these is that they weren't part of the week 3 requirements. They were already in the project from before and they basically just generate random numbers and write them somewhere. To test `TcpOutputStrategy` for example you would need an actual network connection running, which doesn't make sense for a unit test. Same with `HealthDataSimulator` – it runs a whole scheduled loop with threads and you can't really just call it in a test.

The `Main.java` dispatcher also isn't tested since it just calls other main methods based on a command line argument, there's not really logic in there to verify.
