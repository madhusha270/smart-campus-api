package com.smartcampus.store;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public final class DataStore {

    private static final DataStore INSTANCE = new DataStore();

    public static DataStore getInstance() {
        return INSTANCE;
    }

    private DataStore() {
        
    }

    private final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Sensor> sensors = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, List<SensorReading>> sensorReadings =
            new ConcurrentHashMap<>();


    public Collection<Room> getAllRooms() {
        return rooms.values();
    }

    public Room getRoom(String id) {
        return rooms.get(id);
    }

    public void addRoom(Room room) {
        rooms.put(room.getId(), room);
    }


    public Room removeRoom(String id) {
        return rooms.remove(id);
    }

    public boolean roomExists(String id) {
        return rooms.containsKey(id);
    }

    public Collection<Sensor> getAllSensors() {
        return sensors.values();
    }

    public Sensor getSensor(String id) {
        return sensors.get(id);
    }

    public void addSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
    }

    public Sensor removeSensor(String id) {
        return sensors.remove(id);
    }

    public boolean sensorExists(String id) {
        return sensors.containsKey(id);
    }

    public List<Sensor> getSensorsByRoom(String roomId) {
        List<Sensor> result = new ArrayList<>();
        for (Sensor sensor : sensors.values()) {
            if (roomId.equals(sensor.getRoomId())) {
                result.add(sensor);
            }
        }
        return result;
    }

    public List<Sensor> getSensorsByType(String type) {
        List<Sensor> result = new ArrayList<>();
        for (Sensor sensor : sensors.values()) {
            if (type.equalsIgnoreCase(sensor.getType())) {
                result.add(sensor);
            }
        }
        return result;
    }

    public List<SensorReading> getReadings(String sensorId) {
        return sensorReadings.getOrDefault(sensorId, new ArrayList<>());
    }

    public void addReading(String sensorId, SensorReading reading) {

        sensorReadings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);

        if (sensors.containsKey(sensorId)) {
            sensors.get(sensorId).setCurrentValue(reading.getValue());
        }
    }

    public void removeReadings(String sensorId) {
        sensorReadings.remove(sensorId);
    }
}
