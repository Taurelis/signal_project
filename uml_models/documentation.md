# UML Models Documentation

## 1. Alert Generation System

This diagram shows how the system detects dangerous patient conditions and sends alerts to medical staff. The main class is `AlertGenerator`, which goes through a patient's recent data and checks it against a list of `AlertCondition` objects. Each `AlertCondition` holds one rule, like "heart rate above 130", and knows how to check if a value breaks that rule.

If a condition is broken, `AlertGenerator` creates an `Alert` with the patient ID, what went wrong, the time, and how serious it is. The alert is then given to `AlertManager`, which tracks all current alerts and contacts `MedicalStaff` when needed.

Every patient has their own list of conditions, so thresholds can be different per patient. I used composition between `Patient` and `AlertCondition` because those conditions only belong to one patient and wouldn't exist on their own. `AlertManager` uses aggregation for alerts since an alert can exist before the manager picks it up.

I decided to keep alert creation in `AlertGenerator` and alert delivery in `AlertManager` separately. This way if you wanted to change how staff get notified later, you only have to change `AlertManager` and nothing else breaks.

---

## 2. Data Storage System

This diagram covers how patient data gets saved and retrieved. I used an interface for `DataStorage` so it can have different implementations, for example one that stores data in memory and another that might use a real database. `InMemoryDataStorage` is the current implementation and uses a map to keep patients organized by ID.

Each `Patient` object holds a list of `PatientRecord` objects, where one record is a single measurement with a type, value and timestamp. I used composition here because records don't really mean anything without the patient they belong to.

`DataRetriever` is a separate class that gives medical staff more specific ways to query data, like getting only the newest reading of a certain type. It also checks with `AccessControl` before giving out any data, since in a hospital not everyone should be able to see everything.

`DataDeletionPolicy` handles removing old records after a set number of days. I put this in its own class so the storage class doesn't get too complicated and so the deletion rules can be changed without touching the storage code.

---

## 3. Patient Identification System

This diagram models how incoming data gets matched to the right patient. `PatientIdentifier` is the class that takes a patient ID from an incoming record and tries to find the matching `HospitalPatient` in the system.

`HospitalPatient` stores the actual patient details like name, date of birth, medical history and assigned doctor. All of these are private fields because that kind of sensitive information shouldn't be accessible from places that don't need it. You can only get it through the getter methods.

`IdentityManager` keeps the full patient registry as a map. It handles adding new patients, looking them up and removing them. I kept this separate from `PatientIdentifier` so that managing the registry and doing the matching are two different jobs, which keeps the code cleaner.

If an ID doesn't match anything, `PatientIdentifier` doesn't just crash or ignore it. Instead it calls `IdentityMismatchHandler`, which logs what happened and marks it for someone to check manually. Handling it properly matters because linking data to the wrong patient in a hospital could cause real problems.

---

## 4. Data Access Layer

This diagram shows how the system receives data from the outside. The data generator can send data through TCP, WebSocket, or plain files, so I modeled a `DataListener` interface that all three types of listeners implement. Each listener handles its own connection details internally, so the rest of the system doesn't need to care about where the data came from.

When data arrives, the listener passes the raw string to `DataSourceAdapter`, which uses `DataParser` to convert it into a `ParsedRecord` with proper fields like patient ID, value, type and timestamp. The adapter then sends it on to storage.

`DataParser` supports both CSV and JSON. It figures out which format it received and calls the right internal method. `ParsedRecord` is basically just a container that holds the parsed values so they can be passed around easily.

The main reason I used an interface for `DataListener` is so that all three listener types can be swapped out or replaced without any changes needed in `DataSourceAdapter`. It also makes it easier to test because you could write a fake listener that just feeds in test data without needing an actual network connection.
