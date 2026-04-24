package com.smartcampus.exception;

public class RoomNotEmptyException extends RuntimeException {

    private final String roomId;
    private final int sensorCount;

  
    public RoomNotEmptyException(String roomId, int sensorCount) {
        super("Cannot delete room '" + roomId + "': "
              + sensorCount + " sensor(s) still assigned.");
        this.roomId = roomId;
        this.sensorCount = sensorCount;
    }

    public String getRoomId() {
        return roomId;
    }

    public int getSensorCount() {
        return sensorCount;
    }
}
