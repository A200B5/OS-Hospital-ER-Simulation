# 🏥 Hospital Emergency Room Simulation

A multithreaded Java simulation of a busy hospital emergency room, built around the classic **Producer–Consumer problem** with a **Bounded Buffer** and hand-written **semaphores**.

Patients (producers) arrive and try to enter a limited waiting room. Doctors (consumers) take patients from it, compete for only **two treatment rooms**, and treat them concurrently, all without race conditions or deadlocks.

> **Course:** CS241 / SCS241 – Operating Systems, Fall 2026
> **Faculty of Computing and Artificial Intelligence, Cairo University**
> **Assignment 1:** Hospital Emergency Room Simulation

---

## Table of Contents

- [How It Works](#how-it-works)
- [Project Structure](#project-structure)
- [Synchronization Design](#synchronization-design)
- [Getting Started](#getting-started)
- [Sample Run](#sample-run)
- [Configuration](#configuration)
- [Known Limitations](#known-limitations)
- [Team](#team)

---

## How It Works

1. A patient arrives and tries to enter the waiting room.
2. If the waiting room is **full**, the patient waits outside until a seat is free.
3. An idle doctor takes the next patient from the waiting room and is assigned to them.
4. The doctor tries to acquire one of the **2 treatment rooms** within **3 seconds**.
   - **Acquired:** treatment starts and takes a random amount of time. The doctor then releases the room, the patient leaves, and the doctor becomes available again.
   - **Not acquired in time:** the doctor sends the patient back to the waiting room and becomes available for another patient.
5. When every patient has been treated, the doctors are shut down and the simulation ends.

Every step is printed to the console: arrival, entering the waiting room, waiting outside, doctor assignment, room acquisition, treatment, sending a patient back, finishing, and leaving.

```
                   ┌──────────────────────┐        ┌────────────────────┐
  Patients ──────► │ Waiting Room         │ ─────► │ Doctors            │
  (producers)      │ bounded buffer       │        │ (consumers)        │
                   │ capacity = N (input) │        │ D threads (input)  │
                   └──────────────────────┘        └─────────┬──────────┘
                              ▲                              │ tryP(3000 ms)
                              │ not acquired in time         ▼
                              └───────────────────  ┌──────────────────┐
                                  send patient back │ Treatment Rooms  │
                                                    │ semaphore = 2    │
                                                    └──────────────────┘
```

## Project Structure

```
OS-Hospital-ER-Simulation/
└── src/
    ├── Hospital.java        # Entry point: reads input, creates threads, handles shutdown
    ├── Patient.java         # Patient thread (producer)
    ├── Doctor.java          # Doctor thread (consumer)
    ├── Semaphore.java       # Custom counting semaphore with a timed acquire
    ├── WaitingRoom.java     # Bounded buffer shared resource
    └── TreatmentRoom.java   # Treatment rooms shared resource
```

| Class | Responsibility |
|---|---|
| `Hospital` | Reads the waiting room capacity, number of doctors and number of patients. Creates the shared resources, doctors and patients, starts all threads, and shuts the simulation down cleanly. |
| `Patient` | A thread that arrives after a random delay, enters the waiting room (or waits outside if it is full), then blocks until its treatment is finished and leaves. |
| `Doctor` | A thread that loops: take a patient, get assigned, try to acquire a treatment room within 3 s, then either treat the patient or send them back. |
| `Semaphore` | Counting semaphore with `P()`, `V()` and the custom `tryP(timeout)`, which returns `false` if no resource becomes available in time. |
| `WaitingRoom` | The bounded buffer. It exposes `produce()` for patients and `consume()` for doctors. |
| `TreatmentRoom` | Wraps a semaphore initialised to 2. It exposes `acquire(timeout)` and `release()`. |

## Synchronization Design

The waiting room and the treatment rooms are **separate shared resources**, each synchronized independently.

### Semaphore

`Semaphore` is implemented with Java monitors (`synchronized`, `wait`, `notify`):

| Method | Behavior |
|---|---|
| `P()` | Blocks until the value is positive, then decrements it. |
| `V()` | Increments the value and wakes a waiting thread. |
| `tryP(long timeout)` | Waits up to `timeout` ms. Returns `true` if the resource was acquired, `false` if the time ran out. `tryP(0)` acts as a non-blocking check. |

### Waiting Room (bounded buffer)

A circular array guarded by two semaphores:

- `spaces` starts at the room's **capacity** and counts free seats.
- `elements` starts at **0** and counts waiting patients.

`produce()` acquires a space, writes at `inptr` inside a `synchronized` block, then signals `elements`. `consume()` acquires an element, reads at `outptr` inside a `synchronized` block, then signals `spaces`. If `tryP(0)` on `spaces` fails, the patient is reported as waiting outside and blocks on `spaces.P()` until a seat frees up. Buffer index updates are always done inside `synchronized`, so concurrent producers and consumers cannot corrupt the buffer.

### Treatment Rooms

A counting semaphore initialised to **2**. A doctor calls `tryP(3000)`. If a room is not available within 3 seconds, the doctor gives up and returns the patient to the waiting room.

### Why there are no deadlocks

- A doctor only holds a treatment room while treating a patient, and releases it before doing anything else. A room holder never waits for another resource, so no circular wait can form.
- A doctor that fails to get a room holds **no** room while sending the patient back, so it can never block the doctors who do hold one.
- Rooms are always released after treatment, so waiting doctors eventually get one or time out and retry.

### Clean shutdown

After all patient threads finish, `Hospital` puts one `null` "poison pill" into the waiting room per doctor. Each doctor that receives `null` prints that it is leaving and stops, then `Hospital` joins all doctor threads.

## Getting Started

### Requirements

- Java JDK 8 or newer

### Build and run

From the project root:

```bash
# Compile
javac -d out src/*.java

# Run
java -cp out Hospital
```

You will be prompted for three values:

```
Enter waiting room capacity: 2
Enter number of doctors: 3
Enter number of patients: 5
```

You can also open the folder in IntelliJ IDEA (the `.iml` file is included) and run `Hospital`.

## Sample Run

Input: capacity `2`, doctors `3`, patients `5`. This is a trimmed excerpt of a real run (`[...]` marks omitted lines). Thread timing is random, so your output will differ.

```
Patients arriving:
P1 P2 P3 P4 P5

P1 arrived.
P1 entered the waiting room.
P1 was removed from the waiting room.
Doctor 1 is assigned to P1.
Doctor 1 is trying to acquire a treatment room.
Doctor 1 acquired a treatment room.
Doctor 1 begins treating P1.
[...]
Doctor 3 is trying to acquire a treatment room.
Doctor 3 could not acquire a treatment room.
Doctor 3 is sending P4 back to the waiting room.
Waiting room is full.
[...]
Doctor 1 finished treating P1.
Doctor 1 released the treatment room.
P1 left.
Doctor 1 is now available.
[...]
All patients have been treated.
Doctor 1 is leaving.
Doctor 3 is leaving.
Doctor 2 is leaving.
Simulation completed.
```

## Configuration

These values are constants in the source. Change them to experiment with the simulation:

| Setting | Current value | Where |
|---|---|---|
| Number of treatment rooms | `2` | `Hospital.java` – `new TreatmentRoom(2)` |
| Room acquire timeout | `3000` ms | `Doctor.java` – `treatmentRoom.acquire(3000)` |
| Treatment duration | `5000–7999` ms | `Doctor.java` – `5000 + random.nextInt(3000)` |
| Patient arrival delay | `500–2499` ms | `Patient.java` – `500 + Math.random() * 2000` |

## Known Limitations

- Input is assumed to be valid positive integers; there is no input validation.
- Treatment room count and all timings are hard-coded (see [Configuration](#configuration)).

## Team

| Name | ID |
|---|---|
| _Ahmed Bakr_ | _20250907_ |
| _Ahmed Emad_ | _20250916_ |
| _Mohamed Mahmoud_ | _20250915_ |
